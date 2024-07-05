package com.AI.chatbot.controller;

import com.AI.chatbot.model.Comment;
import com.AI.chatbot.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Comment> createComment(@PathVariable("postId") Long postId, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        String username = authentication.getName();
        String content = requestBody.get("content");
        Comment createdComment = commentService.createComment(postId, username, content);
        return ResponseEntity.ok(createdComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId, Authentication authentication) {
        String username = authentication.getName();
        commentService.deleteComment(commentId, username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable("commentId") Long commentId, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        String username = authentication.getName();
        String content = requestBody.get("content");
        Comment updatedComment = commentService.updateComment(commentId, username, content);
        return ResponseEntity.ok(updatedComment);
    }
}
