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

    private boolean ismine;
    private List<CommentResponseDto> commentList;


    @Builder
    private PostResponseDtoDetail( Post post, List<CommentResponseDto> commentList,boolean likes,boolean ismine) {
        this.id = post.getId();
        this.username =post.getMember().getName();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.image = post.getImage();
        this.sellState = post.getSellState();
        this.likes = likes;
        this.ismine = ismine;
        this.commentList = commentList;
    }

    public static PostResponseDtoDetail from( Post post, List<CommentResponseDto>  commentList,boolean likes,boolean ismine) {
        return PostResponseDtoDetail.builder()
                .post(post)
                .likes(likes)
                .ismine(ismine)
                .commentList(commentList)
                .build();

    }
}