package tt.chat.vc.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "observer_image")
@Setter
@Getter
@NoArgsConstructor
public class ObserverImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "observer_id")
    private Observer observer;

    @Column(name = "path")
    private String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObserverImage)) return false;

        ObserverImage that = (ObserverImage) o;

        return path.equals(that.path);
    }


    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Builder
    public ObserverImage(Long id, Observer observer, String path) {
        this.id = id;
        this.observer = observer;
        this.path = path;
    }
}
