package com.AI.chatbot.service;

import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.LawyerAvailableTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class LawyerAvailableTimeService {

    @Autowired
    private LawyerAvailableTimeRepository lawyerAvailableTimeRepository;

    public LawyerAvailableTime addAvailableTime(User lawyer, LocalDate date, LocalTime startTime, LocalTime endTime,
                                                boolean phoneConsultation, boolean inPersonConsultation,
                                                double phoneConsultationPrice, double inPersonConsultationPrice) {
        LawyerAvailableTime availableTime = new LawyerAvailableTime();
        availableTime.setLawyer(lawyer);
        availableTime.setDate(date);
        availableTime.setStartTime(startTime);
        availableTime.setEndTime(endTime);
        availableTime.setPhoneConsultation(phoneConsultation);
        availableTime.setInPersonConsultation(inPersonConsultation);
        availableTime.setPhoneConsultationPrice(phoneConsultationPrice);
        availableTime.setInPersonConsultationPrice(inPersonConsultationPrice);

        return lawyerAvailableTimeRepository.save(availableTime);
    }

    public List<LawyerAvailableTime> getAvailableTimes(User lawyer) {
        return lawyerAvailableTimeRepository.findByLawyer(lawyer);
    }
}
