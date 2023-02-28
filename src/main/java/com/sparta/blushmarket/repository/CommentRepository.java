package com.sparta.blushmarket.repository;

import com.sparta.blushmarket.entity.Comment;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Comment findByIdAndMember_Id(Long id, Long memberId);
    Optional<Comment> findByIdAndPost_IdAndMember_Id(Long id , Long postId, Long memberid);
}
