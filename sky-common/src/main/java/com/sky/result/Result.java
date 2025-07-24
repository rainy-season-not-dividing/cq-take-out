package com.sky.result;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;   // 1为成功，0为失败
    private String msg;
    private T data;

    Result(){}

    public static<T> Result<T> success(){
        Result<T> result = new Result<>();
        result.code = 1;
        return result;
    }

    public static<T> Result<T> success(T object){
        Result<T> result = new Result<>();
        result.code = 1;
        result.data = object;
        return result;
    }

    public static<T> Result<T> success(String msg){
        Result<T> result = new Result<>();
        result.code = 1;
        result.msg = msg;
        return result;
    }

    public static<T> Result<T> error(String msg){
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = msg;
        return result;
    }


}
