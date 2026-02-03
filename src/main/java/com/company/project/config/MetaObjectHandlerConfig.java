package com.company.project.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus字段自动填充配置
 * 用于自动填充BaseEntity中的公共字段
 *
 * @author Your Name
 * @date 2026-01-31
 */
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);

        // TODO: 创建人和更新人需要从当前登录用户获取，暂时填充为null
        // this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
        // this.strictInsertFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());

        // TODO: 更新人需要从当前登录用户获取，暂时填充为null
        // this.strictUpdateFill(metaObject, "updateBy", Long.class, getCurrentUserId());
    }

}
