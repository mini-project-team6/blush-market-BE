package com.sparta.blushmarket.dto;

import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.entity.enumclass.SellState;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String username;
    private String title;
    private String content;
    private String image;
    private SellState sellState;

    private boolean likes;



    @Builder
    private PostResponseDto(boolean likes, Post post) {
        this.id = post.getId();
        this.username =post.getMember().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.sellState = post.getSellState();
        this.likes = likes;

    }

    public static PostResponseDto from(boolean likes, Post post) {
        return PostResponseDto.builder()
                .post(post)
                .likes(likes)

                .build();

    }
}