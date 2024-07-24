package com.AI.chatbot.repository;

import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface LawyerAvailableTimeRepository extends JpaRepository<LawyerAvailableTime, Long> {
    List<LawyerAvailableTime> findByLawyer(User lawyer);
    
    List<LawyerAvailableTime> findByLawyerAndDateAndStartTimeAndEndTime(User lawyer, LocalDate date, LocalTime startTime, LocalTime endTime);

    @Query("SELECT COUNT(DISTINCT l.lawyer) FROM LawyerAvailableTime l") long countDistinctByLawyer();

    List<LawyerAvailableTime> findByLawyerId(Long lawyerId);

    LawyerAvailableTime findByLawyerAndDateAndStartTimeAndPhoneConsultation(User lawyer, LocalDate date, LocalTime startTime, boolean PhoneConsultation);

    List<LawyerAvailableTime> findByLawyerAndDate(User lawyer, LocalDate date);
}
