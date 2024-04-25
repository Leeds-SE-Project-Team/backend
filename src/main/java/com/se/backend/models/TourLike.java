package com.se.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tour_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tour_id"})  // 强制唯一性
})
@Getter
@Setter
public class TourLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(length = 50, nullable = false)
    private String createtTime;
}
