package com.sparta.blushmarket.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StatusMsgResponseDto {

    private String statusMsg;
    private Integer statusCode;

    @Builder
    public StatusMsgResponseDto(String statusMsg, Integer statusCode) {
        this.statusMsg = statusMsg;
        this.statusCode = statusCode;
    }
}
