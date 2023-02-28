package com.sparta.blushmarket.dto;


import com.sparta.blushmarket.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long id;
    private String username;
    private String content;
    private boolean ismine;
    private LocalDateTime createAt;
    private LocalDateTime modifyedAt;


    @Builder
    private CommentResponseDto(Comment comment,Boolean ismine){
        this.id = comment.getId();
        this.username = comment.getMember().getName();
        this.content = comment.getContent();
        this.createAt = comment.getCreatedAt();
        this.modifyedAt = comment.getModifiedAt();
        this.ismine = ismine;
    }

    public static CommentResponseDto from(Comment comment,Boolean ismine){
        return CommentResponseDto.builder()
                .comment(comment)
                .ismine(ismine)
                .build();
    }

}
