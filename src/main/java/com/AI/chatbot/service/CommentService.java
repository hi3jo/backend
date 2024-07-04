package com.AI.chatbot.service;

import com.AI.chatbot.model.Comment;
import com.AI.chatbot.model.Post;
import com.AI.chatbot.model.User;
import com.AI.chatbot.repository.CommentRepository;
import com.AI.chatbot.repository.PostRepository;
import com.AI.chatbot.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment createComment(Long postId, String username, String content) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("게시물 없음"));
        User user = userRepository.findByUserid(username)
            .orElseThrow(() -> new RuntimeException("작성자 없음"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getUser().getUserid().equals(username)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        commentRepository.deleteById(commentId);
    }

    public Comment updateComment(Long commentId, String username, String content) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getUser().getUserid().equals(username)) {
            throw new RuntimeException("수정 권한 없음");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }
}
