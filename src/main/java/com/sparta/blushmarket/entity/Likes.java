package com.sparta.blushmarket.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    Post post;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    Member member;



    @Builder
    private Likes(Post post, Member member) {
        this.post = post;
        this.member = member;

    }

    public static Likes of(Post post, Member member){
        return Likes.builder()
                .post(post)
                .member(member)
                .build();
    }



}
