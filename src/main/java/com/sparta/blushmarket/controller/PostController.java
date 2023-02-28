package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.dto.PostResponseDtoDetail;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "게시글", description = "게시글 API 입니다.")
public class PostController {

    private final PostService postService;

    //게시글 작성
    @Operation(summary = "게시글 작성 메서드", description = "게시글 작성 메서드 입니다.")
    @PostMapping("/api/post")
    public ApiResponseDto<SuccessResponse> createPost(@ModelAttribute PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {
        return postService.createPost(requestsDto, userDetails.getUser());
    }

    //선택 게시글 수정
    @Operation(summary = "게시글 수정 메서드", description = "게시글 수정 메서드 입니다.")
    @PutMapping("/api/post/{postId}")
    public ApiResponseDto<SuccessResponse> updatePost(@PathVariable("postId") Long postId, @ModelAttribute PostRequestDto requestsDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return postService.updatePost(postId, requestsDto, userDetails.getUser());
    }

    // 선택된 게시글 삭제
    @Operation(summary = "선택 게시글 삭제 메서드", description = "선택 게시글 삭제 메서드 입니다.")
    @DeleteMapping("/api/post/{postId}")
    public ApiResponseDto<SuccessResponse> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(postId, userDetails.getUser());
    }

    // 선택된 게시글 상세보기
    @Operation(summary = "특정 게시글 메서드", description = "특정 게시글 메서드 입니다.")
    @GetMapping("/api/post/{postId}")
    public ApiResponseDto<PostResponseDtoDetail> getPost(@PathVariable Long postId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.getPost(postId,userDetails);
    }

    // 게시글 전체 목록 조회
    @Operation(summary = "게시글 전체보기 메서드", description = "게시글 전체보기 메서드 입니다.")
    @GetMapping("/api/post")
    public ApiResponseDto<List<PostResponseDto>> getAllPosts(Member member) {
        return postService.getAllPosts(member);
    }

    @Operation(summary = "게시글 키워드검색 메서드", description = "게시글 키워드검색 메서드 입니다.")
    @GetMapping("/api/posts")
    public ApiResponseDto<List<PostResponseDto>> getAllPosts( @RequestParam("keyword") String keyword, @RequestParam(name="sellstatus", required=false) Integer sellstaus  , @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return postService.getPostsByKeyword(keyword,sellstaus,userDetails);
    }



}
