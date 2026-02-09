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
 * 番茄钟会话实体类（每一局）
 */
@Data
@TableName("pomodoro_session")
public class PomodoroSession implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态常量
     */
    public static final int STATUS_IN_PROGRESS = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_ABANDONED = 2;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 使用的主题ID
     */
    private Long themeId;

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
     * 状态（0-进行中，1-已完成，2-已放弃）
     */
    private Integer status;

    /**
     * 总工作秒数
     */
    private Integer totalWorkSeconds;

    /**
     * 总休息秒数
     */
    private Integer totalBreakSeconds;

    /**
     * 完成的番茄数
     */
    private Integer pomodoroCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

}
