package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시글 작성
    @PostMapping("/api/post")
    public ApiResponseDto<SuccessResponse> createPost(@RequestBody PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return postService.createPost(requestsDto, userDetails.getUser());
    }

    //선택 게시글 수정
    @PutMapping("/api/post/{postId}")
    public ApiResponseDto<SuccessResponse> updatePost(@RequestBody PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestsDto, userDetails.getUser());
    }

    // 선택된 게시글 삭제
    @DeleteMapping("/api/post/{postId}")
    public ApiResponseDto<SuccessResponse> deletePost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(postId, userDetails.getUser());
    }

    // 선택된 게시글 상세보기
    @GetMapping("/api/post/{postId}")
    public ApiResponseDto<PostResponseDto> getPost(@PathVariable Long postId,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId,userDetails.getUser());
    }

    // 게시글 전체 목록 조회
    @Operation(summary = "게시글 전체보기 메서드", description = "게시글 전체보기 메서드 입니다.")
    @GetMapping("/api/posts")
    public ApiResponseDto<List<PostResponseDto>> getAllPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getAllPosts(userDetails.getUser());
    }

}
