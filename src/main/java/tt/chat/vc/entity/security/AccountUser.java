package tt.chat.vc.entity.security;

import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.common.InfoEntity ;
import tt.chat.vc.entity.security.enums.AccountStatus ;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@Entity
@Table(name = "ACCOUNT_USER")
@EntityListeners(AuditingEntityListener.class)
    public class AccountUser extends InfoEntity implements UserDetails {

    private String username;
    private String password;

    private String firstname;
    private String lastname;

    @OneToOne(targetEntity = Observer.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "observer_id", referencedColumnName = "ID")
    private Observer observer;

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")})
    private Set<AccountRole> roles;

    @Transient
    private Set<Authority> authorities;

    @Builder.Default
    private boolean accountNonExpired = false;
    @Builder.Default
    private boolean accountNonLocked = false;
    @Builder.Default
    private boolean credentialsNonExpired = false;
    @Builder.Default
    private boolean enabled = false;
    @Column(name = "email")
    private String email;
    @Column(name = "phone")
    private String phone;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;
    @Column(name = "payment")
    private BigDecimal payment;
    @Builder.Default
    private boolean StatusPayment = false;
    @Override
    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = roles.stream()
                .map(AccountRole::getAuthorities)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()));
        return authorities;
    }
    public AccountUser() {
    }

    @Builder
    public AccountUser(Long id, int version, String createdBy, LocalDateTime createdDate, String lastModifiedBy,
                       LocalDateTime lastModifiedDate, String username, String password, String firstname,
                       String lastname, String email, String phone, AccountStatus status, Set<AccountRole> roles,
                       boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired, boolean enabled) {
        super(id, version, createdBy, createdDate, lastModifiedBy, lastModifiedDate);
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.roles = roles;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }
}
