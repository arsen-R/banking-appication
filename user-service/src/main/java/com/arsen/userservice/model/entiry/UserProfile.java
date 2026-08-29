package com.arsen.userservice.model.entiry;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_profiles")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class UserProfile extends BaseEntity {
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    @NonNull
    private Instant birthday;
    @Column(name = "cell_phone_number")
    @NonNull
    private String cellPhoneNumber;
    @OneToOne(mappedBy = "userProfile")
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
