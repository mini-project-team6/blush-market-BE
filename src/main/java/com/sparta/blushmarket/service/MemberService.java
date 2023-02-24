package com.sparta.blushmarket.service;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.entity.ExceptionEnum;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.jwt.JwtUtil;
import com.sparta.blushmarket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponseDto<SuccessResponse> signup(String userName, String password){
        // 회원가입 유저가 있는지 확인하는 부분
        // 기존에 id체크하는 부분이 있어서 이 부분 관련 협의 필요
        memberCheck(userName);
        memberRepository.save(new Member(userName,passwordEncoder.encode(password)));
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"회원가입 성공"));
    }

    /**
     * 로그인 기능
     */
    @Transactional
    public ApiResponseDto<SuccessResponse> login(String userName, String password, HttpServletResponse response) {

        Optional<Member> findMemeber = memberRepository.findByName(userName);
        if(findMemeber.isEmpty() || !passwordEncoder.matches(password,findMemeber.get().getPassword())){
            throw new CustomException(ExceptionEnum.PASSWORD_WRONG);
        }
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(findMemeber.get().getName()));
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"로그인성공"));
    }

    /**
     * 등록된 멤버인지 확인하는 기능
     */
    @Transactional
    public void memberCheck(String username) {
        Optional<Member> findMember = memberRepository.findByName(username);
        if(findMember.isPresent()){
            throw new CustomException(ExceptionEnum.DUPLICATE_USER);
        }
    }


}
