package com.se.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "trips") // 确保表名正确
@Getter
@Setter
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn( nullable = false)
    private User user; // 确保与User实体正确关联

    @Column(length = 50, nullable = false)
    private String startLocation;

    @Column(length = 50, nullable = false)
    private String endLocation;

    @Column( length = 50, nullable = false)
    private String createTime;

    @Column( length = 50, nullable = false)
    private String type; // 添加出行类型字段

    // 可选：如果有必经点的需求，可以考虑在这里使用@OneToMany注解关联Waypoints
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WayPoint> wayPoints;
}