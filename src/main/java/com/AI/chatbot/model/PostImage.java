package com.AI.chatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post_images")
public class PostImage {

    @Id
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "image_url", length = 255)
    private String imageUrl;
}