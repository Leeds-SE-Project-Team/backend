package com.se.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "waypoints")
@Getter
@Setter
public class WayPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Trip trip; // 关联到Trip实体

    @Column(length = 50,  nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String location; // "经度,纬度"格式

    @Column(length = 50,  nullable = false)
    private Integer sequence; // 必经点在行程中的顺序
}