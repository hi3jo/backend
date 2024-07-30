package com.AI.chatbot.service;

import com.AI.chatbot.model.ImageAnalysis;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.ImageAnalysisRepository;
import com.AI.chatbot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageAnalysisService {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Autowired
    private ImageAnalysisRepository imageAnalysisRepository;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Path root = Paths.get("uploads");
    private static final Logger logger = LoggerFactory.getLogger(ImageAnalysisService.class);

    public Map<String, Object> analyzeImage(MultipartFile file) throws IOException {
        
        String url = aiServerUrl +"/api/analysis-image/";

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
                url, 
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

    public ImageAnalysis saveImageAnalysis(String userId, MultipartFile file, Map<String, Object> aiResponse) throws IOException {
        logger.info("Saving image analysis: userId={}, answer={}", userId, aiResponse.get("answer"));

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

        try {
            Files.copy(file.getInputStream(), filePath);
            logger.info("File saved to disk: {}", filePath.toString());
        } catch (IOException e) {
            logger.error("Failed to save file to disk: {}", e.getMessage());
            throw e;
        }

        ImageAnalysis imageAnalysis = new ImageAnalysis();
        imageAnalysis.setFileName(uniqueFileName);
        imageAnalysis.setFilePath(filePath.toString());
        imageAnalysis.setAnswer((String) aiResponse.get("answer"));
        imageAnalysis.setUser(user);
        imageAnalysis.setIsPossible((Boolean) aiResponse.get("isPossible"));
        imageAnalysis.setDatetime(LocalDateTime.now());

        try {
            ImageAnalysis savedImageAnalysis = imageAnalysisRepository.save(imageAnalysis);
            logger.info("Image analysis saved to database: {}", savedImageAnalysis);
            return savedImageAnalysis;
        } catch (Exception e) {
            logger.error("Failed to save image analysis to database: {}", e.getMessage());
            throw e;
        }
    }

    public ImageAnalysis getImageAnalysis(String userId, Long imageAnalysisId) {
        return imageAnalysisRepository.findById(imageAnalysisId)
            .orElseThrow(() -> new RuntimeException("Image analysis not found"));
    }

    public byte[] getImage(String userId, Long imageAnalysisId) throws IOException {
        ImageAnalysis imageAnalysis = getImageAnalysis(userId, imageAnalysisId);
        return Files.readAllBytes(Paths.get(imageAnalysis.getFilePath()));
    }
}