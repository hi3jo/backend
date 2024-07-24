package com.AI.chatbot.controller;

import com.AI.chatbot.dto.AvailableTimeRequest;
import com.AI.chatbot.model.LawyerAvailableTime;
import com.AI.chatbot.model.LawyerProfile;
import com.AI.chatbot.model.User;
import com.AI.chatbot.service.LawyerAvailableTimeService;
import com.AI.chatbot.service.LawyerProfileService;
import com.AI.chatbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LawyerController {

    @Autowired
    private LawyerAvailableTimeService lawyerAvailableTimeService;

    @Autowired
    private LawyerProfileService lawyerProfileService;

    @Autowired
    private UserService userService;

    @GetMapping("/lawyer/profile/{id}")
    public LawyerProfile getLawyerProfile(@PathVariable("id") Long id) {
        LawyerProfile profile = lawyerProfileService.getLawyerProfileByUserId(id);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        return profile;
    }

    @PutMapping("/lawyer/profile/{id}")
    public LawyerProfile updateLawyerProfile(@PathVariable("id") Long id, @RequestBody LawyerProfile lawyerProfile) {
        User user = getCurrentAuthenticatedUser();
        LawyerProfile existingProfile = lawyerProfileService.getLawyerProfileByUserId(id);
        if (existingProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found");
        }
        if (!user.getId().equals(existingProfile.getUser().getId())) {
            throw new RuntimeException("Unauthorized to update this profile");
        }

        if (lawyerProfile.getTitle() != null) {
            existingProfile.setTitle(lawyerProfile.getTitle());
        }
        if (lawyerProfile.getContent() != null) {
            existingProfile.setContent(lawyerProfile.getContent());
        }
        if (lawyerProfile.getName() != null) {
            existingProfile.setName(lawyerProfile.getName());
        }
        if (lawyerProfile.getAddress() != null) {
            existingProfile.setAddress(lawyerProfile.getAddress());
        }
        if (lawyerProfile.getPhoneNumber() != null) {
            existingProfile.setPhoneNumber(lawyerProfile.getPhoneNumber());
        }
        if (lawyerProfile.getPhoneConsultationPrice() != null) {
            existingProfile.setPhoneConsultationPrice(lawyerProfile.getPhoneConsultationPrice());
        }
        if (lawyerProfile.getInPersonConsultationPrice() != null) {
            existingProfile.setInPersonConsultationPrice(lawyerProfile.getInPersonConsultationPrice());
        }

        return lawyerProfileService.updateLawyerProfile(existingProfile);
    }

    @PostMapping("/lawyer/profile")
    public LawyerProfile createLawyerProfile(@RequestBody LawyerProfile lawyerProfile) {
        User user = getCurrentAuthenticatedUser();
        lawyerProfile.setUser(user);
        return lawyerProfileService.createLawyerProfile(lawyerProfile);
    }

    @PostMapping("/lawyer/available-time")
    public LawyerAvailableTime addAvailableTime(@RequestBody AvailableTimeRequest request) {
        User lawyer = getCurrentAuthenticatedUser();
        return lawyerAvailableTimeService.addAvailableTime(
                lawyer,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.isPhoneConsultation(),
                request.isInPersonConsultation()
        );
    }

    @GetMapping("/lawyer/{id}/available-times")
    public List<LawyerAvailableTime> getAvailableTimesByLawyerId(@PathVariable("id") Long lawyerId) {
        return lawyerAvailableTimeService.getAvailableTimesByLawyerId(lawyerId);
    }

    @GetMapping("/lawyer/available-times")
    public List<LawyerAvailableTime> getAvailableTimes() {
        User lawyer = getCurrentAuthenticatedUser();
        return lawyerAvailableTimeService.getAvailableTimes(lawyer);
    }

    @DeleteMapping("/lawyer/available-time")
    public void deleteAvailableTime(@RequestBody AvailableTimeRequest request) {
        User lawyer = getCurrentAuthenticatedUser();
        lawyerAvailableTimeService.deleteAvailableTime(
                lawyer,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.loadUserEntityByUsername(userDetails.getUsername());
            return user;
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping("/lawyer/count")
    public long countLawyersWithAvailableTimes() {
        return lawyerAvailableTimeService.countDistinctLawyersWithAvailableTimes();
    }

    @GetMapping("/lawyer/available-lawyers")
    public List<User> getAvailableLawyers() {
        return lawyerAvailableTimeService.getAvailableLawyers();
    }
}
