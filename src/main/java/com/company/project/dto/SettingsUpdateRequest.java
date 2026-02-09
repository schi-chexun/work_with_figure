package com.company.project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 用户设置更新请求DTO
 */
@Data
public class SettingsUpdateRequest {

    /**
     * 主题ID
     */
    private Long themeId;

    /**
     * 工作时长(分钟) 1-120
     */
    @Min(value = 1, message = "工作时长最少1分钟")
    @Max(value = 120, message = "工作时长最多120分钟")
    private Integer workDuration;

    /**
     * 短休息时长(分钟) 1-30
     */
    @Min(value = 1, message = "短休息最少1分钟")
    @Max(value = 30, message = "短休息最多30分钟")
    private Integer shortBreak;

    /**
     * 长休息时长(分钟) 5-60
     */
    @Min(value = 5, message = "长休息最少5分钟")
    @Max(value = 60, message = "长休息最多60分钟")
    private Integer longBreak;

    /**
     * 几个番茄后长休息 2-10
     */
    @Min(value = 2, message = "最少2个番茄后长休息")
    @Max(value = 10, message = "最多10个番茄后长休息")
    private Integer longBreakInterval;

    /**
     * 声音特效（0-关，1-开）
     */
    private Integer soundEnabled;

    /**
     * 喝水提醒（0-关，1-开）
     */
    private Integer waterReminder;

    /**
     * 喝水提醒间隔(分钟)
     */
    @Min(value = 10, message = "喝水提醒间隔最少10分钟")
    @Max(value = 120, message = "喝水提醒间隔最多120分钟")
    private Integer waterInterval;

    /**
     * 工作过久提醒（0-关，1-开）
     */
    private Integer overworkReminder;

    /**
     * 过久阈值(分钟)
     */
    @Min(value = 60, message = "过久阈值最少60分钟")
    @Max(value = 480, message = "过久阈值最多480分钟")
    private Integer overworkThreshold;

}
