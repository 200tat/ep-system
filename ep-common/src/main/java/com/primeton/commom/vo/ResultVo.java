package com.primeton.commom.vo;


import java.io.Serializable;
import java.util.List;

public class ResultVo<T> implements Serializable {
    /*
    此类为封装返回给前端的封装类
    */
    private Integer code = 200; //状态码 200 为成功 500为失败
    private String msg = "success"; //对于返回内容的描述
    private T obj; //封装附带的对象
    private List<T> list; //封装附带的集合

    public ResultVo(Integer code, String msg, T obj, List<T> list) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
        this.list = list;
    }

    public ResultVo(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVo() {
    }

    public ResultVo(T obj) {
        this.obj = obj;
    }

    public ResultVo(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ResultVo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", obj=" + obj +
                ", list=" + list +
                '}';
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

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
