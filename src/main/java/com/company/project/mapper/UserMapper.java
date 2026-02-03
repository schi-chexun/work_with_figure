package com.company.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.project.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口（示例）
 * 继承MyBatis Plus的BaseMapper获取基础CRUD方法
 *
 * @author Your Name
 * @date 2026-01-31
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 这里可以定义自定义的SQL方法
    // 复杂查询建议在对应的XML文件中编写

}
