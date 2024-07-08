package com.AI.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.AI.chatbot.dto.PostRequest;
import com.AI.chatbot.model.Like;
import com.AI.chatbot.model.Post;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.LikeRepository;
import com.AI.chatbot.repository.PostRepository;
import com.AI.chatbot.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    public List<Post> getAllPosts() {
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        posts.forEach(post -> post.getUser().getNickname());
        return posts;
    }

    public Optional<Post> getPostById(Long id) {
        Optional<Post> post = postRepository.findById(id);
        post.ifPresent(p -> p.getUser().getNickname());
        return post;
    }

    @Transactional
    public void incrementViews(Long postId) {
        postRepository.incrementViewCount(postId);
    }

    @Transactional
    public void likePost(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findByUserid(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        Like like = new Like();
        like.setPost(post);
        like.setUser(user);

        likeRepository.save(like);
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    public void unlikePost(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findByUserid(username).orElseThrow(() -> new RuntimeException("User not found"));
        
        Like like = likeRepository.findByPostAndUser(post, user).orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);
        post.setLikeCount(post.getLikeCount() - 1);
        postRepository.save(post);
    }

    public boolean isLikedByUser(Long postId, String username) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findByUserid(username).orElseThrow(() -> new RuntimeException("User not found"));
        return likeRepository.existsByPostAndUser(post, user);
    }
    
    public Post save(Post post) {
        return postRepository.save(post);
    }

    public Post createPost(PostRequest postRequest, User user) {
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUser(user);
        return postRepository.save(post);
    }

    public Post updatePost(Long id, PostRequest postRequest) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
