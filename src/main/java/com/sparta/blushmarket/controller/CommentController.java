package com.sparta.blushmarket.controller;


import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.CommentRequestDto;
import com.sparta.blushmarket.repository.MemberRepository;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    private final MemberRepository memberRepository;
    @PostMapping("/comment/{id}")
    public ApiResponseDto<SuccessResponse> addComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.addComment(id,commentRequestDto,userDetails.getUser());

    }

    @PutMapping("/comment/{id}")
    public ApiResponseDto<SuccessResponse> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.updateComment(id,commentRequestDto,userDetails.getUser());

    }

    @DeleteMapping("/comment/{id}")
    public ApiResponseDto<SuccessResponse> updateComment(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.deleteComment(id,userDetails.getUser());

    }

}
