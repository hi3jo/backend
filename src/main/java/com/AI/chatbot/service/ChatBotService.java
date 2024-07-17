package com.AI.chatbot.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AI.chatbot.model.ChatBot;
import com.AI.chatbot.model.History;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.ChatbotRepository;
import com.AI.chatbot.repository.HistoryRepository;
import com.AI.chatbot.repository.UserRepository;

@Service
public class ChatBotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotService.class);

    private final ChatbotRepository chatBotRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    public ChatBotService(ChatbotRepository chatBotRepository, HistoryRepository historyRepository, UserRepository userRepository) {
        this.chatBotRepository = chatBotRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    // 질문 저장
    @Transactional
    public Long save(ChatBot chatBot, Long userId, Long historyId) {
        logger.debug("질문 저장 시도 중. 사용자 ID: {}, 히스토리 ID: {}", userId, historyId);

        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            logger.error("유효하지 않은 사용자 ID: {}", userId);
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }
        User user = optionalUser.get();
        logger.debug("사용자 정보를 성공적으로 가져왔습니다. 사용자 ID: {}", user.getId());

        History history;
        if (historyId != null) {
            Optional<History> optionalHistory = historyRepository.findById(historyId);
            if (optionalHistory.isPresent()) {
                history = optionalHistory.get();
                logger.debug("기존 히스토리를 성공적으로 가져왔습니다. 히스토리 ID: {}", history.getId());
            } else {
                history = new History();
                history.setUser(user);
                history.setFirstChatBotAsk(chatBot.getAsk());
                historyRepository.save(history);
                logger.debug("새로운 히스토리를 생성했습니다. 히스토리 ID: {}", history.getId());
            }
        } else {
            history = new History();
            history.setUser(user);
            history.setFirstChatBotAsk(chatBot.getAsk());
            historyRepository.save(history);
            logger.debug("새로운 히스토리를 생성했습니다. 히스토리 ID: {}", history.getId());
        }

        chatBot.setHistory(history);
        chatBot.setUser(user);
        logger.debug("ChatBot 엔티티에 사용자와 히스토리를 설정했습니다.");

        ChatBot savedChatBot = chatBotRepository.save(chatBot);
        logger.debug("ChatBot 엔티티를 성공적으로 저장했습니다. ChatBot ID: {}", savedChatBot.getId());

        history.setLastChatBotDate(LocalDateTime.now());
        historyRepository.save(history);
        logger.debug("히스토리의 마지막 채팅 날짜를 갱신했습니다. 히스토리 ID: {}", history.getId());

        return savedChatBot.getId();
    }

    @Transactional
    public Long createNewHistory(Long userId, String firstAsk) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }

        User user = optionalUser.get();
        History history = new History();
        history.setUser(user);
        history.setFirstChatBotAsk(firstAsk);
        history.setLastChatBotDate(LocalDateTime.now());
        historyRepository.save(history);

        return history.getId();
    }

    // 답변 저장
    public Integer updateAnswer(String ask, String answer, Long userId, Long historyId) {
        try {
            logger.info("답변 업데이트 시도 중. 사용자 ID: {}, 히스토리 ID: {}", userId, historyId);

            Optional<User> optionalUser = userRepository.findById(userId);
            if (!optionalUser.isPresent()) {
                logger.error("유효하지 않은 사용자 ID: {}", userId);
                throw new IllegalArgumentException("Invalid userId: " + userId);
            }
            User user = optionalUser.get();
            logger.debug("사용자 정보를 성공적으로 가져왔습니다. 사용자 ID: {}", user.getId());

            Optional<History> optionalHistory = historyRepository.findById(historyId);
            if (!optionalHistory.isPresent()) {
                logger.error("유효하지 않은 히스토리 ID: {}", historyId);
                return null;
            }
            History history = optionalHistory.get();
            logger.debug("기존 히스토리를 성공적으로 가져왔습니다. 히스토리 ID: {}", history.getId());

            ChatBot chatBot = new ChatBot();
            chatBot.setHistory(history);
            chatBot.setUser(user);
            chatBot.setAsk(ask);  // 수정된 부분
            chatBot.setAnswer(answer);

            ChatBot savedChatBot = chatBotRepository.save(chatBot);
            logger.debug("ChatBot 엔티티를 성공적으로 저장했습니다. ChatBot ID: {}", savedChatBot.getId());

            history.setLastChatBotDate(LocalDateTime.now());
            historyRepository.save(history);
            logger.debug("히스토리의 마지막 채팅 날짜를 갱신했습니다. 히스토리 ID: {}", history.getId());

            return savedChatBot.getId().intValue();
        } catch (Exception e) {
            logger.error("답변 업데이트 중 오류 발생.", e);
            return null;
        }
    }

    public History getHistoryById(Long historyId) {
        return historyRepository.findById(historyId).orElse(null);
    }

    public List<History> getHistoryByUserId(Long userId) {
        return historyRepository.findByUserId(userId);
    }
    
    public List<ChatBot> getQuestionsByHistoryId(Long historyId) {
        return chatBotRepository.findByHistoryId(historyId);
    }

    public Long createHistory(History history) {
        historyRepository.save(history);
        return history.getId();
    }
}
