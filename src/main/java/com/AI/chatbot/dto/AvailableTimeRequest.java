package com.AI.chatbot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableTimeRequest {
    private String  date;
    private String  startTime;
    private String  endTime;
    private boolean phoneConsultation;
    private boolean inPersonConsultation;
}
