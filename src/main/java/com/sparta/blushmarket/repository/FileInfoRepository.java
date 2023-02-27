package com.sparta.blushmarket.repository;


import com.sparta.blushmarket.entity.FileInfo;
import com.sparta.blushmarket.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
//    List<FileInfo> findAllByOrderByCreatedAtDesc();
}