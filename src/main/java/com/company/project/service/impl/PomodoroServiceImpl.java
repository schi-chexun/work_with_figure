package com.company.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.company.project.dto.WelcomeDTO;
import com.company.project.entity.DailyStats;
import com.company.project.entity.PomodoroSession;
import com.company.project.entity.User;
import com.company.project.entity.UserSettings;
import com.company.project.entity.WorkLog;
import com.company.project.mapper.PomodoroSessionMapper;
import com.company.project.mapper.UserMapper;
import com.company.project.mapper.WorkLogMapper;
import com.company.project.service.PomodoroService;
import com.company.project.service.StatsService;
import com.company.project.service.UserSettingsService;
import com.company.project.util.TimeFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 番茄钟服务实现
 */
@Service
@RequiredArgsConstructor
public class PomodoroServiceImpl implements PomodoroService {

    private final PomodoroSessionMapper sessionMapper;
    private final WorkLogMapper workLogMapper;
    private final UserMapper userMapper;
    private final UserSettingsService userSettingsService;
    private final StatsService statsService;
    private final StringRedisTemplate redisTemplate;

    private static final String SESSION_LOCK_KEY = "pomodoro:session:lock:";

    @Override
    public WelcomeDTO getWelcome(Long userId) {
        WelcomeDTO dto = new WelcomeDTO();

        // 欢迎语
        dto.setWelcomeMessage("欢迎回来，准备开始你牛马的一天吧！");

        // 昨日工作时长
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DailyStats yesterdayStats = statsService.getDailyStats(userId, yesterday);

        int yesterdaySeconds = yesterdayStats != null ? yesterdayStats.getTotalWorkSeconds() : 0;
        dto.setYesterdayWorkSeconds(yesterdaySeconds);
        dto.setYesterdayWorkFormatted(TimeFormatUtil.formatSeconds(yesterdaySeconds));

        // 超越百分比
        Double beatPercentage = statsService.calculateBeatPercentage(userId, yesterday);
        dto.setBeatPercentage(beatPercentage);

        // 检查是否有进行中的会话
        PomodoroSession activeSession = getCurrentSession(userId);
        dto.setHasActiveSession(activeSession != null);
        dto.setActiveSessionId(activeSession != null ? activeSession.getId() : null);

        return dto;
    }

    @Override
    @Transactional
    public PomodoroSession startSession(Long userId) {
        // 使用Redis锁防止重复开局
        String lockKey = SESSION_LOCK_KEY + userId;
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(locked)) {
            throw new RuntimeException("操作太频繁，请稍后再试");
        }

        try {
            // 检查是否已有进行中的会话
            User user = userMapper.selectById(userId);
            if (user.getCurrentSessionId() != null) {
                PomodoroSession existingSession = sessionMapper.selectById(user.getCurrentSessionId());
                if (existingSession != null && existingSession.getStatus() == PomodoroSession.STATUS_IN_PROGRESS) {
                    throw new RuntimeException("你已经有一局进行中，请先完成或放弃当前会话");
                }
            }

            // 获取用户设置
            UserSettings settings = userSettingsService.getByUserId(userId);

            // 创建新会话
            PomodoroSession session = new PomodoroSession();
            session.setUserId(userId);
            session.setThemeId(settings.getThemeId());
            session.setStartTime(LocalDateTime.now());
            session.setStatus(PomodoroSession.STATUS_IN_PROGRESS);
            session.setTotalWorkSeconds(0);
            session.setTotalBreakSeconds(0);
            session.setPomodoroCount(0);

            sessionMapper.insert(session);

            // 更新用户当前会话ID
            LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(User::getId, userId)
                         .set(User::getCurrentSessionId, session.getId());
            userMapper.update(null, updateWrapper);

            return session;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public PomodoroSession getCurrentSession(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getCurrentSessionId() == null) {
            return null;
        }

        PomodoroSession session = sessionMapper.selectById(user.getCurrentSessionId());
        if (session != null && session.getStatus() == PomodoroSession.STATUS_IN_PROGRESS) {
            return session;
        }
        return null;
    }

    @Override
    @Transactional
    public void endSession(Long userId, Long sessionId, boolean completed) {
        PomodoroSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在");
        }

        // 更新会话状态
        session.setEndTime(LocalDateTime.now());
        session.setStatus(completed ? PomodoroSession.STATUS_COMPLETED : PomodoroSession.STATUS_ABANDONED);
        sessionMapper.updateById(session);

        // 清除用户当前会话ID
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, userId)
                     .set(User::getCurrentSessionId, null);
        userMapper.update(null, updateWrapper);

        // 更新每日统计
        if (session.getTotalWorkSeconds() > 0) {
            statsService.updateDailyStats(
                userId,
                LocalDate.now(),
                session.getTotalWorkSeconds(),
                session.getPomodoroCount()
            );
        }
    }

    @Override
    @Transactional
    public WorkLog startPomodoro(Long userId, Long sessionId, Integer plannedSeconds) {
        // 验证会话
        PomodoroSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在");
        }
        if (session.getStatus() != PomodoroSession.STATUS_IN_PROGRESS) {
            throw new RuntimeException("会话已结束");
        }

        // 创建工作日志
        WorkLog workLog = new WorkLog();
        workLog.setUserId(userId);
        workLog.setSessionId(sessionId);
        workLog.setType(WorkLog.TYPE_WORK);
        workLog.setPlannedSeconds(plannedSeconds);
        workLog.setActualSeconds(0);
        workLog.setStartTime(LocalDateTime.now());
        workLog.setCompleted(0);

        workLogMapper.insert(workLog);
        return workLog;
    }

    @Override
    @Transactional
    public void completePomodoro(Long userId, Long workLogId, Integer actualSeconds) {
        WorkLog workLog = workLogMapper.selectById(workLogId);
        if (workLog == null || !workLog.getUserId().equals(userId)) {
            throw new RuntimeException("记录不存在");
        }

        // 更新工作日志
        workLog.setEndTime(LocalDateTime.now());
        workLog.setActualSeconds(actualSeconds);
        workLog.setCompleted(1);
        workLogMapper.updateById(workLog);

        // 更新会话统计
        PomodoroSession session = sessionMapper.selectById(workLog.getSessionId());
        session.setTotalWorkSeconds(session.getTotalWorkSeconds() + actualSeconds);
        session.setPomodoroCount(session.getPomodoroCount() + 1);
        sessionMapper.updateById(session);
    }

    @Override
    @Transactional
    public WorkLog startBreak(Long userId, Long sessionId, Integer type, Integer plannedSeconds) {
        // 验证会话
        PomodoroSession session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在");
        }

        // 创建休息日志
        WorkLog workLog = new WorkLog();
        workLog.setUserId(userId);
        workLog.setSessionId(sessionId);
        workLog.setType(type); // 1-短休息, 2-长休息
        workLog.setPlannedSeconds(plannedSeconds);
        workLog.setActualSeconds(0);
        workLog.setStartTime(LocalDateTime.now());
        workLog.setCompleted(0);

        workLogMapper.insert(workLog);
        return workLog;
    }

    @Override
    @Transactional
    public void completeBreak(Long userId, Long workLogId, Integer actualSeconds) {
        WorkLog workLog = workLogMapper.selectById(workLogId);
        if (workLog == null || !workLog.getUserId().equals(userId)) {
            throw new RuntimeException("记录不存在");
        }

        // 更新休息日志
        workLog.setEndTime(LocalDateTime.now());
        workLog.setActualSeconds(actualSeconds);
        workLog.setCompleted(1);
        workLogMapper.updateById(workLog);

        // 更新会话休息统计
        PomodoroSession session = sessionMapper.selectById(workLog.getSessionId());
        session.setTotalBreakSeconds(session.getTotalBreakSeconds() + actualSeconds);
        sessionMapper.updateById(session);
    }

    @Override
    @Transactional
    public void abandonCurrent(Long userId, Long workLogId, Integer actualSeconds) {
        WorkLog workLog = workLogMapper.selectById(workLogId);
        if (workLog == null || !workLog.getUserId().equals(userId)) {
            throw new RuntimeException("记录不存在");
        }

        // 更新日志（标记为未完成）
        workLog.setEndTime(LocalDateTime.now());
        workLog.setActualSeconds(actualSeconds);
        workLog.setCompleted(0);
        workLogMapper.updateById(workLog);

        // 如果是工作阶段，仍然记录实际工作时长
        if (workLog.getType() == WorkLog.TYPE_WORK && actualSeconds > 0) {
            PomodoroSession session = sessionMapper.selectById(workLog.getSessionId());
            session.setTotalWorkSeconds(session.getTotalWorkSeconds() + actualSeconds);
            sessionMapper.updateById(session);
        }
    }

}
