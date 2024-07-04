package com.AI.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AI.chatbot.model.Answer;
import com.AI.chatbot.model.Question;
import com.AI.chatbot.repository.QuestionRepository;

@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;

    //1. 질문 저장
    public Long save(Question question) {
        
        Question savedQuestion = questionRepository.save(question);
        return savedQuestion.getId();
    }

    //2. 질문에 대한 답변 저장
    @Transactional
    public Integer updateAnswer(Long id, String answer) {
        
        Integer savedAnswer = questionRepository.updateAnswer(id, answer);
        return savedAnswer;
    }
}