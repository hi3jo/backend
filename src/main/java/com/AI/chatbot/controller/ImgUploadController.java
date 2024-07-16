package com.AI.chatbot.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/uploads")
public class ImgUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: 빈 파일");
        }

        try {
            // 원본 파일명 가져오기
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: 파일 이름 없음");
            }

            // 파일명 처리 (UUID를 이용한 고유한 파일명 생성)
            String fileExtension = StringUtils.getFilenameExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // 파일 저장 경로 설정
            Path path = Paths.get(uploadDir + File.separator + newFilename);

            // 파일 저장
            Files.write(path, file.getBytes());

            // 파일 URL 반환 (필요 시 URL 형식 조정)
            return ResponseEntity.ok("/uploads/" + newFilename);
        } catch (IOException e) {
            e.printStackTrace();  // 에러 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
        }
    }
}
