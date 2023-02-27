package com.sparta.blushmarket.service;


import com.sparta.blushmarket.common.ApiResponseDto;
import com.sparta.blushmarket.common.ResponseUtils;
import com.sparta.blushmarket.common.SuccessResponse;
import com.sparta.blushmarket.common.s3.FileUtil;
import com.sparta.blushmarket.common.s3.Uploader;
import com.sparta.blushmarket.dto.CommentResponseDto;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.dto.PostResponseDto;
import com.sparta.blushmarket.entity.ExceptionEnum;
import com.sparta.blushmarket.entity.FileInfo;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.exception.CustomException;
import com.sparta.blushmarket.repository.FileInfoRepository;
import com.sparta.blushmarket.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final Uploader uploader;
    private final FileInfoRepository fileInfoRepository;

    //게시글 작성
    @Transactional
    public FileInfo createPost(PostRequestDto postRequestDto, Member member) throws IOException {
        String fileUrl = "";
        FileInfo fileinfo1;
        MultipartFile file = postRequestDto.getFile();

        try {
            fileUrl = uploader.upload(file, "testImage");
            FileInfo fileInfo = new FileInfo(
                    FileUtil.cutFileName(file.getOriginalFilename(), 500), fileUrl);

            fileinfo1 =  fileInfoRepository.save(fileInfo);
            postRequestDto.setImage(fileinfo1.getFileUrl());
            //fileInfoRepository.save(fileInfo);

        } catch (IOException ie) {
            log.info("S3파일 저장 중 예외 발생");
            throw ie;

        } catch (Exception e) {
            log.info("s3에 저장되었던 파일 삭제");
            uploader.delete(fileUrl.substring(fileUrl.lastIndexOf(".com/") + 5));
            throw e;
        }
        postRepository.save(Post.of(postRequestDto, member));
        return fileinfo1;
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
        Optional<Post> found = postRepository.findByIdAndMember(id, member);
        if (found.isEmpty()) { // 일치하는 게시물이 없다면
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }

        // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
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

        // 이미지 reposit 이미지 삭제
        FileInfo fileInfo = fileInfoRepository
                .findById(id).orElseThrow(() -> new RuntimeException("존재 하지 않는 파일"));
        fileInfoRepository.deleteById(id);
        uploader.delete(fileInfo.S3key());

        // 게시글 id 와 사용자 정보 일치한다면, 게시글 수정
        postRepository.deleteById(id);

        return ResponseUtils.ok(SuccessResponse.of(HttpStatus.OK, "삭제 성공"));

    }

    //선택된 게시글 상세보기
    @Transactional()
    public ApiResponseDto<PostResponseDto> getPost(Long id) {
        // Id에 해당하는 게시글이 있는지 확인
        Optional<Post> post = postRepository.findById(id);
        if (post.isEmpty()) { // 해당 게시글이 없다면
            throw new CustomException(ExceptionEnum.NOT_EXIST_POST);
        }

        List<CommentResponseDto> commentList = post.get().getCommentList().stream().map(CommentResponseDto::from).sorted(Comparator.comparing(CommentResponseDto::getCreateAt).reversed()).toList();

        // board 를 responseDto 로 변환 후, ResponseEntity body 에 dto 담아 리턴
        return ResponseUtils.ok(PostResponseDto.from(post.get(),commentList));
    }


    //게시글 전체 조회
    public ApiResponseDto<List<PostResponseDto>> getAllPosts() {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> responseDtoList = new ArrayList<>();

        for (Post post : postList) {
            // List<BoardResponseDto> 로 만들기 위해 board 를 BoardResponseDto 로 만들고, list 에 dto 를 하나씩 넣는다.
            List<CommentResponseDto> commentList = post.getCommentList().stream().map(CommentResponseDto::from).sorted(Comparator.comparing(CommentResponseDto::getCreateAt).reversed()).toList();
            responseDtoList.add(PostResponseDto.from(post,commentList));
        }

        return ResponseUtils.ok(responseDtoList);
    }
}
