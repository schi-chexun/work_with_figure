package com.company.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.project.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户设置Mapper
 */
@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {

}
