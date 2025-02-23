package com.pangying.entity;

import lombok.Data;

/**
 * 通用响应类
 */
@Data
public class R {
    private int code; // 状态码
    private String message; // 返回消息
    private Object data; // 返回数据

    // 无参构造方法
    public R() {
    }

    // 全参构造方法
    public R(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应（无数据）
    public static R success() {
        return new R(1, "成功", null);
    }

    // 成功响应（带数据）
    public static R success(Object data) {
        return new R(1, "成功", data);
    }

    // 失败响应（自定义状态码和消息）
    public static R fail(int code, String message) {
        return new R(code, message, null);
    }

    // 失败响应（默认状态码和消息）
    public static R fail(String message) {
        return new R(0, message, null);
    }

}
