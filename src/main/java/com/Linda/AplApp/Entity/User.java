package com.Linda.AplApp.Entity;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;


@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
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



    public int size() {

        return 0;
    }
    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }


}
