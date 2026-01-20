package com.travel.tourbooking.model;

import com.travel.tourbooking.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_phone", columnList = "phoneNumber", unique = true),
        @Index(name = "idx_users_username", columnList = "username", unique = true),
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(unique = true, length = 20)
    private String phoneNumber;

    @Column(length = 255)
    private String password;

    @Column(length = 50)
    private String provider;

    @Column(length = 255)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;
}