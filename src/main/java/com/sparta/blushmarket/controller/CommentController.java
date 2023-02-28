package com.sparta.blushmarket.controller;


import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.CommentRequestDto;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "댓글", description = "댓글 API 입니다.")
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 추가 메서드", description = "댓글 추가 메서드 입니다.")
    @PostMapping("/post/comment/{id}")
    public ApiResponseDto<SuccessResponse> addComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.addComment(id,commentRequestDto,userDetails.getUser());

    }

    @Operation(summary = "댓글 수정 메서드", description = "댓글 수정 메서드 입니다.")
    @PutMapping("/post/comment/{id}")
    public ApiResponseDto<SuccessResponse> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto commentRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.updateComment(id,commentRequestDto,userDetails.getUser());

    }

    @DeleteMapping("/post/comment/{id}")
    @Operation(summary = "댓글 삭제 메서드", description = "댓글 삭제 메서드 입니다.")
    public ApiResponseDto<SuccessResponse> updateComment(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.deleteComment(id,userDetails.getUser());

    }

}
