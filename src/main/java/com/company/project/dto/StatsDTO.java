package com.company.project.dto;

import lombok.Data;

/**
 * 统计数据DTO
 */
@Data
public class StatsDTO {

    /**
     * 今日工作时长（秒）
     */
    private Integer todayWorkSeconds;

    /**
     * 今日工作时长格式化
     */
    private String todayWorkFormatted;

    /**
     * 今日完成番茄数
     */
    private Integer todayPomodoros;

    /**
     * 本周工作时长（秒）
     */
    private Integer weekWorkSeconds;

    /**
     * 本周工作时长格式化
     */
    private String weekWorkFormatted;

    /**
     * 本月工作时长（秒）
     */
    private Integer monthWorkSeconds;

    /**
     * 本月工作时长格式化
     */
    private String monthWorkFormatted;

    /**
     * 总工作时长（秒）
     */
    private Integer totalWorkSeconds;

    /**
     * 总工作时长格式化
     */
    private String totalWorkFormatted;

    /**
     * 总完成番茄数
     */
    private Integer totalPomodoros;

    /**
     * 是否需要提醒工作过久
     */
    private Boolean overworkWarning;

    /**
     * 过久提醒消息
     */
    private String overworkMessage;

}
