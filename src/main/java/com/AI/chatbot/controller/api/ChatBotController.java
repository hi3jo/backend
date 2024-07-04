package com.AI.chatbot.controller.api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.AI.chatbot.model.Answer;
import com.AI.chatbot.model.Question;
import com.AI.chatbot.service.QuestionService;

@RestController
@RequestMapping("api/chatbot")
public class ChatBotController {
    
    @Autowired
    private QuestionService questionsService;

    @PostMapping("ask")
    public Long askQuestion(@RequestBody Question question) {
        
        //1. 챗봇에 질문한 내용 저장하기
System.out.println("1.챗봇 클라이언트로부터 전달받은 질문 : " + question);
        Long id = questionsService.save(question);
        return id;
    }

    @PostMapping("answer")
    public Integer saveAnswer(@RequestBody Answer answer) {
        System.out.println("2. GPT가 생성한 답변 : " + answer);
        Integer savedId = questionsService.updateAnswer(answer.getId(), answer.getAnswer());
        
        System.out.println("저장된 결과 : " + savedId);
        return savedId;
    }
}
