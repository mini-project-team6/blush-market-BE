package com.sparta.blushmarket.service;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.entity.ExceptionEnum;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.entity.SellState;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    //게시글 작성
    @Transactional
    public ApiResponseDto<SuccessResponse> createPost(PostRequestDto requestsDto, Member member) {

        // 작성 글 저장
        Post post = postRepository.save(Post.of(requestsDto, member));

        // BoardResponseDto 로 변환 후 responseEntity body 에 담아 반환
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "작성완료"));

    }

    // 선택된 게시글 수정
    @Transactional
    public ApiResponseDto<SuccessResponse> updatePost(Long id, PostRequestDto requestsDto, Member member) {

        // 선택한 게시글이 DB에 있는지 확인
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_MY_CONTENT_MODIFY);
        }

        // 선택한 게시글의 작성자와 토큰에서 가져온 사용자 정보가 일치하는지 확인 (수정하려는 사용자가 관리자라면 게시글 수정 가능)
        Optional<Post> found = postRepository.findByIdAndUser(id, member);
        if (found.isEmpty()) { // 일치하는 게시물이 없다면
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }

        // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
        post.get().update(requestsDto, member);

        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "수정 완료"));

    }


}
