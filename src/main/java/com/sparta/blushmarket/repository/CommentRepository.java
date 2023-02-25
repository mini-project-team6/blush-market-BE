package com.sparta.blushmarket.repository;

import com.sparta.blushmarket.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
