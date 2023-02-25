package com.sparta.blushmarket.repository;

import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Optional<Post> findByIdAndMember(Long id, Member member);

    List<Post> findAllByOrderByModifiedAtDesc();
}
