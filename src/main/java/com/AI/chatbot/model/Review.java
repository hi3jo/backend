package com.AI.chatbot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lawyer_id")
    @JsonManagedReference("lawyer-reviews")
    private User lawyer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonManagedReference("user-reviews")
    private User user;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", lawyer=" + (lawyer != null ? lawyer.getId() : null) +
                ", user=" + (user != null ? user.getId() : null) +
                ", userNickname=" + (user != null ? user.getNickname() : null) +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                '}';
    }
}
