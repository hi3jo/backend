package com.AI.chatbot.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    
    @Autowired
    private ChatbotRepository chatbotRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Long save(ChatBot chatBot, Long userId, Long historyId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }
        User user = optionalUser.get();

        History history;
        if (historyId != null) {
            Optional<History> optionalHistory = historyRepository.findById(historyId);
            if (optionalHistory.isPresent()) {
                history = optionalHistory.get();
            } else {
                history = new History();
                history.setUser(user);
                history.setFirstChatBotAsk(chatBot.getAsk());
                historyRepository.save(history);
            }
        } else {
            history = new History();
            history.setUser(user);
            history.setFirstChatBotAsk(chatBot.getAsk());
            historyRepository.save(history);
        }

        chatBot.setHistory(history);
        chatBot.setUser(user);

        ChatBot savedChatBot = chatbotRepository.save(chatBot);
        history.setLastChatBotDate(LocalDateTime.now());
        historyRepository.save(history);

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

    @Transactional
    public Integer updateAnswer(Long id, String answer) {
        return chatbotRepository.updateAnswer(id, answer);
    }

    public History getHistoryById(Long historyId) {
        return historyRepository.findById(historyId).orElse(null);
    }

    public List<History> getHistoryByUserId(Long userId) {
        return historyRepository.findByUserId(userId);
    }
    
    public List<ChatBot> getQuestionsByHistoryId(Long historyId) {
        return chatbotRepository.findByHistoryId(historyId);
    }

    public Long createHistory(History history) {
        historyRepository.save(history);
        return history.getId();
    }
}
