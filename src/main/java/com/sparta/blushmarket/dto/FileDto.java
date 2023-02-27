package com.sparta.blushmarket.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class FileDto {
    private String title;
    private String content;
    private MultipartFile file;

    private int sellState;



}
