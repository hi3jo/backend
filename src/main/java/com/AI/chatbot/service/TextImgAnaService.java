package com.AI.chatbot.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

import com.AI.chatbot.model.TextImgAna;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.TextImgAnaRepository;
import com.AI.chatbot.repository.UserRepository;

@Service
public class TextImgAnaService {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Autowired
    private TextImgAnaRepository textImgAnaRepository;

    @Autowired
    private UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Path root = Paths.get("uploads");
    private static final Logger logger = LoggerFactory.getLogger(TextImgAnaService.class);

    public List<Map<String, Object>> analyzeTexts(List<MultipartFile> files) throws IOException {
        
        String url = aiServerUrl + "/api/analysis-text/";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile file : files) {
            body.add("files", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            logger.info("Sending request to AI server...");
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                requestEntity, 
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("results")) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) responseBody.get("results");
                logger.info("Received AI response: {}", results);
                return results;
            }
            logger.error("AI 서버 응답이 예상한 형식이 아님: {}", responseBody);
            throw new IOException("Failed to get a valid response from AI server.");
        } catch (Exception e) {
            logger.error("AI 서버와 통신 중 오류 발생: {}", e.getMessage());
            logger.error("Exception stack trace: ", e);
            throw new IOException("Error occurred while communicating with AI server", e);
        }
    }

    public List<TextImgAna> saveTextImgAna(String userId, List<MultipartFile> files, List<Map<String, Object>> aiResponses) throws IOException {
        logger.info("Saving text image analyses: userId={}, number of files={}", userId, files.size());

        User user = userRepository.findByUserid(userId).orElseThrow(() -> {
            logger.error("User not found: userId={}", userId);
            return new RuntimeException("User not found");
        });

        List<TextImgAna> savedAnalyses = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            Map<String, Object> aiResponse = aiResponses.get(i);

            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + "_" + originalFileName;
            Path filePath = root.resolve(uniqueFileName);

            logger.info("Saving file: {}", uniqueFileName);

            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            Files.copy(file.getInputStream(), filePath);

            TextImgAna textImgAna = new TextImgAna();
            textImgAna.setFileName(originalFileName);
            textImgAna.setFilePath(filePath.toString());
            textImgAna.setAnswer((String) aiResponse.get("answer"));
            textImgAna.setUser(user);
            textImgAna.setIsPossible((Boolean) aiResponse.get("isPossible"));
            textImgAna.setDatetime(LocalDateTime.now());

            savedAnalyses.add(textImgAnaRepository.save(textImgAna));
            logger.info("Text image analysis saved to database: {}", textImgAna);
        }

        return savedAnalyses;
    }

    public TextImgAna getTextImgAna(String userId, Long textImgAnaId) {
        return textImgAnaRepository.findById(textImgAnaId)
            .orElseThrow(() -> new RuntimeException("Text image analysis not found"));
    }

    public byte[] getImage(String userId, Long textImgAnaId) throws IOException {
        TextImgAna textImgAna = getTextImgAna(userId, textImgAnaId);
        return Files.readAllBytes(Paths.get(textImgAna.getFilePath()));
    }

    public List<TextImgAna> getAllAnalyses() {
        return textImgAnaRepository.findAll();
    }
}