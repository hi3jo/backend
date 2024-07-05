package com.AI.chatbot.controller;

import com.AI.chatbot.dto.PostRequest;
import com.AI.chatbot.model.Post;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.PostRepository;
import com.AI.chatbot.repository.UserRepository;
import com.AI.chatbot.service.PostService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostRequest postRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        
        User user = userRepository.findByUserid(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post savedPost = postService.createPost(postRequest, user);
        return ResponseEntity.ok(savedPost);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostRequest postRequest) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!post.getUser().getUserid().equals(username)) {
                return ResponseEntity.status(403).build();
            }
            Post updatedPost = postService.updatePost(id, postRequest);
            return ResponseEntity.ok(updatedPost);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!post.getUser().getUserid().equals(username)) {
                return ResponseEntity.status(403).build();
            }
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SuppressWarnings("null")
    @GetMapping("/pages")
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(required = false, name = "sk") String sk,
            @RequestParam(required = false, name = "sv") String sv) {
        try {
            List<Post> posts;
            Pageable paging = PageRequest.of(page, size);

            Page<Post> pagePosts;
            if (sk != null && sv != null) {
                if (sk.equals("title")) {
                    pagePosts = postRepository.findByTitleContaining(sv, paging);
                } else if (sk.equals("content")) {
                    pagePosts = postRepository.findByContentContaining(sv, paging);
                } else if (sk.equals("")) {
                    pagePosts = postRepository.findByTitleOrContentContaining(sv, paging);
                } else {
                    pagePosts = postRepository.findAll(paging);
                }
            } else {
                pagePosts = postRepository.findAll(paging);
            }

            posts = pagePosts.getContent();
            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("currentPage", pagePosts.getNumber());
            response.put("totalItems", pagePosts.getTotalElements());
            response.put("totalPages", pagePosts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
