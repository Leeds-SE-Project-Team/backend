package com.se.backend.models;

import com.se.backend.projection.TourDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tour")
@Getter
@Setter
public class Tour {
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "tour_highlight_r", joinColumns = @JoinColumn(name = "tour_id"), inverseJoinColumns = @JoinColumn(name = "highlight_id"))
    List<TourHighlight> highlights;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private TourStatus status;
    @Column(length = 50, nullable = false)
    private String startLocation;
    @Column(length = 50, nullable = false)
    private String endLocation;
    @Column(length = 50, nullable = false)
    private String createTime;
    @Column(length = 50, nullable = false)
    private TourType type; // 添加出行类型字段
    // 可选：如果有必经点的需求，可以考虑在这里使用@OneToMany注解关联Waypoints
//    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "tour")
    private List<PON> pons;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private TourCollection tourCollection;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn
    private GroupCollection groupCollection;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user; // 确保与User实体正确关联
    @OneToMany(mappedBy = "tour")
    private List<TourSpot> spots;
    @OneToMany(mappedBy = "tour")
    private List<TourImage> tourImages;

    public TourDTO toDTO() {
        return new TourDTO(this);
    }

    public enum TourType {
        WALK("walk"), RUNNING("running"), DRIVE("drive");

        private final String type;

        TourType(String type) {
            this.type = type;
        }
    }

    public enum TourStatus {
        ONLINE("online"), OFFLINE("offline"), AWAIT_APPROVAL("awaitApproval");
        private final String type;

        TourStatus(String type) {
            this.type = type;
        }
    }

}
