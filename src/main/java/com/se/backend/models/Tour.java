package com.se.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se.backend.projection.TourDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tour")
@Getter
@Setter
public class Tour {
    @ManyToMany
    @JoinTable(name = "tour_highlight_r", joinColumns = @JoinColumn(name = "tour_id"), inverseJoinColumns = @JoinColumn(name = "highlight_id"))
    List<TourHighlight> highlights;
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "user_likes_tour", joinColumns = @JoinColumn(name = "tour_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> likedBy;
    @JsonIgnore
    @JoinTable(name = "user_stars_tour", joinColumns = @JoinColumn(name = "star_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ManyToMany(cascade = CascadeType.MERGE)
    Set<User> starredBy;
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
    @Column(length = 50, nullable = false)
    private TourState state; //添加出行状态字段
    @Column(length = 100, nullable = false)
    private String mapUrl;
    @Column(length = 100, nullable = false)
    private String dataUrl;
    @Column(length = 100, nullable = false)
    private String completeUrl;
    // 可选：如果有必经点的需求，可以考虑在这里使用@OneToMany注解关联Waypoints
//    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PON> pons;
    @ManyToOne
    @JoinColumn(nullable = false)
    private TourCollection tourCollection;
    @ManyToOne
    private GroupCollection groupCollection;
    @ManyToOne
    private User user; // 确保与User实体正确关联
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourSpot> spots;
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourImage> tourImages;
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments; // 添加这一行来确保级联删除
    //Tour 与 TourRecordData 单向关系
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private TourRecordData tourRecordData;

    public TourDTO toDTO() {
        return new TourDTO(this);
    }

    @Getter
    public enum TourType {
        WALK("walk"), RUNNING("running"), DRIVE("drive");

        private final String type;

        TourType(String type) {
            this.type = type;
        }
    }

    @Getter
    public enum TourState {
        UNFINISHED("unfinished"), ONGOING("ongoing"), FINISHED("finished");

        private final String state;

        TourState(String state) {
            this.state = state;
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
