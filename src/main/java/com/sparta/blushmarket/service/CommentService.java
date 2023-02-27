package com.sparta.blushmarket.service;

import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.dto.CommentRequestDto;
import com.sparta.blushmarket.entity.Comment;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.entity.enumclass.ExceptionEnum;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.repository.CommentRepository;
import com.sparta.blushmarket.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ApiResponseDto<SuccessResponse> addComment(Long id, CommentRequestDto commentRequestDto , Member member) {

        if(postRepository.findById(id).isEmpty()){
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }
        Post post = postRepository.findById(id).get();
        commentRepository.save(Comment.of(commentRequestDto,member,post));
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"댓글등록 성공"));
    }
    @Transactional
    public ApiResponseDto<SuccessResponse> updateComment(Long id, CommentRequestDto commentRequestDto, Member member) {
        if(commentRepository.findById(id).isEmpty()){
            throw new CustomException(ExceptionEnum.NOT_EXIST_COMMENT);
        }
        Comment comment = commentRepository.findByIdAndAndMember_Id(id, member.getId());
        if(comment==null){
            throw new CustomException(ExceptionEnum.NOT_MY_CONTENT_MODIFY);
        }

        comment.update(commentRequestDto);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"댓글수정 성공"));

    }

    public ApiResponseDto<SuccessResponse> deleteComment(Long id, Member member) {
        if(commentRepository.findById(id).isEmpty()){
            throw new CustomException(ExceptionEnum.NOT_EXIST_COMMENT);
        }
        Comment comment = commentRepository.findByIdAndAndMember_Id(id, member.getId());
        if(comment==null){
            throw new CustomException(ExceptionEnum.NOT_MY_CONTENT_DELETE);
        }
        commentRepository.delete(comment);
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK,"댓글삭제 성공"));
    }
}
