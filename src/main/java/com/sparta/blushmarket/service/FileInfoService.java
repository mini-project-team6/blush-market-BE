package com.sparta.blushmarket.service;

import com.sparta.blushmarket.common.s3.FileUtil;
import com.sparta.blushmarket.common.s3.Uploader;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.entity.FileInfo;
import com.sparta.blushmarket.entity.Member;
import com.sparta.blushmarket.entity.Post;
import com.sparta.blushmarket.repository.FileInfoRepository;
import com.sparta.blushmarket.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileInfoService {

    private final Uploader uploader;
    private final FileInfoRepository fileInfoRepository;
    private final PostRepository postRepository;

    @Transactional
    public FileInfo upload(PostRequestDto postRequestDto, Member member) throws IOException{
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

    @Transactional
    public Long delete(@PathVariable("id") Long id) {
        FileInfo fileInfo = fileInfoRepository
                .findById(id).orElseThrow(() -> new RuntimeException("존재 하지 않는 파일"));
        fileInfoRepository.deleteById(id);
        uploader.delete(fileInfo.S3key());
        return id;
    }
}