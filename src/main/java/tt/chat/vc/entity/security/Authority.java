package tt.chat.vc.entity.security;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "AUTHORITY")
public class Authority implements GrantedAuthority{

    static final long serialVersionUID = -3282709929270204703L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String permission;

    @ManyToMany(mappedBy = "authorities")
    private Set<AccountRole> roles;
    @Override
    public String getAuthority() {
        return this.permission;
    }

}