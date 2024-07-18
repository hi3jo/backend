package com.AI.chatbot.controller;

import com.AI.chatbot.dto.AvailableTimeRequest;
import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.Reservation;
import com.AI.chatbot.model.User;
import com.AI.chatbot.service.LawyerAvailableTimeService;
import com.AI.chatbot.service.ReservationService;
import com.AI.chatbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LawyerController {

    @Autowired
    private LawyerAvailableTimeService lawyerAvailableTimeService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @PostMapping("/lawyer/available-time")
    public LawyerAvailableTime addAvailableTime(@RequestBody AvailableTimeRequest request) {
        User lawyer = getCurrentAuthenticatedUser();
        return lawyerAvailableTimeService.addAvailableTime(
                lawyer,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.isPhoneConsultation(),
                request.isInPersonConsultation(),
                request.getPhoneConsultationPrice(),
                request.getInPersonConsultationPrice()
        );
    }

    @GetMapping("/lawyer/available-times")
    public List<LawyerAvailableTime> getAvailableTimes() {
        User lawyer = getCurrentAuthenticatedUser();
        return lawyerAvailableTimeService.getAvailableTimes(lawyer);
    }

    @PostMapping("/reservation")
    public Reservation makeReservation(@RequestParam Long lawyerId,
                                       @RequestParam LocalDateTime startTime,
                                       @RequestParam LocalDateTime endTime) {
        User user = getCurrentAuthenticatedUser();
        User lawyer = userService.loadUserById(lawyerId);
        return reservationService.makeReservation(user, lawyer, startTime, endTime);
    }

    @GetMapping("/reservations/user")
    public List<Reservation> getUserReservations() {
        User user = getCurrentAuthenticatedUser();
        return reservationService.getReservationsByUser(user);
    }

    @GetMapping("/reservations/lawyer")
    public List<Reservation> getLawyerReservations() {
        User lawyer = getCurrentAuthenticatedUser();
        return reservationService.getReservationsByLawyer(lawyer);
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.loadUserEntityByUsername(userDetails.getUsername());
            // 추가된 로그
            System.out.println("Authenticated user ID: " + user.getId());
            return user;
        }
        throw new RuntimeException("User not authenticated");
    }
}
