package com.sparta.blushmarket.service;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.entity.SellState;
import com.sparta.blushmarket.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    //게시글 작성
    @Transactional
    public ApiResponseDto<SuccessResponse> createPost(PostRequestDto requestsDto, Member member) {

        // 작성 글 저장
        Post post = postRepository.save(Post.of(requestsDto, member));

        // BoardResponseDto 로 변환 후 responseEntity body 에 담아 반환
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "작성완료"));

    }
}
