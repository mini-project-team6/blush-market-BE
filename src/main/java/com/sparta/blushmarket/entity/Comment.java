package com.sparta.blushmarket.entity;

import com.sparta.blushmarket.dto.CommentRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    public void update(CommentRequestDto commentRequestDto){
        content = commentRequestDto.getContent();
    }
    @Builder
    private Comment(CommentRequestDto commentRequestDto, Member member, Post post) {
        this.content = commentRequestDto.getContent();
        this.member = member;
        this.post = post;
    }

    public static Comment of(CommentRequestDto commentRequestDto, Member member, Post post){
        return Comment.builder()
                .commentRequestDto(commentRequestDto)
                .member(member)
                .post(post)
                .build();
    }

}
