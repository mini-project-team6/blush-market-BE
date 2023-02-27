package com.sparta.blushmarket.service;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.LoginRequestDto;
import com.sparta.blushmarket.dto.SignupRequestDto;
import com.sparta.blushmarket.dto.TokenDto;
import com.sparta.blushmarket.entity.enumclass.ExceptionEnum;
import com.sparta.blushmarket.entity.LoginType;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.RefreshToken;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.jwt.JwtUtil;
import com.sparta.blushmarket.repository.MemberRepository;
import com.sparta.blushmarket.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ApiResponseDto<SuccessResponse> signup(SignupRequestDto signupRequestDto){
        // 회원가입 유저가 있는지 확인하는 부분
        // 기존에 id체크하는 부분이 있어서 이 부분 관련 협의 필요
        memberCheck(signupRequestDto.getName());
        memberRepository.save(
                Member.builder()
                        .name(signupRequestDto.getName())
                        .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                        .email(signupRequestDto.getEmail())
                        .loginType(LoginType.USER)
                        .build());
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"회원가입 성공"));
    }

    /**
     * 로그인 기능
     */
    @Transactional
    public ApiResponseDto<SuccessResponse> login(LoginRequestDto requestDto, HttpServletResponse response) {
        String useremail = requestDto.getEmail();
        String password = requestDto.getPassword();

        Optional<Member> findMemeber = memberRepository.findByEmail(useremail);
        if(findMemeber.isEmpty() || !passwordEncoder.matches(password,findMemeber.get().getPassword())){
            throw new CustomException(ExceptionEnum.PASSWORD_WRONG);
        }

        TokenDto tokenDto = jwtUtil.createAllToken(useremail);

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findAllByMemberId(useremail);

        if(refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefresh_Token()));
        }else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefresh_Token(), useremail);
            refreshTokenRepository.save(newToken);
        }

        jwtUtil.setHeader(response, tokenDto);


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

    public ApiResponseDto<SuccessResponse> issueToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = jwtUtil.resolveToken(request, "Refresh");
        if(!jwtUtil.refreshTokenValidation(refreshToken)){
            throw new CustomException(ExceptionEnum.JWT_EXPIRED_TOKEN);
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(jwtUtil.getUserId(refreshToken), "Access"));
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"갱신 성공"));
    }
    public ApiResponseDto<SuccessResponse> logout(Member member) {
        refreshTokenRepository.deleteByMemberId(member.getName());
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"로그아웃 성공"));
    }
}