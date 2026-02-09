package com.company.project.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户设置实体类
 */
@Data
@TableName("user_settings")
public class UserSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前主题ID
     */
    private Long themeId;

    // ========== 番茄钟设置 ==========

    /**
     * 工作时长(分钟)
     */
    private Integer workDuration;

    /**
     * 短休息时长(分钟)
     */
    private Integer shortBreak;

    /**
     * 长休息时长(分钟)
     */
    private Integer longBreak;

    /**
     * 几个番茄后长休息
     */
    private Integer longBreakInterval;

    // ========== 提醒设置 ==========

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
    private Integer waterInterval;

    /**
     * 工作过久提醒（0-关，1-开）
     */
    private Integer overworkReminder;

    /**
     * 过久阈值(分钟)
     */
    private Integer overworkThreshold;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

}
