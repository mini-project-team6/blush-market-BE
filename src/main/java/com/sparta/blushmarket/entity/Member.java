package com.sparta.blushmarket.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    private Long kakaoId;

    private String email;

    @Builder
    public Member(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public Member(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public Member(String name, Long kakaoId, String password, String email) {
        this.name = name;
        this.kakaoId = kakaoId;
        this.password = password;
        this.email = email;
    }
    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}
