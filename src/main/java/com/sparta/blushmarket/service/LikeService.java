package com.sparta.blushmarket.service;


import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.entity.ExceptionEnum;
import com.sparta.blushmarket.entity.Likes;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.repository.LikeRepository;
import com.sparta.blushmarket.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Transactional
    public ApiResponseDto<SuccessResponse> likePost(Long id, Member member) {
        if (postRepository.findById(id).isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);

        }
        Optional<Likes> likes = likeRepository.findByPost_IdAndMember_Id(id, member.getId());

        if(likes.isPresent()){
            likeRepository.delete(likes.get());
            
            return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "찜하기 삭제"));
            
        }


        Post post = postRepository.findById(id).get();
        likeRepository.saveAndFlush(Likes.of(post,member));
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "찜하기 추가"));


//        likeRepository.findByBoard_IdAndUsers_Id(id, member.getId());
    }


}
