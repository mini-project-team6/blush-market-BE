package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시판", description = "게시판 API 입니다.")
public class PostController {

    private final PostService postService;

    //게시글 작성
    @Operation(summary = "게시글 추가 메서드", description = "게시글 추가 메서드 입니다.")
    @PostMapping("/api/post")
    public ApiResponseDto<SuccessResponse> createPost(@RequestBody PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestsDto, userDetails.getUser());
    }

    //선택 게시글 수정
    @Operation(summary = "게시글 수정 메서드", description = "게시글 수정 메서드 입니다.")
    @PutMapping("/api/post/{id}")
    public ApiResponseDto<SuccessResponse> updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(id,requestsDto, userDetails.getUser());
    }

    // 선택된 게시글 삭제
    @Operation(summary = "게시글 삭제 메서드", description = "게시글 삭제 메서드 입니다.")
    @DeleteMapping("/api/post/{id}")
    public ApiResponseDto<SuccessResponse> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(id, userDetails.getUser());
    }

    // 선택된 게시글 상세보기
    @Operation(summary = "게시글 상세보기", description = "게시글 상세보기 메서드 입니다.")
    @GetMapping("/api/post/{id}")
    public ApiResponseDto<PostResponseDto> getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 게시글 전체 목록 조회
    @Operation(summary = "게시글 추가 메서드", description = "게시글 추가 메서드 입니다.")
    @GetMapping("/api/posts")
    public ApiResponseDto<List<PostResponseDto>> getAllPosts() {
        return postService.getAllPosts();
    }

}
