package com.AI.chatbot.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.AI.chatbot.model.ImageAnalysis;
import com.AI.chatbot.service.ImageAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/imageAnalysis")
public class ImageAnalysisController {

    @Autowired
    private ImageAnalysisService imageAnalysisService;

    private static final Logger logger = LoggerFactory.getLogger(ImageAnalysisController.class);

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImageAnalysis(@RequestParam("file") MultipartFile file,
                                                      Authentication authentication) {
        try {
            String userId = authentication.getName(); // JWT 토큰에서 사용자 ID를 가져옵니다.
            logger.info("Received upload request: userId={}", userId);
            String aiAnswer = imageAnalysisService.analyzeImage(file);
            logger.info("AI answer received: {}", aiAnswer);
            imageAnalysisService.saveImageAnalysis(userId, file, aiAnswer);
            return ResponseEntity.ok(aiAnswer);
        } catch (IOException e) {
            logger.error("Failed to upload image", e);
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            logger.error("Exception stack trace: ", e); // 스택 트레이스를 로그에 추가
            return ResponseEntity.status(500).body("Unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/{imageAnalysisId}")
    public ResponseEntity<ImageAnalysis> getImageAnalysis(@PathVariable String userId, @PathVariable Long imageAnalysisId) {
        try {
            ImageAnalysis imageAnalysis = imageAnalysisService.getImageAnalysis(userId, imageAnalysisId);
            return ResponseEntity.ok(imageAnalysis);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/image/{userId}/{imageAnalysisId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String userId, @PathVariable Long imageAnalysisId) {
        try {
            byte[] imageData = imageAnalysisService.getImage(userId, imageAnalysisId);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageAnalysisId + "\"")
                .body(imageData);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
