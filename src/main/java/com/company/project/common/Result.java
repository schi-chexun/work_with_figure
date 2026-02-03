package com.company.project.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @author Your Name
 * @date 2026-01-31
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 构造方法私有化
     */
    private Result() {
    }

    /**
     * 构造方法
     *
     * @param code 响应码
     * @param msg  响应消息
     * @param data 响应数据
     */
    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回（无数据）
     *
     * @return Result
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /**
     * 成功返回（有数据）
     *
     * @param data 数据
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /**
     * 成功返回（自定义消息）
     *
     * @param msg  消息
     * @param data 数据
     * @return Result
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败返回
     *
     * @return Result
     */
    public static <T> Result<T> fail() {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMsg(), null);
    }

    /**
     * 失败返回（自定义消息）
     *
     * @param msg 消息
     * @return Result
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(ResultCode.ERROR.getCode(), msg, null);
    }

    /**
     * 失败返回（自定义码和消息）
     *
     * @param code 响应码
     * @param msg  消息
     * @return Result
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 根据ResultCode返回
     *
     * @param resultCode 结果码枚举
     * @return Result
     */
    public static <T> Result<T> result(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    /**
     * 根据ResultCode返回（带数据）
     *
     * @param resultCode 结果码枚举
     * @param data       数据
     * @return Result
     */
    public static <T> Result<T> result(ResultCode resultCode, T data) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), data);
    }

}
