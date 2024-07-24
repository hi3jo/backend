package com.AI.chatbot.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {

    private Long lawyerId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean phoneConsultation;
    private boolean inPersonConsultation;
    private Long availableTimeId;
}
