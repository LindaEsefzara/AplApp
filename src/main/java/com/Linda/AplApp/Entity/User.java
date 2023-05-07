package com.Linda.AplApp.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;


@Data
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "fname")
    private String firstName;

    @Column(name = "lname")
    private String lastName;

    @Column(name = "active")
    @ColumnDefault("1")
    private int active;

    @Column(name = "user_role")
    private String role;
}
