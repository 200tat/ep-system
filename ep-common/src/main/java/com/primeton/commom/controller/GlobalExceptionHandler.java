package com.primeton.commom.controller;

import com.primeton.commom.exception.GlobalException;
import com.primeton.commom.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Configuration
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value=Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {

        ResultVo<Object> resultVo = new ResultVo<>();
        if (e instanceof GlobalException){

            logger.error("-----------------------------------------------------");
            logger.error(e.getLocalizedMessage());
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement sta: stackTrace) {
                logger.error(sta.toString());
            }
            logger.error("-----------------------------------------------------");
            GlobalException ge = (GlobalException) e;
            resultVo.setCode(ge.getCodeMsg().getCode());
            resultVo.setMsg(ge.getCodeMsg().getMsg());
        }else {
            resultVo.setCode(500);
            resultVo.setMsg("服务器报错："+e.getMessage());
            e.printStackTrace();
        }
        return resultVo;
    }
}
