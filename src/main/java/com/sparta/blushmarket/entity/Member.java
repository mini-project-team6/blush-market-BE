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
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LoginType loginType;

    @Builder
    public Member(String name, String password, String email,LoginType loginType) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.loginType = loginType;
    }

    public void updateLoginStatus(LoginType loginType){
        this.loginType = loginType;
    }

}
