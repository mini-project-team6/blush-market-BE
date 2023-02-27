package com.sparta.blushmarket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.LoginRequestDto;
import com.sparta.blushmarket.service.MemberService;
import com.sparta.blushmarket.service.oauth.KakaoService;
import com.sparta.blushmarket.service.oauth.NaverService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final NaverService naverService;


    /**
     * 로그인 기능 Controller
     */
    @Operation(summary = "회원 로그인 메서드", description = "회원 로그인 메서드 입니다.")
    @PostMapping("/login")
    public ApiResponseDto<SuccessResponse> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        return memberService.login(requestDto,response);
    }

    /**
     * 카카오 로그인 기능 Controller
     */
    @GetMapping("/kakao/callback")
    public ApiResponseDto<SuccessResponse> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        log.info("response={}",response.getHeaderNames());
        ApiResponseDto<SuccessResponse> successResponseApiResponseDto = kakaoService.kakaoLogin(code, response);

        return successResponseApiResponseDto;
    }

    /**
     * 네이버 로그인 기능 Controller
     */
    @GetMapping("/naver/callback")
    public ApiResponseDto<SuccessResponse> naverLogin(@RequestParam String code,@RequestParam String state, HttpServletResponse response) throws JsonProcessingException {
        return naverService.naverLogin(code, state, response);
    }

    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public ApiResponseDto<SuccessResponse> logout(HttpServletResponse response)  {
        // 로그 아웃 시 쿠키 삭제
        Cookie cookie = new Cookie("Authorization",null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"로그아웃 성공"));
    }
}
