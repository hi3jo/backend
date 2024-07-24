package com.AI.chatbot.service;

import com.AI.chatbot.model.TextImgAna;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.TextImgAnaRepository;
import com.AI.chatbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TextImgAnaService {

    @Autowired
    private TextImgAnaRepository textImgAnaRepository;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Path root = Paths.get("uploads");
    private static final Logger logger = LoggerFactory.getLogger(TextImgAnaService.class);

    // AI 서버로 텍스트 이미지를 전송하고 분석 결과를 받아오는 메서드
    public Map<String, Object> analyzeText(MultipartFile file) throws IOException {
        String aiServerUrl = "http://localhost:8000/api/analysis-text/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            logger.info("Sending request to AI server...");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                aiServerUrl, 
                HttpMethod.POST, 
                requestEntity, 
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("answer") && responseBody.containsKey("isPossible")) {
                logger.info("Received AI response: {}", responseBody);
                return responseBody;
            } else {
                logger.error("AI 서버 응답이 예상한 형식이 아님: {}", responseBody);
                throw new IOException("Failed to get a valid response from AI server.");
            }
        } catch (Exception e) {
            logger.error("AI 서버와 통신 중 오류 발생: {}", e.getMessage());
            logger.error("Exception stack trace: ", e); // 스택 트레이스를 로그에 추가
            throw new IOException("Error occurred while communicating with AI server", e);
        }
    }

    // 텍스트 이미지 분석 결과를 데이터베이스에 저장하는 메서드
    public TextImgAna saveTextImgAna(String userId, MultipartFile file, Map<String, Object> aiResponse) throws IOException {
        logger.info("Saving text image analysis: userId={}, answer={}", userId, aiResponse.get("answer"));

        // 사용자 조회
        User user = userRepository.findByUserid(userId).orElseThrow(() -> {
            logger.error("User not found: userId={}", userId);
            return new RuntimeException("User not found");
        });

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + "_" + originalFileName; // 타임스탬프와 UUID를 파일 이름에 추가
        Path filePath = root.resolve(uniqueFileName);

        logger.info("Original file name: {}", originalFileName);
        logger.info("Unique file name: {}", uniqueFileName);
        logger.info("File path: {}", filePath.toString());

        // 파일 저장 경로 존재 여부 확인 및 디렉토리 생성
        if (!Files.exists(root)) {
            try {
                Files.createDirectories(root);
                logger.info("Created directory for file uploads: {}", root.toString());
            } catch (IOException e) {
                logger.error("Failed to create directories: {}", e.getMessage());
                throw e;
            }
        }

        // 파일을 디스크에 저장
        try {
            Files.copy(file.getInputStream(), filePath);
            logger.info("File saved to disk: {}", filePath.toString());
        } catch (IOException e) {
            logger.error("Failed to save file to disk: {}", e.getMessage());
            throw e;
        }

        // 텍스트 이미지 분석 결과 엔티티 생성 및 저장
        TextImgAna textImgAna = new TextImgAna();
        textImgAna.setFileName(uniqueFileName);
        textImgAna.setFilePath(filePath.toString());
        textImgAna.setAnswer((String) aiResponse.get("answer"));
        textImgAna.setUser(user);
        textImgAna.setIsPossible((Boolean) aiResponse.get("isPossible"));
        textImgAna.setDatetime(LocalDateTime.now());

        try {
            TextImgAna savedTextImgAna = textImgAnaRepository.save(textImgAna);
            logger.info("Text image analysis saved to database: {}", savedTextImgAna);
            return savedTextImgAna;
        } catch (Exception e) {
            logger.error("Failed to save text image analysis to database: {}", e.getMessage());
            throw e;
        }
    }

    // 특정 텍스트 이미지 분석 결과를 조회하는 메서드
    public TextImgAna getTextImgAna(String userId, Long textImgAnaId) {
        return textImgAnaRepository.findById(textImgAnaId)
            .orElseThrow(() -> new RuntimeException("Text image analysis not found"));
    }

    // 특정 텍스트 이미지 파일을 조회하는 메서드
    public byte[] getImage(String userId, Long textImgAnaId) throws IOException {
        TextImgAna textImgAna = getTextImgAna(userId, textImgAnaId);
        return Files.readAllBytes(Paths.get(textImgAna.getFilePath()));
    }

    // 모든 텍스트 이미지 분석 결과를 조회하는 메서드
    public List<TextImgAna> getAllAnalyses() {
        return textImgAnaRepository.findAll();
    }
}
