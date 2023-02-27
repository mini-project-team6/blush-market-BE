package com.sparta.blushmarket.service.oauth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.TokenDto;
import com.sparta.blushmarket.dto.oauth.SocialUserInfoDto;
import com.sparta.blushmarket.entity.LoginType;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.RefreshToken;
import com.sparta.blushmarket.jwt.JwtUtil;
import com.sparta.blushmarket.repository.MemberRepository;
import com.sparta.blushmarket.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public ApiResponseDto<SuccessResponse> naverLogin(String code,String state, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code , state);

        // 2. 토큰으로 NAVER API 호출 : "액세스 토큰"으로 "NAVER 사용자 정보" 가져오기
        SocialUserInfoDto userInfo = getNaverUserInfo(accessToken);

        // 3. 필요시에 회원가입
        Member member = registerNaverUserIfNeeded(userInfo);

        // 4. JWT 토큰 반환
        TokenDto tokenDto = jwtUtil.createAllToken(member.getName());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findAllByMemberId(member.getName());

        if(refreshToken.isPresent()) {
            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefresh_Token()));
        }else {
            RefreshToken newToken = new RefreshToken(tokenDto.getRefresh_Token(), member.getName());
            refreshTokenRepository.save(newToken);
        }

        setHeader(response, tokenDto);

        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "사용가능한 계정입니다"));
    }

    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code,String state) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "stHMUzSSyYDbuPwB_C3s"); //REST API KEY
        body.add("client_secret", "aOMOPdao4q"); //REST API KEY
        body.add("code", code);
        body.add("state", state);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private SocialUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("response").get("id").asLong();
        String nickname = jsonNode.get("response").get("nickname").asText();
        String email = jsonNode.get("response").get("email").asText();

        log.info("네이버 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new SocialUserInfoDto(id, nickname, email);
    }

    // 3. 필요시에 회원가입
    private Member registerNaverUserIfNeeded(SocialUserInfoDto userInfoDto) {
        Member findUser = memberRepository.findByEmail(userInfoDto.getEmail())
                .orElse(null);

        if(findUser == null){
            findUser = memberRepository.save(Member.builder()
                    .name(userInfoDto.getNickname())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .email(userInfoDto.getEmail())
                    .loginType(LoginType.NAVER_USER)
                    .build());
        }else {
            findUser.updateLoginStatus(LoginType.NAVER_USER);
        }
        return findUser;
    }
    private void setHeader(HttpServletResponse response, TokenDto tokenDto) {
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, tokenDto.getAuthorization());
        response.addHeader(JwtUtil.REFRESH_TOKEN, tokenDto.getRefresh_Token());
    }
}












 /*// 네이버 사용자 email 동일한 email 가진 회원이 있는지 확인
            String naverEmail = naverUserInfo.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(naverEmail).orElse(null);
            if (sameEmailUser != null) {
                naverUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                naverUser = naverUser.naverIdUpdate(naverId);
            } else {
                // 신규 회원가입
                // password: random UUID
                String password = UUID.randomUUID().toString();
                String encodedPassword = passwordEncoder.encode(password);

                // email: kakao email
                String email = naverUserInfo.getEmail();
                naverUser = Member.builder()
                        .name(naverUserInfo.getNickname())
                        .naverId(naverId)
                        .password(encodedPassword)
                        .email(email)
                        .build();
            }
            memberRepository.save(naverUser);
        }
        return naverUser;*/