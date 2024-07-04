package com.AI.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AI.chatbot.repository.QuestionRepository;


@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;

    public int save(String param) {
        
        int cnt = questionRepository.saveAll(param);
        return cnt;
    }
}