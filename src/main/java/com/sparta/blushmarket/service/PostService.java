package com.sparta.blushmarket.service;


import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.common.s3.FileUtil;
import com.sparta.blushmarket.common.s3.Uploader;
import com.sparta.blushmarket.dto.CommentResponseDto;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.dto.PostResponseDtoDetail;
import com.sparta.blushmarket.entity.Comment;
import com.sparta.blushmarket.entity.FileInfo;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.entity.enumclass.ExceptionEnum;
import com.sparta.blushmarket.entity.enumclass.SellState;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.repository.CommentRepository;
import com.sparta.blushmarket.repository.FileInfoRepository;
import com.sparta.blushmarket.repository.LikeRepository;
import com.sparta.blushmarket.repository.PostRepository;
import com.sparta.blushmarket.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final Uploader uploader;
    private final FileInfoRepository fileInfoRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;


    //게시글 작성
    @Transactional
    public ApiResponseDto<SuccessResponse> createPost(PostRequestDto postRequestDto, Member member) throws IOException {
        String fileUrl = "";
        FileInfo fileInfo;

        MultipartFile file = postRequestDto.getFile();

        if (file.isEmpty()) {

            postRepository.save(Post.of(postRequestDto, member));
            return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "등록완료"));
        }

        try {
            fileUrl = uploader.upload(file, "testImage");
            fileInfo = new FileInfo(
                    FileUtil.cutFileName(file.getOriginalFilename(), 500), fileUrl);

            postRequestDto.setImage(fileInfo.getFileUrl());
            postRequestDto.setOriginalFilename(file.getOriginalFilename());


        } catch (IOException ie) {
            log.info("S3파일 저장 중 예외 발생");
            throw ie;

        } catch (Exception e) {
            log.info("s3에 저장되었던 파일 삭제");
            uploader.delete(fileUrl.substring(fileUrl.lastIndexOf(".com/") + 5));
            throw e;
        }

        postRepository.save(Post.of(postRequestDto, member));
        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "작성 완료"));
    }

    // 선택된 게시글 수정
    @Transactional
    public ApiResponseDto<SuccessResponse> updatePost(Long id, PostRequestDto requestsDto, Member member) throws IOException {
        String fileUrl = "";
        FileInfo fileInfo = new FileInfo(requestsDto.getOriginalFilename(), requestsDto.getImage());
        MultipartFile file = requestsDto.getFile();
        Optional<Post> post = postRepository.findById(id);


        // 선택한 게시글이 DB에 있는지 확인
        if (post.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_MY_CONTENT_MODIFY);
        }

        // 선택한 게시글의 작성자와 토큰에서 가져온 사용자 정보가 일치하는지 확인 (수정하려는 사용자가 관리자라면 게시글 수정 가능)
        Optional<Post> found = postRepository.findByIdAndMember(id, member);
        if (found.isEmpty()) { // 일치하는 게시물이 없다면
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }
        // 기존 이미지 삭제

        FileInfo fileInfo1 = new FileInfo("삭제될 이미지", post.get().getImage());
        if (!(fileInfo1.getFileUrl() == null)) {
            uploader.delete(fileInfo1.S3key());
        }

        //이미지 비어있을시
        if (file.isEmpty()) {
            post.get().update(requestsDto, member);
            return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "수정 완료"));
        }
        // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정

        fileUrl = uploader.upload(file, "testImage");
        fileInfo = new FileInfo(
                FileUtil.cutFileName(Objects.requireNonNull(file.getOriginalFilename()), 500), fileUrl);

        requestsDto.setImage(fileInfo.getFileUrl());
        post.get().update(requestsDto, member);

        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "수정 완료"));

    }

    //선택 게시글 삭제
    @Transactional
    public ApiResponseDto<SuccessResponse> deletePost(Long id, Member member) {
        // 선택한 게시글이 DB에 있는지 확인
        Optional<Post> found = postRepository.findById(id);
        if (found.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }

        // 선택한 게시글의 작성자와 토큰에서 가져온 사용자 정보가 일치하는지 확인 (삭제하려는 사용자가 관리자라면 게시글 삭제 가능)
        Optional<Post> board = postRepository.findByIdAndMember(id, member);
        if (board.isEmpty()) { // 일치하는 게시물이 없다면
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }
        // S3 이미지 삭제

        Post post = postRepository
                .findById(id).orElseThrow(() -> new RuntimeException("존재 하지 않는 파일"));
        if (post.getImage() == null) {
            postRepository.deleteById(id);
            return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "삭제 성공"));
        }
        FileInfo fileInfo = new FileInfo(post.getOriginalFilename(), post.getImage());
        uploader.delete(fileInfo.S3key());

        // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
        postRepository.deleteById(id);

        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "삭제 성공"));

    }

    //선택된 게시글 상세보기
    @Transactional()
    public ApiResponseDto<PostResponseDtoDetail> getPost(Long id, UserDetailsImpl userDetails) {
        boolean isLike = false;
        boolean ismine = false;

        Member member = null;
        // Id에 해당하는 게시글이 있는지 확인
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) { // 해당 게시글이 없다면
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }
        if (userDetails != null) {
            member = userDetails.getUser();
        }
//        List<CommentResponseDto> commentList = post.get().getCommentList().stream().map(CommentResponseDto::from).sorted(Comparator.comparing(CommentResponseDto::getCreateAt).reversed()).toList();
//
        List<CommentResponseDto> commentList = new ArrayList<>();
        List<Comment> commentListTmp = post.get().getCommentList().stream().sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList();


        for (Comment comment : commentListTmp) {
            boolean ismineComment = false;
            if (member != null && commentRepository.findByIdAndPost_IdAndMember_Id(comment.getId(), id, member.getId()).isPresent()) {
                ismineComment = true;
            }

            commentList.add(CommentResponseDto.from(comment, ismineComment));
        }


        // board 를 responseDto 로 변환 후, ResponseEntity body 에 dto 담아 리턴
        if (member != null && likeRepository.findByPost_IdAndMember_Id(id, member.getId()).isPresent()) {
            isLike = true;
        }
        if (member != null && postRepository.findByIdAndMember(id, member).isPresent()) {
            ismine = true;
        }
        int sellcount = postRepository.countByMember_IdAndSellState(post.get().getMember().getId(), SellState.SOLDOUT);


        return ResponseUtils.ok(PostResponseDtoDetail.from(post.get(), commentList, isLike, ismine, sellcount));
    }


    //게시글 전체 조회
    public ApiResponseDto<List<PostResponseDto>> getAllPosts(Member member) {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> responseDtoList = new ArrayList<>();

        for (Post post : postList) {
            Boolean isLike = false;
            // List<BoardResponseDto> 로 만들기 위해 board 를 BoardResponseDto 로 만들고, list 에 dto 를 하나씩 넣는다.

            if (member != null && likeRepository.findByPost_IdAndMember_Id(post.getId(), member.getId()).isPresent()) {
                isLike = true;

            }
            int sellcount = postRepository.countByMember_IdAndSellState(post.getMember().getId(), SellState.SOLDOUT);
            responseDtoList.add(PostResponseDto.from(isLike, post, sellcount));
        }

        return ResponseUtils.ok(responseDtoList);
    }

    public ApiResponseDto<List<PostResponseDto>> getPostsByKeyword(String keyword, Integer sellstatus, UserDetailsImpl userDetails) {
        Member member = null;
        List<Post> postList;
        if (sellstatus != null) {
            postList = postRepository.findByTitleContainsAndSellStateOrderByCreatedAtDesc(keyword, SellState.fromInteger(sellstatus));
        } else {
            postList = postRepository.findByTitleContainsOrderByCreatedAtDesc(keyword);
        }
        System.out.println(postList.size());

        List<PostResponseDto> responseDtoList = new ArrayList<>();
        if (userDetails != null) {
            member = userDetails.getUser();
        }
        for (Post post : postList) {
            Boolean isLike = false;
            // List<BoardResponseDto> 로 만들기 위해 board 를 BoardResponseDto 로 만들고, list 에 dto 를 하나씩 넣는다.

            if (member != null && likeRepository.findByPost_IdAndMember_Id(post.getId(), member.getId()).isPresent()) {
                isLike = true;

            }
            int sellcount = postRepository.countByMember_IdAndSellState(post.getMember().getId(), SellState.SOLDOUT);
            responseDtoList.add(PostResponseDto.from(isLike, post, sellcount));
        }

        return ResponseUtils.ok(responseDtoList);
    }
}