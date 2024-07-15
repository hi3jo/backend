package com.AI.chatbot.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.AI.chatbot.model.ImageAnalysis;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.ImageAnalysisRepository;
import com.AI.chatbot.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ImageAnalysisService {

    @Autowired
    private ImageAnalysisRepository imageAnalysisRepository;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Path root = Paths.get("uploads");
    private static final Logger logger = LoggerFactory.getLogger(ImageAnalysisService.class);

    public String analyzeImage(MultipartFile file) throws IOException {
        String aiServerUrl = "http://localhost:8000/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

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
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("answer")) {
                String aiAnswer = (String) responseBody.get("answer");
                logger.info("Received AI answer: {}", aiAnswer);
                return aiAnswer;
            } else {
                logger.error("AI 서버 응답이 예상한 형식이 아님: {}", responseBody);
                throw new IOException("Failed to get a valid response from AI server.");
            }
        } catch (Exception e) {
            logger.error("AI 서버와 통신 중 오류 발생: {}", e.getMessage());
            throw new IOException("Error occurred while communicating with AI server", e);
        }
    }

    public ImageAnalysis saveImageAnalysis(String userId, MultipartFile file, String ask, String answer) throws IOException {
        logger.info("Saving image analysis: userId={}, ask={}, answer={}", userId, ask, answer);
        
        User user = userRepository.findByUserid(userId).orElseThrow(() -> {
            logger.error("User not found: userId={}", userId);
            return new RuntimeException("User not found");
        });

        String fileName = file.getOriginalFilename();
        Path filePath = root.resolve(fileName);
        
        try {
            Files.copy(file.getInputStream(), filePath);
            logger.info("File saved to disk: {}", filePath.toString());
        } catch (IOException e) {
            logger.error("Failed to save file to disk: {}", e.getMessage());
            throw e;
        }

        ImageAnalysis imageAnalysis = new ImageAnalysis();
        imageAnalysis.setFileName(fileName);
        imageAnalysis.setFilePath(filePath.toString());
        imageAnalysis.setAsk(ask);
        imageAnalysis.setAnswer(answer);
        imageAnalysis.setUser(user);

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
