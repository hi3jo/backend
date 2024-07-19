package com.AI.chatbot.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
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

import com.AI.chatbot.model.ChatBot;
import com.AI.chatbot.model.History;
import com.AI.chatbot.model.User;
import com.AI.chatbot.service.ChatBotService;
import com.AI.chatbot.service.UserService;

@Tag(name = "chatbot", description = "챗봇 정보")
@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotController.class);

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

    // 질문 저장
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

    //채팅 답변 로직
    @PostMapping("/answer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> saveAnswer(@RequestBody ChatBot answer) {
        // 현재 로그인된 사용자를 가져옴
        User user = userService.getCurrentUser();
        // 사용자가 null인 경우(인증되지 않은 사용자) 에러 로그를 남기고 401 Unauthorized 상태 반환
        if (user == null) {
            logger.error("인증되지 않은 접근 시도입니다.");
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        // 전송된 ChatBot 객체의 History 객체의 ID를 가져옴. 없으면 null
        Long historyId = answer.getHistory() != null ? answer.getHistory().getId() : null;
        // historyId가 null인 경우 에러 로그를 남기고 400 Bad Request 상태 반환
        if (historyId == null) {
            logger.error("전송된 ChatBot 객체에 유효한 History 객체가 포함되어 있지 않습니다.");
            return ResponseEntity.status(400).body(null); // Bad Request
        }

        // 답변 저장 요청 로그를 남김
        logger.info("답변 저장 요청 받음. historyId: {} 및 답변: {}", historyId, answer.getAnswer());

        // 답변을 저장하고 저장된 ID를 반환 받음
        Integer savedId = chatBotService.updateAnswer(answer.getAsk(), answer.getAnswer(), user.getId(), historyId);
        // 저장된 ID가 null이 아닌 경우 저장 성공 로그를 남김
        if (savedId != null) {
            logger.info("답변 저장 성공 answer id: {}", savedId);
        } else {
            // 저장된 ID가 null인 경우 저장 실패 로그를 남김
            logger.error("답변 저장 실패 answer id: {}", answer.getId());
        }
        // 저장된 ID를 OK 상태로 반환
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
