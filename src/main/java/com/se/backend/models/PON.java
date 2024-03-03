package com.se.backend.models;

import com.se.backend.projection.PONDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// 必经点
@Entity
@Table(name = "pon")
@Getter
@Setter
public class PON {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Tour tour; // 关联到Trip实体

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String location; // "经度,纬度"格式

    @Column(length = 50, nullable = false)
    private Integer sequence; // 必经点在行程中的顺序

    public PONDTO toDTO() {
        return new PONDTO(this);
    }
}