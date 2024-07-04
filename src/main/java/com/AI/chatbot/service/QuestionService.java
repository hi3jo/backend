package com.AI.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AI.chatbot.model.Question;
import com.AI.chatbot.repository.QuestionRepository;


@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;

    public int save(Question question) {
        
        questionRepository.save(question);
        return 1;
    }
}