package com.AI.chatbot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.AI.chatbot.dto.PostRequest;
import com.AI.chatbot.model.Post;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.PostRepository;
import com.AI.chatbot.repository.UserRepository;
import com.AI.chatbot.service.PostService;
import com.AI.chatbot.util.S3Utils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private S3Utils s3utils;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @GetMapping
    public List<Post> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        posts.forEach(post -> post.setCommentCount(postService.getCommentCount(post.getId())));
        return posts;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        Optional<Post> post = postService.getPostById(id);
        if (post.isPresent()) {
            Post p = post.get();
            int commentCount = postService.getCommentCount(id);
            p.setCommentCount(commentCount);
            return ResponseEntity.ok(p);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/increment-views")
    public ResponseEntity<Void> incrementViews(@PathVariable("id") Long id) {
        postService.incrementViews(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable("id") Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        postService.likePost(id, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable("id") Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        postService.unlikePost(id, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/like-status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(@PathVariable("id") Long id) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        boolean liked = postService.isLikedByUser(id, username);
        return ResponseEntity.ok(Map.of("liked", liked));
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

    //게시글 업데이트
    @PutMapping("/{id}")
  //public ResponseEntity<Post> updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostRequest postRequest) {
    public ResponseEntity<Post> updatePost(@PathVariable("id") Long id, @ModelAttribute PostRequest postRequest) {
        
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!post.getUser().getUserid().equals(username)) {
                return ResponseEntity.status(403).build();
            }

            // 삭제 된 이미지 S3 Bucket 에서 삭제 : Handle deleted images
            List<String> deletedImages = postRequest.getDeletedImages();
            if (deletedImages != null) {
                for (String delImageUrl : deletedImages) {
                    
                    // Assuming s3utils.deleteImageFromS3 handles the deletion
                    s3utils.deleteImageFromS3(delImageUrl);
                    
                    // Also remove from the post's imageUrls list if stored there
                    post.getImageUrls().remove(delImageUrl);
                }
            }

            //제목과 컨텐츠만 업데이트
            Post updatedPost = postService.updatePost(id, postRequest);

            try {
                
                // Handle new image URLs if they exist
                List<MultipartFile> newImageUrls = postRequest.getUptImageUrls();
                s3utils.fileUpload(newImageUrls, post);
            } catch (Exception e) {
                e.printStackTrace();
            }     

            return ResponseEntity.ok(updatedPost);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(   @PathVariable("id") Long id
                                            , @RequestParam(value = "imageUrls", required = false) List<String> imageUrls) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        Optional<Post> optionalPost = postService.getPostById(id);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!post.getUser().getUserid().equals(username)) {
                return ResponseEntity.status(403).build();
            }
            postService.deletePost(id);

            //S3 Bucket에서 이미지 삭제해야 함.
            if (imageUrls != null) {
                for (String imageUrl : imageUrls) {
                    s3utils.deleteImageFromS3(imageUrl);
                }
            }
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
            Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

            Page<Post> pagePosts;
            if (sk != null && sv != null) {
                if (sk.equals("title")) {
                    pagePosts = postRepository.findByTitleContaining(sv, paging);
                } else if (sk.equals("content")) {
                    pagePosts = postRepository.findByContentContaining(sv, paging);
                } else {
                    pagePosts = postRepository.findByTitleOrContentContaining(sv, paging);
                }
            } else {
                pagePosts = postRepository.findAll(paging);
            }

            posts = pagePosts.getContent();
            posts.forEach(post -> post.setCommentCount(postService.getCommentCount(post.getId())));
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

    @SuppressWarnings("null")
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularPosts(
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(required = false, name = "sk") String sk,
            @RequestParam(required = false, name = "sv") String sv) {
        try {
            List<Post> posts;
            Pageable paging = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

            Page<Post> pagePosts;
            if (sk != null && sv != null) {
                pagePosts = postRepository.findByPopularPostsBySearch(30, sv, paging);
            } else {
                pagePosts = postRepository.findByPopularPosts(30, paging);
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

    @GetMapping("/best")
    public ResponseEntity<List<Post>> getBestPosts(@RequestParam(name = "limit", defaultValue = "5") int limit) {
        List<Post> bestPosts = postService.getBestPosts(limit);
        return ResponseEntity.ok(bestPosts);
    }
}
