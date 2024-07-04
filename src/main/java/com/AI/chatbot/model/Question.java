package com.AI.chatbot.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //질문
    @Column(nullable = false)
    private String ask;

    //답변
    @Column(nullable = false)
    private String answer;

    //등록일자
    @Column(nullable = false)
    private Date reg_date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}