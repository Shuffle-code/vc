package tt.chat.vc.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tt.chat.vc.entity.common.BaseEntity;

@Entity
@Table(name = "tour_image")
@Setter
@Getter
@NoArgsConstructor
public class TourImage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private Tour tour;

    @Column(name = "path")
    private String path;

    @Builder
    public TourImage(Long id, Tour tour, String path) {
        super(id);
        this.tour = tour;
        this.path = path;
    }
}
