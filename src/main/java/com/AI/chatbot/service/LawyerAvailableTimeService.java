package com.AI.chatbot.service;

import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.LawyerAvailableTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LawyerAvailableTimeService {

    @Autowired
    private LawyerAvailableTimeRepository lawyerAvailableTimeRepository;

    public LawyerAvailableTime addAvailableTime(User lawyer, String date, String startTime, String endTime, boolean phoneConsultation, boolean inPersonConsultation) {
        LawyerAvailableTime availableTime = new LawyerAvailableTime();
        availableTime.setLawyer(lawyer);
        availableTime.setDate(LocalDate.parse(date));
        availableTime.setStartTime(LocalTime.parse(startTime));
        availableTime.setEndTime(LocalTime.parse(endTime));
        availableTime.setPhoneConsultation(phoneConsultation);
        availableTime.setInPersonConsultation(inPersonConsultation);
        return lawyerAvailableTimeRepository.save(availableTime);
    }

    public List<LawyerAvailableTime> getAvailableTimes(User lawyer) {
        return lawyerAvailableTimeRepository.findByLawyer(lawyer);
    }

    public void deleteAvailableTime(User lawyer, String date, String startTime, String endTime) {
        List<LawyerAvailableTime> availableTimes = lawyerAvailableTimeRepository.findByLawyerAndDateAndStartTimeAndEndTime(
                lawyer, LocalDate.parse(date), LocalTime.parse(startTime), LocalTime.parse(endTime));
        if (availableTimes.size() == 1) {
            lawyerAvailableTimeRepository.delete(availableTimes.get(0));
        } else if(availableTimes.size() > 1) {
            for(LawyerAvailableTime availableTime : availableTimes) {
                lawyerAvailableTimeRepository.delete(availableTime);
            }
        } else {
            throw new RuntimeException("Available time not found");
        }
    }

    public long countDistinctLawyersWithAvailableTimes() {
        return lawyerAvailableTimeRepository.countDistinctByLawyer();
    }

    public List<User> getAvailableLawyers() {
        List<LawyerAvailableTime> availableTimes = lawyerAvailableTimeRepository.findAll();
        return availableTimes.stream()
                .map(LawyerAvailableTime::getLawyer)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<LawyerAvailableTime> getAvailableTimesByLawyerId(Long lawyerId) {
        return lawyerAvailableTimeRepository.findByLawyerId(lawyerId);
    }

    public LawyerAvailableTime findAvailableTime(User lawyer, LocalDate date, LocalTime startTime, boolean isPhoneConsultation) {
        return lawyerAvailableTimeRepository.findByLawyerAndDateAndStartTimeAndPhoneConsultation(lawyer, date, startTime, isPhoneConsultation);
    }

    public Optional<LawyerAvailableTime> findById(Long id) {
        return lawyerAvailableTimeRepository.findById(id);
    }
}
