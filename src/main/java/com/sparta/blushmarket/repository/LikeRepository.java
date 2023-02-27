package com.sparta.blushmarket.repository;


import com.sparta.blushmarket.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes,Long> {

    Optional<Likes> findByPost_IdAndMember_Id(Long postId, Long MemberId);


}
