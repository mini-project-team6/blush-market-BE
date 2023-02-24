package com.sparta.blushmarket.repository;

import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Optional<Post> findByIdAndUser(Long id, Member member);

}
