package com.sparta.blushmarket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ErrorResponse;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.LoginRequestDto;
import com.sparta.blushmarket.dto.SignupRequestDto;
import com.sparta.blushmarket.jwt.JwtUtil;
import com.sparta.blushmarket.service.KakaoService;
import com.sparta.blushmarket.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
@Tag(name = "회원", description = "회원 API 입니다.")
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    /**
     * 회원관련 예외처리
     */
    
    @ExceptionHandler(value = {IllegalArgumentException.class,IllegalStateException.class})
    public ResponseEntity<ErrorResponse> userInfoError(RuntimeException e){
        log.error("Error Msg - " + e.getMessage() );
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),e.getMessage()));
    }

    /**
     * 회원가입 기능 Controller
     */
    @Operation(summary = "회원가입 메서드", description = "회원가입 메서드 입니다.")
    @PostMapping("/signup")
    public ApiResponseDto<SuccessResponse> signup(@RequestBody SignupRequestDto signupRequestDto){
        return memberService.signup(signupRequestDto.getName(),signupRequestDto.getPassword());
    }

    /**
     * 회원명 중복 체크
     */
    @Operation(summary = "회원 중복확인 메서드", description = "회원 중복확인 메서드 입니다.")
    @GetMapping("/usercheck/{username}")
    public ApiResponseDto<SuccessResponse> memberCheck(@PathVariable String username) {
        memberService.memberCheck(username);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"사용가능한 계정입니다"));
    }

    /**
     * 로그인 기능 Controller
     */
    @Operation(summary = "회원 로그인 메서드", description = "회원 로그인 메서드 입니다.")
    @PostMapping("/login")
    public ApiResponseDto<SuccessResponse> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response){
        return memberService.login(requestDto.getName(),requestDto.getPassword(),response);
    }

    /**
     * 카카오 로그인 기능 Controller
     */
    @GetMapping("/kakao/callback")
    public ApiResponseDto<SuccessResponse> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        return kakaoService.kakaoLogin(code, response);
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
