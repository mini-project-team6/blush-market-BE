package com.sparta.blushmarket.repository;

import com.sparta.blushmarket.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
