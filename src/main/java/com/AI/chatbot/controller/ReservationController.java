package com.AI.chatbot.controller;

import com.AI.chatbot.dto.ReservationRequest;
import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.Reservation;
import com.AI.chatbot.model.User;
import com.AI.chatbot.service.LawyerAvailableTimeService;
import com.AI.chatbot.service.ReservationService;
import com.AI.chatbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @Autowired
    private LawyerAvailableTimeService lawyerAvailableTimeService;

    @PostMapping("/reservation")
    public Reservation makeReservation(@RequestBody ReservationRequest reservationRequest) {
        User user = getCurrentAuthenticatedUser();
        User lawyer = userService.loadUserById(reservationRequest.getLawyerId());
        LawyerAvailableTime availableTime = lawyerAvailableTimeService.findById(reservationRequest.getAvailableTimeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Available time not found"));

        if (user == null || lawyer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User or Lawyer not found");
        }

        return reservationService.makeReservation(user, lawyer, availableTime, reservationRequest.getDate(), reservationRequest.getStartTime(), reservationRequest.getEndTime(), reservationRequest.isPhoneConsultation(), reservationRequest.isInPersonConsultation());
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userService.loadUserEntityByUsername(username);
        }
        throw new RuntimeException("User not authenticated");
    }
}
