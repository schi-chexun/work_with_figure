package com.company.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.project.entity.DailyStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 每日统计Mapper
 */
@Mapper
public interface DailyStatsMapper extends BaseMapper<DailyStats> {

    /**
     * 统计某日工作时长超过指定秒数的用户数量
     */
    @Select("SELECT COUNT(*) FROM daily_stats WHERE stat_date = #{date} AND total_work_seconds > #{seconds} AND deleted = 0")
    long countUsersWithMoreWorkThan(@Param("date") LocalDate date, @Param("seconds") int seconds);

    /**
     * 统计某日有记录的用户总数
     */
    @Select("SELECT COUNT(*) FROM daily_stats WHERE stat_date = #{date} AND deleted = 0")
    long countUsersOnDate(@Param("date") LocalDate date);

}
