package com.sparta.blushmarket.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class PostRequestDto {
    private String title;
    private String content;
    private String image;

    private MultipartFile file;

    private int sellState;

}
