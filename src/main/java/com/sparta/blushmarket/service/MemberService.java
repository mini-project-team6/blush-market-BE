package com.sparta.blushmarket.service;

import com.sparta.blushmarket.dto.StatusMsgResponseDto;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.jwt.JwtUtil;
import com.sparta.blushmarket.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<StatusMsgResponseDto> signup(String userName, String password){
        // 회원가입 유저가 있는지 확인하는 부분
        // 기존에 id체크하는 부분이 있어서 이 부분 관련 협의 필요
        memberCheck(userName);
        memberRepository.save(new Member(userName,passwordEncoder.encode(password)));
        return ResponseEntity.ok(new StatusMsgResponseDto("회원가입 성공", HttpStatus.OK.value()));
    }

    /**
     * 로그인 기능
     */
    @Transactional
    public ResponseEntity<StatusMsgResponseDto> login(String userName, String password, HttpServletRequest request) {

        Optional<Member> findMemeber = memberRepository.findByName(userName);
        if(findMemeber.isEmpty() || !passwordEncoder.matches(password,findMemeber.get().getPassword())){
            throw new IllegalStateException("회원이 존재하지 않거나 패스워드가 일치하지 않습니다");
        }

        return ResponseEntity.ok()
                .header(JwtUtil.AUTHORIZATION_HEADER,jwtUtil.createToken(findMemeber.get().getName()))
                .body(new StatusMsgResponseDto("로그인 완료", HttpStatus.OK.value()));
    }

    /**
     * 등록된 멤버인지 확인하는 기능
     */
    @Transactional
    public void memberCheck(String username) {
        Optional<Member> findMember = memberRepository.findByName(username);
        if(findMember.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }


}
