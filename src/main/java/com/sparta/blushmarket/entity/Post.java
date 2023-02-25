package com.sparta.blushmarket.entity;


import com.sparta.blushmarket.dto.PostRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Post extends Timestamped{
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private SellState sellState;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    @Builder
    private Post(PostRequestDto requestDto , Member member) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContents();
        this.image = requestDto.getImage();
        this.sellState = SellState.fromInteger(requestDto.getSellState());
        this.member = member;

    }

    public static Post of(PostRequestDto requestDto, Member member) {
        return Post.builder()
                .requestDto(requestDto)
                .member(member)
                .build();
    }


    public void update(PostRequestDto requestDto, Member member) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContents();
        this.sellState = SellState.fromInteger(requestDto.getSellState());
        this.member = member;

    }

}
