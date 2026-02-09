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
 * 工作日志实体类（每次专注/休息记录）
 */
@Data
@TableName("work_log")
public class WorkLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型常量
     */
    public static final int TYPE_WORK = 0;
    public static final int TYPE_SHORT_BREAK = 1;
    public static final int TYPE_LONG_BREAK = 2;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 所属会话ID
     */
    private Long sessionId;

    /**
     * 类型（0-工作，1-短休息，2-长休息）
     */
    private Integer type;

    /**
     * 计划时长(秒)
     */
    private Integer plannedSeconds;

    /**
     * 实际时长(秒)
     */
    private Integer actualSeconds;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;

    /**
     * 是否完成（0-否，1-是）
     */
    private Integer completed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;

}
