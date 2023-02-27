package com.sparta.blushmarket.dto;

import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.entity.SellState;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String image;
    private SellState sellState;
    private List<CommentResponseDto> commentList;


    @Builder
    private PostResponseDto(Post post, List<CommentResponseDto> commentList) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.sellState = post.getSellState();
        this.commentList = commentList;
    }

    public static PostResponseDto from(Post post,List<CommentResponseDto>  commentList) {
        return PostResponseDto.builder()
                .post(post)
                .commentList(commentList)
                .build();

    }
}