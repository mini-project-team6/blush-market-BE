package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "찜하기", description = "찜하기 API 입니다.")
public class LikeController {

    private final LikeService likeService;
    @Operation(summary = "글 찜하기 메서드", description = "글에 찜하기를 추가하거나 빼는 메서드")
    @PostMapping("/like/{id}")
    private ApiResponseDto<SuccessResponse> likePost(@PathVariable Long id , @AuthenticationPrincipal UserDetailsImpl userDetails){
        return likeService.likePost(id,userDetails.getUser());
    }

}
