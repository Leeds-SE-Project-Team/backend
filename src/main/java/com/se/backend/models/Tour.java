package com.se.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tour")
@Getter
@Setter
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String startLocation;

    @Column(length = 50, nullable = false)
    private String endLocation;

    @Column(length = 50, nullable = false)
    private String createTime;

    public enum TourType {
        WALK("walk"),
        RUNNING("running"),
        DRIVE("drive");

        private String type;

        TourType(String type) {
            this.type = type;
        }
    }

    @Column(length = 50, nullable = false)
    private TourType type; // 添加出行类型字段

    // 可选：如果有必经点的需求，可以考虑在这里使用@OneToMany注解关联Waypoints
//    @JsonBackReference
//    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "tour")
    private List<PON> pons;

//    @JsonBackReference
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
//    @JoinColumn(nullable = false) // FIXME: nullable = false
    private TourCollection tourCollection; // 关联到Trip实体

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user; // 确保与User实体正确关联
}