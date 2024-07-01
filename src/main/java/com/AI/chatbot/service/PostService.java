package com.AI.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.AI.chatbot.repository.PostRepository;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}