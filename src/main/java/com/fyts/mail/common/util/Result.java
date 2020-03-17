package com.fyts.mail.common.util;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 页面响应entity
 */
@Builder
@AllArgsConstructor
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    // @ApiModelProperty("业务状态码")
    private int code;
    // @ApiModelProperty("状态描述")
    private String message;
    // @ApiModelProperty("返回数据体")
    private Object data;
    // @ApiModelProperty("返回扩展数据")
    private Map<String, Object> extras;

    private static final int DEFAULT_SUCCESS_CODE = 200;
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";
    private static final int DEFAULT_FAILURE_CODE = 400;
    private static final String DEFAULT_FAILURE_MESSAGE = "FAILURE";

    public Result() { }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return this.data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public Map<String, Object> getExtras() {
        return this.extras;
    }

    public Result setExtras(Map<String, Object> extras) {
        if (null == this.extras) {
            this.extras = extras;
        } else {
            this.extras.putAll(extras);
        }
        return this;
    }


    public Result put(String key, Object value) {
        if (this.extras == null) {
            this.extras = new HashMap();
        }
        this.extras.put(key, value);
        return this;
    }

    /**
     * 快捷方式--成功
     */
    public static Result ok() {
        return new Result(DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE);
    }

    public static Result ok(String message) {
        return new Result(DEFAULT_SUCCESS_CODE, message);
    }

    public static Result ok(String message, Object data) {
        return new Result(DEFAULT_SUCCESS_CODE, message).setData(data);
    }

    public static Result ok(int code, String message, Object data) {
        return new Result(code, message).setData(data);
    }

    /**
     * 快捷方式--失败
     */
    public static Result error() {
        return new Result(DEFAULT_FAILURE_CODE, DEFAULT_FAILURE_MESSAGE);
    }

    public static Result error(String message) {
        return new Result(DEFAULT_FAILURE_CODE, message);
    }

    public static Result error(String message, Object data) {
        return new Result(DEFAULT_FAILURE_CODE, message).setData(data);
    }

    public static Result error(int code, String message, Object data) {
        return new Result(code, message).setData(data);
    }

}