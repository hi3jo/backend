package com.AI.chatbot.repository;

import com.AI.chatbot.model.Reservation;
import com.AI.chatbot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByLawyer(User lawyer);
}
