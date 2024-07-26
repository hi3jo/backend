package com.AI.chatbot.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.AI.chatbot.model.TextImgAna;
import com.AI.chatbot.service.TextImgAnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/textImgAna")
public class TextImgAnaController {

    @Autowired
    private TextImgAnaService textImgAnaService;

    private static final Logger logger = LoggerFactory.getLogger(TextImgAnaController.class);

    // 텍스트 이미지 분석 요청을 처리하는 엔드포인트
    @PostMapping("/upload")
    public ResponseEntity<String> uploadTextImgAna(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            // JWT 토큰에서 사용자 ID를 가져옵니다.
            String userId = authentication.getName();

            // AI 서버에 파일을 전송하여 분석 결과를 받습니다.
            Map<String, Object> aiResponse = textImgAnaService.analyzeText(file);

            // 분석 결과를 데이터베이스에 저장합니다.
            TextImgAna textImgAna = textImgAnaService.saveTextImgAna(userId, file, aiResponse);

            // 저장된 결과를 문자열 형식으로 반환합니다.
            return ResponseEntity.ok(String.format("분석결과: %s, 증거채택여부: %s, 시간: %s",
                    textImgAna.getAnswer(),
                    textImgAna.isPossible() ? "True" : "False",
                    textImgAna.getDatetime()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image or process text: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Unexpected error occurred: " + e.getMessage());
        }
    }

    // 특정 텍스트 이미지 분석 결과를 반환하는 엔드포인트
    @GetMapping("/{userId}/{textImgAnaId}")
    public ResponseEntity<TextImgAna> getTextImgAna(@PathVariable String userId, @PathVariable Long textImgAnaId) {
        try {
            // 데이터베이스에서 텍스트 이미지 분석 결과를 조회합니다.
            TextImgAna textImgAna = textImgAnaService.getTextImgAna(userId, textImgAnaId);
            return ResponseEntity.ok(textImgAna);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // 특정 이미지 파일을 반환하는 엔드포인트
    @GetMapping("/image/{userId}/{textImgAnaId}")
    public ResponseEntity<byte[]> getImage(@PathVariable String userId, @PathVariable Long textImgAnaId) {
        try {
            // 데이터베이스에서 이미지 파일을 조회합니다.
            byte[] imageData = textImgAnaService.getImage(userId, textImgAnaId);
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + textImgAnaId + "\"")
                .body(imageData);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
