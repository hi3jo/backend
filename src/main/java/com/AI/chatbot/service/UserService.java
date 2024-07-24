package com.AI.chatbot.service;

import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserid(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User loadUserEntityByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserid(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User loadUserById(Long id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.findByUserid(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        return null;
    }

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByUserid(String userid) {
        return userRepository.findByUserid(userid);
    }
}
