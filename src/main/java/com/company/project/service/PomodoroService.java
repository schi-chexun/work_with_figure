package com.company.project.service;

import com.company.project.dto.WelcomeDTO;
import com.company.project.entity.PomodoroSession;
import com.company.project.entity.WorkLog;

/**
 * 番茄钟服务接口
 */
public interface PomodoroService {

    /**
     * 获取欢迎信息（开启新一局时调用）
     */
    WelcomeDTO getWelcome(Long userId);

    /**
     * 开始新的会话（开一局）
     */
    PomodoroSession startSession(Long userId);

    /**
     * 获取当前进行中的会话
     */
    PomodoroSession getCurrentSession(Long userId);

    /**
     * 结束会话
     */
    void endSession(Long userId, Long sessionId, boolean completed);

    /**
     * 开始一个番茄钟（工作阶段）
     */
    WorkLog startPomodoro(Long userId, Long sessionId, Integer plannedSeconds);

    /**
     * 完成一个番茄钟
     */
    void completePomodoro(Long userId, Long workLogId, Integer actualSeconds);

    /**
     * 开始休息
     */
    WorkLog startBreak(Long userId, Long sessionId, Integer type, Integer plannedSeconds);

    /**
     * 完成休息
     */
    void completeBreak(Long userId, Long workLogId, Integer actualSeconds);

    /**
     * 放弃当前番茄钟/休息
     */
    void abandonCurrent(Long userId, Long workLogId, Integer actualSeconds);

}
