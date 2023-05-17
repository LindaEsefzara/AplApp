package com.Linda.AplApp.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;


@Data
@Entity
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "Förnamn")
    private String firstName;

    @Column(name = "Efternamn")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name="Telefonnummer")
    private Integer phoneNumber;
    @Column(name="Kön")
    private String gender;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<SimpleGrantedAuthority> authorities;


    @Column(name = "active")
    @ColumnDefault("1")
    private int active;

    @Column(name = "user_role")
    private String role;

    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    public User(long id, String firstName, String lastName, String email, String password, Integer phoneNumber, String gender, Set<SimpleGrantedAuthority> authorities, int active, String role, boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired, boolean isEnabled) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.authorities = authorities;
        this.active = active;
        this.role = role;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }

    public int size() {

        return 0;
    }
}
