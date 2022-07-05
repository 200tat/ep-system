package com.primeton.commom.exception;

import com.primeton.commom.vo.CodeMsg;

/**
 * 操作失败异常
 */
public class OperationException extends GlobalException{

    public OperationException() {
    }

    public OperationException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
