package com.company.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.company.project.entity.WorkLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工作日志Mapper
 */
@Mapper
public interface WorkLogMapper extends BaseMapper<WorkLog> {

}
