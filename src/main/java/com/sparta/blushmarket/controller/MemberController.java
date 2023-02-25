package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.dto.LoginRequestDto;
import com.sparta.blushmarket.dto.SignupRequestDto;
import com.sparta.blushmarket.dto.StatusMsgResponseDto;
import com.sparta.blushmarket.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
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
    @PostMapping("/signup")
    public ResponseEntity<StatusMsgResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
        return memberService.signup(signupRequestDto.getName(),signupRequestDto.getPassword());
    }

    /**
     * 회원명 중복 체크
     */
    @GetMapping("/userCheck/{username}")
    public ResponseEntity<StatusMsgResponseDto> memberCheck(@PathVariable String username) {
        memberService.memberCheck(username);
        return ResponseEntity.ok()
                .body(new StatusMsgResponseDto("사용가능한 계정명입니다", HttpStatus.OK.value()));
    }

    /**
     * 로그인 기능 Controller
     */
    @PostMapping("/login")
    public ResponseEntity<StatusMsgResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletRequest request){
        return memberService.login(requestDto.getName(),requestDto.getPassword(),request);
    }

}
