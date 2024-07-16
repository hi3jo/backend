package com.AI.chatbot.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.AI.chatbot.model.ChatBot;
import com.AI.chatbot.model.History;
import com.AI.chatbot.model.User;
import com.AI.chatbot.service.ChatBotService;
import com.AI.chatbot.service.UserService;

@Tag(name = "chatbot", description = "챗봇 정보")
@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @Autowired
    private UserService userService;

    @PostMapping("/create-history")
    public ResponseEntity<Long> createHistory(@RequestBody History history) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        history.setUser(user);
        Long id = chatBotService.createHistory(history);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/ask")
    public ResponseEntity<Long> askQuestion(@RequestBody ChatBot chatBot) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        Long historyId = chatBot.getHistory() != null ? chatBot.getHistory().getId() : null;
        Long id = chatBotService.save(chatBot, user.getId(), historyId);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/answer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> saveAnswer(@RequestBody ChatBot answer) {
        Integer savedId = chatBotService.updateAnswer(answer.getId(), answer.getAnswer());
        return ResponseEntity.ok(savedId);
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<History>> getHistory(@RequestParam("userId") Long userId) {
        List<History> histories = chatBotService.getHistoryByUserId(userId);
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/history/questions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatBot>> getQuestions(@RequestParam("historyId") Long historyId) {
        List<ChatBot> questions = chatBotService.getQuestionsByHistoryId(historyId);
        return ResponseEntity.ok(questions);
    }
}
