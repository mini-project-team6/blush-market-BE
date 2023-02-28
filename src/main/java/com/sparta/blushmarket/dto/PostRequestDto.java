package com.sparta.blushmarket.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequestDto {
    private String title;
    private String content;
    private String image;
    private String originalFilename;

    private MultipartFile file;

    private int sellState;

}
