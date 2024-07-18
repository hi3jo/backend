package com.AI.chatbot.service;

import com.AI.chatbot.model.Reservation;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation makeReservation(User user, User lawyer, LocalDateTime startTime, LocalDateTime endTime) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setLawyer(lawyer);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus("PENDING");
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public List<Reservation> getReservationsByLawyer(User lawyer) {
        return reservationRepository.findByLawyer(lawyer);
    }
}
