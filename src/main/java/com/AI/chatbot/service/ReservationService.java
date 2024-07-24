package com.AI.chatbot.service;

import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.Reservation;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation makeReservation(User user, User lawyer, LawyerAvailableTime availableTime, LocalDate date, LocalTime startTime, LocalTime endTime, boolean phoneConsultation, boolean inPersonConsultation) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setLawyer(lawyer);
        reservation.setAvailableTime(availableTime);
        reservation.setDate(date);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setType(phoneConsultation ? "phone" : "inPerson");
        return reservationRepository.save(reservation);
    }
}
