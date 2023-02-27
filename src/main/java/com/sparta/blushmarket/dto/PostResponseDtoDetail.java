package com.sparta.blushmarket.dto;

import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.entity.enumclass.SellState;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostResponseDtoDetail {
    private Long id;
    private String username;
    private String title;
    private String content;
    private String image;
    private SellState sellState;

    private boolean likes;
    private List<CommentResponseDto> commentList;


    @Builder
    private PostResponseDtoDetail(boolean likes, Post post, List<CommentResponseDto> commentList) {
        this.id = post.getId();
        this.username =post.getMember().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.sellState = post.getSellState();
        this.likes = likes;
        this.commentList = commentList;
    }

    public static PostResponseDtoDetail from(boolean likes, Post post, List<CommentResponseDto>  commentList) {
        return PostResponseDtoDetail.builder()
                .post(post)
                .likes(likes)
                .commentList(commentList)
                .build();

    }
}