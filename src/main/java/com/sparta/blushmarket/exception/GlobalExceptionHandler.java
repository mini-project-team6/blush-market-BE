package com.sparta.blushmarket.exception;


import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ErrorResponse;
import com.sparta.blushmarket.common.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponseDto<ErrorResponse>> customException(CustomException e){

//        return ErrorResponse.toResponseEntity(e.getExceptionEnum());
        return ResponseEntity.status(e.getExceptionEnum().getCode())
                .body(ResponseUtils.error(
                        ErrorResponse.of(e.getExceptionEnum().getCode(),e.getExceptionEnum().getMsg())
                ));

    }

}
