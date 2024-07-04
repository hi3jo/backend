package com.AI.chatbot.controller.api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public String askQuestion(@RequestBody Question question) {
        //1. 챗봇에 질문한 내용 저장하기

        System.out.println("데이터 들어왔니 : " + question);

        int cnt = questionsService.save(question);
        return new String(); 
    }
    
}
