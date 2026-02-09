package com.company.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.project.entity.PomodoroSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * 番茄钟会话Mapper
 */
@Mapper
public interface PomodoroSessionMapper extends BaseMapper<PomodoroSession> {

}
