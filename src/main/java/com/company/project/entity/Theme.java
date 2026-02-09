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
 * 主题实体类
 */
@Data
@TableName("theme")
public class Theme implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 主题名称
     */
    private String name;

    /**
     * 主题代码
     */
    private String code;

    /**
     * 主题描述
     */
    private String description;

    /**
     * 主题图标
     */
    private String icon;

    /**
     * 特效类型
     */
    private String effectType;

    /**
     * 是否默认主题
     */
    private Integer isDefault;

    /**
     * 状态（0-禁用，1-启用）
     */
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

}
