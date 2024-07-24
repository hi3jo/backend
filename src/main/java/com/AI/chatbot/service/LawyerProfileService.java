package com.AI.chatbot.service;

import com.AI.chatbot.model.LawyerProfile;
import com.AI.chatbot.repository.LawyerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LawyerProfileService {

  @Autowired
  private LawyerProfileRepository lawyerProfileRepository;

  public LawyerProfile getLawyerProfileByUserId(Long userId) {
      return lawyerProfileRepository.findByUser_Id(userId);
  }

  public LawyerProfile updateLawyerProfile(LawyerProfile lawyerProfile) {
    return lawyerProfileRepository.save(lawyerProfile);
  }

  public LawyerProfile createLawyerProfile(LawyerProfile lawyerProfile) {
    return lawyerProfileRepository.save(lawyerProfile);
  }
}
