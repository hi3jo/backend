package com.AI.chatbot.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AI.chatbot.model.Story;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.UserRepository;
import com.AI.chatbot.repository.WebtoonRepository;

@Service
public class WebtoonService {

    private static final Logger logger = LoggerFactory.getLogger(WebtoonService.class);

    @Autowired
    private WebtoonRepository webtoonRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Story> getStoryByUserId(Long userId) { return webtoonRepository.findByUserId(userId); }

    // 질문 저장
    @Transactional
    public Long save(String story, Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            logger.error("유효하지 않은 사용자 ID: {}", userId);
            throw new IllegalArgumentException("Invalid userId: " + userId);
        }

        User user = optionalUser.get();
        
        Story storyEntity = new Story();
        storyEntity.setStory(story);
        storyEntity.setUser(user);
        Story savedStory = webtoonRepository.save(storyEntity);
        return savedStory.getId();
    }
}
