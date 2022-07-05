package com.primeton.commom.exception;


import com.primeton.commom.vo.CodeMsg;

/**
 * 无权异常
 */
public class PermissionException extends GlobalException{

    public PermissionException() {
    }

    public PermissionException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
