package com.sparta.blushmarket.repository;

import com.sparta.blushmarket.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
