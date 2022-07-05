package com.primeton.commom.exception;

import com.primeton.commom.vo.CodeMsg;

/**
 * 令牌异常
 */
public class IllegalTokenException extends GlobalException{

    public IllegalTokenException() {
    }

    public IllegalTokenException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
