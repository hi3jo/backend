package com.AI.chatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class LawyerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private int yearsOfExperience;

    @Column(nullable = false)
    private String contactInfo;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
