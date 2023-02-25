package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.LoginRequestDto;
import com.sparta.blushmarket.dto.SignupRequestDto;
import com.sparta.blushmarket.dto.StatusMsgResponseDto;
import com.sparta.blushmarket.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
@Tag(name = "회원", description = "회원 API 입니다.")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원관련 예외처리
     */
    
    @ExceptionHandler(value = {IllegalArgumentException.class,IllegalStateException.class})
    public ResponseEntity<StatusMsgResponseDto> userInfoError(RuntimeException e){
        log.error("Error Msg - " + e.getMessage() );
        return ResponseEntity.badRequest()
                .body(new StatusMsgResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
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

}
