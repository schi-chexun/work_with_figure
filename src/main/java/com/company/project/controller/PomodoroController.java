package com.company.project.controller;

import com.company.project.common.Result;
import com.company.project.dto.WelcomeDTO;
import com.company.project.entity.PomodoroSession;
import com.company.project.entity.WorkLog;
import com.company.project.service.PomodoroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 番茄钟控制器
 */
@Tag(name = "番茄钟", description = "番茄钟核心功能接口")
@RestController
@RequestMapping("/pomodoro")
@RequiredArgsConstructor
public class PomodoroController {

    private final PomodoroService pomodoroService;

    @Operation(summary = "获取欢迎信息", description = "用户打开应用时调用，返回欢迎语和昨日统计")
    @GetMapping("/welcome")
    public Result<WelcomeDTO> getWelcome(@RequestAttribute("userId") Long userId) {
        WelcomeDTO welcome = pomodoroService.getWelcome(userId);
        return Result.success(welcome);
    }

    @Operation(summary = "开始新会话", description = "开始新的一局，限制每个用户同时只能有一局")
    @PostMapping("/session/start")
    public Result<PomodoroSession> startSession(@RequestAttribute("userId") Long userId) {
        PomodoroSession session = pomodoroService.startSession(userId);
        return Result.success(session);
    }

    @Operation(summary = "获取当前会话")
    @GetMapping("/session/current")
    public Result<PomodoroSession> getCurrentSession(@RequestAttribute("userId") Long userId) {
        PomodoroSession session = pomodoroService.getCurrentSession(userId);
        return Result.success(session);
    }

    @Operation(summary = "结束会话")
    @PostMapping("/session/{sessionId}/end")
    public Result<Void> endSession(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId,
            @RequestParam(defaultValue = "true") Boolean completed) {
        pomodoroService.endSession(userId, sessionId, completed);
        return Result.success();
    }

    @Operation(summary = "开始番茄钟（工作阶段）")
    @PostMapping("/session/{sessionId}/pomodoro/start")
    public Result<WorkLog> startPomodoro(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId,
            @RequestParam Integer plannedSeconds) {
        WorkLog workLog = pomodoroService.startPomodoro(userId, sessionId, plannedSeconds);
        return Result.success(workLog);
    }

    @Operation(summary = "完成番茄钟")
    @PostMapping("/pomodoro/{workLogId}/complete")
    public Result<Void> completePomodoro(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long workLogId,
            @RequestParam Integer actualSeconds) {
        pomodoroService.completePomodoro(userId, workLogId, actualSeconds);
        return Result.success();
    }

    @Operation(summary = "开始休息")
    @PostMapping("/session/{sessionId}/break/start")
    public Result<WorkLog> startBreak(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long sessionId,
            @RequestParam Integer type,
            @RequestParam Integer plannedSeconds) {
        WorkLog workLog = pomodoroService.startBreak(userId, sessionId, type, plannedSeconds);
        return Result.success(workLog);
    }

    @Operation(summary = "完成休息")
    @PostMapping("/break/{workLogId}/complete")
    public Result<Void> completeBreak(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long workLogId,
            @RequestParam Integer actualSeconds) {
        pomodoroService.completeBreak(userId, workLogId, actualSeconds);
        return Result.success();
    }

    @Operation(summary = "放弃当前番茄钟/休息")
    @PostMapping("/worklog/{workLogId}/abandon")
    public Result<Void> abandonCurrent(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long workLogId,
            @RequestParam(defaultValue = "0") Integer actualSeconds) {
        pomodoroService.abandonCurrent(userId, workLogId, actualSeconds);
        return Result.success();
    }

}
