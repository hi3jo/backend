package com.AI.chatbot.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

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

    @PostMapping("/upload")
    public ResponseEntity<List<Map<String, Object>>> uploadTextImgAna(@RequestParam("file") List<MultipartFile> files, Authentication authentication) {
        try {
            String userId = authentication.getName();
            logger.info("Received upload request: userId={}, number of files={}", userId, files.size());
            
            List<Map<String, Object>> aiResponses = textImgAnaService.analyzeTexts(files);
            List<TextImgAna> savedAnalyses = textImgAnaService.saveTextImgAna(userId, files, aiResponses);
            
            List<Map<String, Object>> response = savedAnalyses.stream().map(analysis -> {
                Map<String, Object> map = new HashMap<>();
                map.put("filename", analysis.getFileName());
                
                // answer 문자열을 파싱하여 개별 필드로 분리
                String[] answerParts = analysis.getAnswer().split("\n");
                Map<String, String> parsedAnswer = new HashMap<>();
                for (String part : answerParts) {
                    String[] keyValue = part.split(": ", 2);
                    if (keyValue.length == 2) {
                        parsedAnswer.put(keyValue[0], keyValue[1]);
                    }
                }
                map.put("answer", parsedAnswer);
                
                map.put("isPossible", analysis.isPossible());
                map.put("datetime", analysis.getDatetime());
                return map;
            }).collect(Collectors.toList());

            logger.info("Upload completed: userId={}, number of files processed={}", userId, response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in uploadTextImgAna: ", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{userId}/{textImgAnaId}")
    public ResponseEntity<Map<String, Object>> getTextImgAna(@PathVariable String userId, @PathVariable Long textImgAnaId) {
        try {
            TextImgAna textImgAna = textImgAnaService.getTextImgAna(userId, textImgAnaId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("filename", textImgAna.getFileName());
            
            // answer 문자열을 파싱하여 개별 필드로 분리
            String[] answerParts = textImgAna.getAnswer().split("\n");
            Map<String, String> parsedAnswer = new HashMap<>();
            for (String part : answerParts) {
                String[] keyValue = part.split(": ", 2);
                if (keyValue.length == 2) {
                    parsedAnswer.put(keyValue[0], keyValue[1]);
                }
            }
            response.put("answer", parsedAnswer);
            
            response.put("isPossible", textImgAna.isPossible());
            response.put("datetime", textImgAna.getDatetime());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }
}