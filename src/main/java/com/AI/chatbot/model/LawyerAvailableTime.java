package com.AI.chatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Getter
@Setter
@Entity
public class LawyerAvailableTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean phoneConsultation;

    @Column(nullable = false)
    private boolean inPersonConsultation;

    @ManyToOne
    @JoinColumn(name = "lawyer_id")
    @JsonBackReference
    private User lawyer;

    @OneToMany(mappedBy = "availableTime")
    @JsonBackReference
    private List<Reservation> reservations;
}
