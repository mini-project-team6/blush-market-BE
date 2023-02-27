package com.sparta.blushmarket.controller;

import com.sparta.blushmarket.common.s3.FileUtil;
import com.sparta.blushmarket.common.s3.Uploader;
import com.sparta.blushmarket.dto.PostRequestDto;
import com.sparta.blushmarket.entity.FileInfo;
import com.sparta.blushmarket.repository.FileInfoRepository;
import com.sparta.blushmarket.security.UserDetailsImpl;
import com.sparta.blushmarket.service.FileInfoService;
import com.sparta.blushmarket.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
public class FileInfoController {

    private final FileInfoService fileInfoService;
    private final FileInfoRepository fileInfoRepository;
    private final Uploader uploader;
    private final PostService postService;

    @PostMapping("api/up22load")
//    public FileInfo upload() throws IOException {
    public FileInfo upload(@ModelAttribute PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return fileInfoService.upload(postRequestDto,userDetails.getUser());
    }

    @DeleteMapping("api/v1/upload/{i22d}")
    public Long delete(@PathVariable("id") Long id) {
        return fileInfoService.delete(id);
    }

    @GetMapping("/api/pos1t/{post22Id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("id") Long id,
                                                 HttpServletRequest request) throws IOException {
        FileInfo fileInfo = fileInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 파일"));

        String filename = FileUtil.getFileNameByBrowser(fileInfo.getFileOriginName(), request);
        Resource resource = uploader.downloadResource(fileInfo.S3key());


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}