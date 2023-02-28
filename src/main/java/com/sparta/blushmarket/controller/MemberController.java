package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ErrorResponse;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.SignupRequestDto;
import com.sparta.blushmarket.entity.enumclass.ExceptionEnum;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.service.MemberService;
import com.sparta.blushmarket.service.oauth.KakaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
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
    @PostMapping("/member/signup")
    public ApiResponseDto<SuccessResponse> signup(@Valid @RequestBody SignupRequestDto signupRequestDto, BindingResult result){
        if (result.hasErrors()){
            log.error("error={}",result.getFieldError().getDefaultMessage());
            if (result.getFieldError().getDefaultMessage().equals("패스워드에러"))
                throw new CustomException(ExceptionEnum.INVALID_PASSWD_REG);
            throw new CustomException(ExceptionEnum.INVALID_EMAIL_REG);
        }
        return memberService.signup(signupRequestDto);
    }

    /**
     * 회원명 중복 체크
     */
    @Operation(summary = "회원 중복확인 메서드", description = "회원 중복확인 메서드 입니다.")
    @GetMapping("/member")
    public ApiResponseDto<SuccessResponse> memberCheck( @RequestParam("username") String username ) {
        memberService.memberCheck(username);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"사용가능한 계정입니다"));
    }


    @Operation(summary = "회원 토큰 갱신 메서드", description = "회원 토큰 갱신 메서드 입니다.")
    @GetMapping("/member/token")
    public  ApiResponseDto<SuccessResponse> issuedToken(HttpServletRequest request, HttpServletResponse response){
        return memberService.issueToken(request,response);
    }

}