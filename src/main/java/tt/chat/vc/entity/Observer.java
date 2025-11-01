package tt.chat.vc.entity;

import tt.chat.vc.entity.common.InfoEntity ;
import tt.chat.vc.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table (name = "observer")
@EntityListeners(AuditingEntityListener.class)
public class Observer extends InfoEntity {
    @NotBlank
    @Column(name = "firstname")
    private String firstname;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "lastname")
    private String lastname;
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;
    @Column(name = "rttw")
    private BigDecimal rttw;
    @Column(name = "id_ttwr")
    private String idTtwr;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "observer")
    private List<ObserverImage> images;

    public void addImage(ObserverImage observerImage) {
        if (images == null) {
            images = new ArrayList<>();
        }
        images.add(observerImage);
    }
    @Override
    public String toString() {
        return "Player{" +
                "id=" + getId() +
                ", firstname ='" + firstname + '\'' +
                ", patronymic =" + patronymic +
                ", lastname =" + lastname +
                ", yearOfBirth =" + yearOfBirth +
//                ", manufacturer=" + manufacturer.getName() +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observer observer = (Observer) o;
        return getId().equals(observer.getId()) && firstname.equals(observer.firstname) &&
                patronymic.equals(observer.patronymic) && lastname.equals(observer.lastname) && yearOfBirth.equals(observer.yearOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), firstname, patronymic, lastname, yearOfBirth);
    }
}
