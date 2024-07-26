package com.AI.chatbot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDTO {
    private Long id;
    private Long lawyerId;
    private Long userId;
    private String userNickname;
    private String comment;
    private int rating;
}
