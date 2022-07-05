package com.primeton.commom.exception;


import com.primeton.commom.vo.CodeMsg;
import lombok.Data;

@Data
public class GlobalException extends RuntimeException{

    private CodeMsg codeMsg;

    public GlobalException() {
        super();
    }

    public GlobalException(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }


}
