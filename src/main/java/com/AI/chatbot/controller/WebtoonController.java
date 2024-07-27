package com.AI.chatbot.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.AI.chatbot.model.Story;
import com.AI.chatbot.model.User;
import com.AI.chatbot.service.UserService;
import com.AI.chatbot.service.WebtoonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/webtoon")
public class WebtoonController {

    private static final Logger logger = LoggerFactory.getLogger(WebtoonController.class);

    @Autowired
    private WebtoonService webtoonService;

    @Autowired
    private UserService userService;

    //사연 조회
    @GetMapping("/story")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Story>> getHistory(@RequestParam("userId") Long userId) {
        
        List<Story> stories = webtoonService.getStoryByUserId(userId);
        return ResponseEntity.ok(stories);
    }

    // 사연 저장
    @PostMapping("/stories")
    public ResponseEntity<Long> stories(@RequestBody String story) {
        
        User user = userService.getCurrentUser();
        if (user == null)
            return ResponseEntity.status(401).build();                                      // Unauthorized

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(story);
        
            // JSON에서 필요한 값 추출
            String storyValue = jsonNode.get("story").asText(); // "story" 키의 값을 추출
        
            Long id = webtoonService.save(storyValue, user.getId());
            return ResponseEntity.ok(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(null);     // Bad Request
        }
    }
}
