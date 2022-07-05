package com.primeton.commom.vo;


public enum CodeMsg {
    /*SUCESS(200,"sucess"),
    SERVER_ERROR(500,"服务端异常"),
    Request_Error(404,"请求异常")*/



    SUCCESS(200,"success"),
    TOKEN_OVERTIME(403,"令牌已过期"),
    USER_NOT_FOUND(405,"用户不存在"),
    DATA_NOT_FOUND(404,"未查询到数据"),
    ILLEGAL_ARG(400,"传参有误"),
    NULL_ARG(400,"必需参数为空"),
    OPERATION_FAIL(410,"操作失败"),
    NO_PERMISSION(401,"没有权限");

    private Integer code;
    private String msg;
    CodeMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
