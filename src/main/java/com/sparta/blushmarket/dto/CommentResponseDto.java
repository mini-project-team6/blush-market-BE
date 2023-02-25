package com.sparta.blushmarket.dto;


import com.sparta.blushmarket.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long id;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifyedAt;


    @Builder
    private CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createAt = comment.getCreatedAt();
        this.modifyedAt = comment.getModifiedAt();
    }

    public static CommentResponseDto from(Comment comment){
        return CommentResponseDto.builder()
                .comment(comment)
                .build();
    }

}
