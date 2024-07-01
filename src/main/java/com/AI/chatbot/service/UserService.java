package com.AI.chatbot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    public Optional<User> findByUserid(String userid) {
        return userRepository.findByUserid(userid);
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public boolean authenticate(String userid, String password) {
        Optional<User> optionalUser = userRepository.findByUserid(userid);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getPassword().equals(password);
        }
        return false;
    }
}