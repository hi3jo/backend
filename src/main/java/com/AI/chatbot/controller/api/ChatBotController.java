package com.AI.chatbot.controller.api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.AI.chatbot.model.Question;
import com.AI.chatbot.service.QuestionService;


@RestController
@RequestMapping("api/chatbot")
public class ChatBotController {
    
    @Autowired
    private QuestionService questionsService;

    @PostMapping("ask")
    public String askQuestion(@RequestParam String param) {
        //1. 챗봇에 질문한 내용 저장하기

        Question question = new Question();
        question.setAsk(param);

        int cnt = questionsService.save(question);
        System.out.println("param : " + param);
        return new String(); 
    }
    
}
