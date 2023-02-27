package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.dto.PostResponseDtoDetail;
import com.sparta.blushmarket.entity.FileInfo;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시글 작성
    @PostMapping("/api/post")
    public FileInfo createPost(@ModelAttribute PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws IOException {
        return postService.createPost(requestsDto, userDetails.getUser());
    }

    //선택 게시글 수정
    @PutMapping("/api/post/{postId}")
    public ApiResponseDto<SuccessResponse> updatePost(@PathVariable("postId") Long postId, @RequestBody PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(postId, requestsDto, userDetails.getUser());
    }

    // 선택된 게시글 삭제
    @DeleteMapping("/api/post/{postId}")
    public ApiResponseDto<SuccessResponse> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(postId, userDetails.getUser());
    }

    // 선택된 게시글 상세보기
    @GetMapping("/api/post/{postId}")
    public ApiResponseDto<PostResponseDtoDetail> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId,userDetails.getUser());
    }

    // 게시글 전체 목록 조회
    @Operation(summary = "게시글 전체보기 메서드", description = "게시글 전체보기 메서드 입니다.")
    @GetMapping("/api/posts")
    public ApiResponseDto<List<PostResponseDto>> getAllPosts(Member member) {
        return postService.getAllPosts(member);
    }

    @Operation(summary = "게시글 전체보기 메서드", description = "게시글 전체보기 메서드 입니다.")
    @GetMapping("/api/posts/keyword/{keyword}")
    public ApiResponseDto<List<PostResponseDto>> getAllPosts(@PathVariable String keyword,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPostsByKeyword(keyword,userDetails.getUser());
    }

}
