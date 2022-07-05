package com.primeton.commom.exception;

import com.primeton.commom.vo.CodeMsg;

/**
 * 数据为空异常
 */
public class DataNotFoundException extends GlobalException {
    public DataNotFoundException() {
    }

    public DataNotFoundException(CodeMsg codeMsg) {
        super(codeMsg);
    }
}
