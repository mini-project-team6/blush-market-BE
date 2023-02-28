package com.sparta.blushmarket.dto;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@Getter
public class SignupRequestDto {
    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9\\\\d`~!@#$%^&*()-_=+]{8,}$", message = "패스워드에러")
    private String password;

    @Pattern(regexp = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$",message = "이메일형식불일치")
    private String email;
}
