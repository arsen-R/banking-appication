package com.arsen.userservice.model.entiry;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private Instant birthday;
    @Column(nullable = false, name = "cell_phone_number")
    private String cellPhoneNumber;
    @OneToOne(mappedBy = "user")
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
