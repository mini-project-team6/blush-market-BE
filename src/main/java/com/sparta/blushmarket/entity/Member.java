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
    private Long naverId;
    private String email;

    @Builder
    public Member(String name, String password,Long kakaoId, Long naverId, String email) {
        this.name = name;
        this.password = password;
        this.kakaoId = kakaoId;
        this.naverId = naverId;
        this.email = email;
    }

    public Member kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public Member naverIdUpdate(Long naverId) {
        this.naverId = naverId;
        return this;
    }
}
