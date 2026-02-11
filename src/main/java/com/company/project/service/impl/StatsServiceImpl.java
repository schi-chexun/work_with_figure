package com.company.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.company.project.dto.StatsDTO;
import com.company.project.entity.DailyStats;
import com.company.project.entity.User;
import com.company.project.entity.UserSettings;
import com.company.project.mapper.DailyStatsMapper;
import com.company.project.service.StatsService;
import com.company.project.service.UserService;
import com.company.project.service.UserSettingsService;
import com.company.project.util.TimeFormatUtil;
import com.company.project.dto.KeyboardReportDTO;
import com.company.project.dto.RankItemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final DailyStatsMapper dailyStatsMapper;
    private final UserSettingsService userSettingsService;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_DAILY_RANK = "rank:keyboard:daily:"; // Redis Key前缀

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
        if (settings != null && settings.getOverworkReminder() == 1) {
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

    @Override
    public void reportKeyboardActivity(Long userId, KeyboardReportDTO report) {
        LocalDate today = LocalDate.now();
        String dateStr = today.toString();

        // 1. 更新 Redis 实时排行榜 (ZSET)
        String rankKey = KEY_DAILY_RANK + dateStr;
        if (report.getTaps() != null && report.getTaps() > 0) {
             redisTemplate.opsForZSet().incrementScore(rankKey, userId.toString(), report.getTaps());
             redisTemplate.expire(rankKey, java.time.Duration.ofDays(3));
        }

        // 2. 更新 MySQL 数据库
        DailyStats stats = getDailyStats(userId, today);
        if (stats == null) {
            stats = new DailyStats();
            stats.setUserId(userId);
            stats.setStatDate(today);
            stats.setTotalWorkSeconds(0);
            stats.setCompletedPomodoros(0);
            stats.setTotalSessions(0);
            stats.setTotalTaps(report.getTaps() != null ? report.getTaps() : 0);
            stats.setTotalHarvests(report.getHarvests() != null ? report.getHarvests() : 0);
            stats.setMostActiveKey(report.getHotKey());
            dailyStatsMapper.insert(stats);
        } else {
            UpdateWrapper<DailyStats> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", stats.getId());

            // 安全处理 null 值
            int taps = report.getTaps() != null ? report.getTaps() : 0;
            int harvests = report.getHarvests() != null ? report.getHarvests() : 0;

            String setSql = "total_taps = ifnull(total_taps, 0) + " + taps +
                          ", total_harvests = ifnull(total_harvests, 0) + " + harvests;

            if (report.getHotKey() != null && !report.getHotKey().isEmpty()) {
                setSql += ", most_active_key = '" + report.getHotKey() + "'";
            }

            updateWrapper.setSql(setSql);
            dailyStatsMapper.update(null, updateWrapper);
        }
    }

    @Override
    public List<RankItemDTO> getDailyKeyboardRank() {
        LocalDate today = LocalDate.now();
        String rankKey = KEY_DAILY_RANK + today.toString();

        // 获取前10名
        Set<ZSetOperations.TypedTuple<String>> range = redisTemplate.opsForZSet().reverseRangeWithScores(rankKey, 0, 9);

        if (range == null || range.isEmpty()) {
            return Collections.emptyList();
        }

        List<RankItemDTO> result = new ArrayList<>();
        int rank = 1;

        for (ZSetOperations.TypedTuple<String> tuple : range) {
            String userIdStr = tuple.getValue();
            Double score = tuple.getScore();
            if (userIdStr == null) continue;

            try {
                Long userId = Long.parseLong(userIdStr);
                User user = userService.getById(userId);
                String username = (user != null) ? user.getUsername() : "用户" + userId;
                result.add(new RankItemDTO(username, null, score != null ? score.intValue() : 0, rank++));
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return result;
    }
}
