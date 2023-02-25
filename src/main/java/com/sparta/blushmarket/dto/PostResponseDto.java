package com.sparta.blushmarket.dto;

import com.sparta.blushmarket.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String image;


    private PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
    }

    public static PostResponseDto from(Post post) {
        return new PostResponseDto(post);

    }
}