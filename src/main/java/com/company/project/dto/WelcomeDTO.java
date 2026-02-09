package com.company.project.dto;

import lombok.Data;

/**
 * 欢迎信息DTO - 用户开启新一局时返回
 */
@Data
public class WelcomeDTO {

    /**
     * 欢迎语
     */
    private String welcomeMessage;

    /**
     * 昨日工作时长（秒）
     */
    private Integer yesterdayWorkSeconds;

    /**
     * 昨日工作时长格式化（如：2小时30分钟）
     */
    private String yesterdayWorkFormatted;

    /**
     * 超越了多少百分比的用户
     */
    private Double beatPercentage;

    /**
     * 连续工作天数
     */
    private Integer streakDays;

    /**
     * 是否有进行中的会话
     */
    private Boolean hasActiveSession;

    /**
     * 进行中的会话ID
     */
    private Long activeSessionId;

}
