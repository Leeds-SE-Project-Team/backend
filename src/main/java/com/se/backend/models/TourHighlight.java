package com.se.backend.models;

import com.se.backend.projection.TourHighlightDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "tour_highlight")
@Getter
@Setter
public class TourHighlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

//    @Column(nullable = false)
//    private String imageUrl;

    @Column(nullable = false)
    private String location;

    @ManyToMany(mappedBy = "highlights")
    private List<Tour> tours;// 关联到Trip实体

    @OneToMany(mappedBy = "tourHighlight", cascade = CascadeType.ALL)
    private List<TourImage> tourImages;

    public TourHighlightDTO toDTO() {
        return new TourHighlightDTO(this);
    }
}

//highlight一对多tour highlight

//tour highlight 外键指向highlight 1对一
//tour highlight 外键指向tour 多对一
//只为了一个tourhighlight 有多个图片
//拆出来很多个records