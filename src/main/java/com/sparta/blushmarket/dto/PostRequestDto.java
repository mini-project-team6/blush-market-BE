package com.sparta.blushmarket.dto;

import com.sparta.blushmarket.entity.SellState;
import lombok.Getter;

@Getter
public class PostRequestDto {
    private String title;
    private String content;
    private String image;

    private int sellState;

}
