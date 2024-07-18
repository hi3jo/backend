package com.AI.chatbot.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AvailableTimeRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean phoneConsultation;
    private boolean inPersonConsultation;
    private double phoneConsultationPrice;
    private double inPersonConsultationPrice;
}
