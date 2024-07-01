package com.AI.chatbot.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AI.chatbot.model.User;
import com.AI.chatbot.service.UserService;

import lombok.Getter;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody User user) {
        if (userService.userRepository.findByUserid(user.getUserid()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재합니다!");
        }
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/register-lawyer")
    public ResponseEntity<?> registerLawyer(@Validated @RequestBody User user) {
        if (userService.userRepository.findByUserid(user.getUserid()).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재합니다!");
        }
        user.setRole("ROLE_LAWYER");
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
        Optional<User> optionalUser = userService.findByUserid(loginRequest.getUserid());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(new UserResponse(user.getUserid(), user.getNickname()));
            }
        }
        return ResponseEntity.status(401).body("로그인 실패: 아이디 또는 비밀번호를 확인하세요.");
    }

    @Getter
    static class UserLoginRequest {
        private String userid;
        private String password;
    }

    @Getter
    static class UserResponse {
        private String userid;
        private String nickname;

        public UserResponse(String userid, String nickname) {
            this.userid = userid;
            this.nickname = nickname;
        }
    }
}
