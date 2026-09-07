package com.arsen.userservice.model.entiry;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "user_profiles")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends BaseEntity {
    @NonNull
    private String firstName;
    @Nullable
    private String middleName;
    @NonNull
    private String lastName;
    @NonNull
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date birthday;
    @Column(name = "cell_phone_number")
    @NonNull
    private String cellPhoneNumber;
    @OneToOne(mappedBy = "userProfile")
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public UserProfile(String firstName, String lastName, Date birthday, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public UserProfile(String firstName, String middleName, String lastName, Date birthday, String cellPhoneNumber) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.cellPhoneNumber = cellPhoneNumber;
    }
}
