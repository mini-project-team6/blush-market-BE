package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시글 작성
    @PostMapping("/api/post")
    public ApiResponseDto<SuccessResponse> createPost(@RequestBody PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestsDto, userDetails.getUser());
    }

    //선택 게시글 수정
    @PutMapping("/api/post/{postId}")
    public ApiResponseDto<SuccessResponse> updatePost(@RequestBody PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestsDto, userDetails.getUser());
    }
}
