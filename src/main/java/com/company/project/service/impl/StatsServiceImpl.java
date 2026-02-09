package com.company.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.company.project.dto.StatsDTO;
import com.company.project.entity.DailyStats;
import com.company.project.entity.UserSettings;
import com.company.project.mapper.DailyStatsMapper;
import com.company.project.service.StatsService;
import com.company.project.service.UserSettingsService;
import com.company.project.util.TimeFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * 统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final DailyStatsMapper dailyStatsMapper;
    private final UserSettingsService userSettingsService;

    @Override
    public StatsDTO getOverview(Long userId) {
        StatsDTO dto = new StatsDTO();
        LocalDate today = LocalDate.now();

        // 今日统计
        DailyStats todayStats = getDailyStats(userId, today);
        int todaySeconds = todayStats != null ? todayStats.getTotalWorkSeconds() : 0;
        int todayPomodoros = todayStats != null ? todayStats.getCompletedPomodoros() : 0;

        dto.setTodayWorkSeconds(todaySeconds);
        dto.setTodayWorkFormatted(TimeFormatUtil.formatSeconds(todaySeconds));
        dto.setTodayPomodoros(todayPomodoros);

        // 本周统计
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<DailyStats> weekStats = getStatsInRange(userId, weekStart, today);
        int weekSeconds = weekStats.stream().mapToInt(DailyStats::getTotalWorkSeconds).sum();
        dto.setWeekWorkSeconds(weekSeconds);
        dto.setWeekWorkFormatted(TimeFormatUtil.formatSeconds(weekSeconds));

        // 本月统计
        LocalDate monthStart = today.withDayOfMonth(1);
        List<DailyStats> monthStats = getStatsInRange(userId, monthStart, today);
        int monthSeconds = monthStats.stream().mapToInt(DailyStats::getTotalWorkSeconds).sum();
        dto.setMonthWorkSeconds(monthSeconds);
        dto.setMonthWorkFormatted(TimeFormatUtil.formatSeconds(monthSeconds));

        // 总统计
        LambdaQueryWrapper<DailyStats> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(DailyStats::getUserId, userId);
        List<DailyStats> allStats = dailyStatsMapper.selectList(totalWrapper);
        int totalSeconds = allStats.stream().mapToInt(DailyStats::getTotalWorkSeconds).sum();
        int totalPomodoros = allStats.stream().mapToInt(DailyStats::getCompletedPomodoros).sum();

        dto.setTotalWorkSeconds(totalSeconds);
        dto.setTotalWorkFormatted(TimeFormatUtil.formatSeconds(totalSeconds));
        dto.setTotalPomodoros(totalPomodoros);

        // 检查是否工作过久
        UserSettings settings = userSettingsService.getByUserId(userId);
        if (settings.getOverworkReminder() == 1) {
            int thresholdSeconds = settings.getOverworkThreshold() * 60;
            if (todaySeconds >= thresholdSeconds) {
                dto.setOverworkWarning(true);
                dto.setOverworkMessage("你今天已经工作了" + TimeFormatUtil.formatSeconds(todaySeconds) +
                    "，超过了设定的" + settings.getOverworkThreshold() + "分钟阈值，注意休息哦！");
            } else {
                dto.setOverworkWarning(false);
            }
        } else {
            dto.setOverworkWarning(false);
        }

        return dto;
    }

    @Override
    public DailyStats getDailyStats(Long userId, LocalDate date) {
        LambdaQueryWrapper<DailyStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyStats::getUserId, userId)
               .eq(DailyStats::getStatDate, date);
        return dailyStatsMapper.selectOne(wrapper);
    }

    @Override
    public List<DailyStats> getStatsInRange(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<DailyStats> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyStats::getUserId, userId)
               .ge(DailyStats::getStatDate, startDate)
               .le(DailyStats::getStatDate, endDate)
               .orderByAsc(DailyStats::getStatDate);
        return dailyStatsMapper.selectList(wrapper);
    }

    @Override
    public void updateDailyStats(Long userId, LocalDate date, int workSeconds, int pomodoros) {
        DailyStats stats = getDailyStats(userId, date);

        if (stats == null) {
            // 创建新记录
            stats = new DailyStats();
            stats.setUserId(userId);
            stats.setStatDate(date);
            stats.setTotalWorkSeconds(workSeconds);
            stats.setTotalSessions(1);
            stats.setCompletedPomodoros(pomodoros);
            dailyStatsMapper.insert(stats);
        } else {
            // 更新现有记录
            stats.setTotalWorkSeconds(stats.getTotalWorkSeconds() + workSeconds);
            stats.setCompletedPomodoros(stats.getCompletedPomodoros() + pomodoros);
            dailyStatsMapper.updateById(stats);
        }
    }

    @Override
    public Double calculateBeatPercentage(Long userId, LocalDate date) {
        DailyStats userStats = getDailyStats(userId, date);
        if (userStats == null || userStats.getTotalWorkSeconds() == 0) {
            return 0.0;
        }

        int userSeconds = userStats.getTotalWorkSeconds();
        long totalUsers = dailyStatsMapper.countUsersOnDate(date);
        if (totalUsers <= 1) {
            return 100.0;
        }

        long beatenUsers = dailyStatsMapper.countUsersWithMoreWorkThan(date, userSeconds);
        // 被超越的用户数 = 总用户 - 比你工作更久的用户 - 你自己
        long usersYouBeat = totalUsers - beatenUsers - 1;

        return Math.round((double) usersYouBeat / (totalUsers - 1) * 10000) / 100.0;
    }

}
