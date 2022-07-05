package com.primeton.commom.exception;

import com.primeton.commom.vo.CodeMsg;

/**
 * 参数异常
 */
public class IllegalArgException extends GlobalException{

    public IllegalArgException() {
    }

    public IllegalArgException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
