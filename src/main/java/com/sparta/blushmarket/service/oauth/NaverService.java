package com.sparta.blushmarket.service.oauth;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.oauth.NaverUserInfoDto;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.jwt.JwtUtil;
import com.sparta.blushmarket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public ApiResponseDto<SuccessResponse> naverLogin(String code,String state, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code , state);
        log.info("accessToken={}",accessToken);

        // 2. 토큰으로 NAVER API 호출 : "액세스 토큰"으로 "NAVER 사용자 정보" 가져오기
        NaverUserInfoDto naverUserInfo = getNaverUserInfo(accessToken);

        // 3. 필요시에 회원가입
        Member naverUser = registerNaverUserIfNeeded(naverUserInfo);

        // 4. JWT 토큰 반환
        String createToken = jwtUtil.createToken(naverUser.getName(),"Access");
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createToken);

        // Cookie 생성 및 직접 브라우저에 Set
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

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
    private NaverUserInfoDto getNaverUserInfo(String accessToken) throws JsonProcessingException {
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
        System.out.println(responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("response").get("id").asLong();
        String nickname = jsonNode.get("response").get("nickname").asText();
        String email = jsonNode.get("response").get("email").asText();

        log.info("네이버 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new NaverUserInfoDto(id, nickname, email);
    }

    // 3. 필요시에 회원가입
    private Member registerNaverUserIfNeeded(NaverUserInfoDto naverUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long naverId = naverUserInfo.getId();
        Member naverUser = memberRepository.findByNaverId(naverId)
                .orElse(null);
        //naver로 가입된 회원 유무
        if (naverUser == null) {
            // 네이버 사용자 email 동일한 email 가진 회원이 있는지 확인
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
        return naverUser;
    }
}