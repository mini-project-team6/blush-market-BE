package com.sparta.blushmarket.exception;

import com.sparta.blushmarket.entity.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{

    private final ExceptionEnum exceptionEnum;

}
