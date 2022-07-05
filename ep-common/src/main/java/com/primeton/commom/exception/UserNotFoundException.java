package com.primeton.commom.exception;


import com.primeton.commom.vo.CodeMsg;

/**
 * 用户不存在的自定义异常
 */
public class UserNotFoundException extends GlobalException{

    public UserNotFoundException() {}

    public UserNotFoundException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
