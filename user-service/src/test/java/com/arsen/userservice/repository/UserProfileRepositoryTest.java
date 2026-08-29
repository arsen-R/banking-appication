package com.arsen.userservice.repository;

import com.arsen.userservice.model.entiry.UserProfile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
class UserProfileRepositoryTest {
    @Autowired
    private UserProfileRepository userProfileRepository;

    @AfterEach
    void tearDown() {
        userProfileRepository.deleteAll();
    }

    @Test
    void testFindUserProfileByFirstNameShouldReturnUserProfileWhenFirstNameFound() {
        UserProfile userProfile = userProfileRepository.save(new UserProfile("Zara", "Brown", Instant.now().minusSeconds(832479212), "+14847748731"));

        Optional<UserProfile> foundUserProfile = userProfileRepository.findByFirstName("Zara");

        assertTrue(foundUserProfile.isPresent());
        assertEquals(userProfile.getId(), foundUserProfile.get().getId());
        assertEquals(userProfile.getFirstName(), foundUserProfile.get().getFirstName());
        assertEquals(userProfile.getLastName(), foundUserProfile.get().getLastName());
        assertEquals(userProfile.getBirthday(), foundUserProfile.get().getBirthday());
        assertEquals(userProfile.getCellPhoneNumber(), foundUserProfile.get().getCellPhoneNumber());
    }

    @Test
    void testFindUserProfileByFirstNameShouldReturnEmptyWhenFirstNameNotFound() {
        userProfileRepository.save(new UserProfile("Mike", "Cook", Instant.now().minusSeconds(83427897), "+17890096431"));

        Optional<UserProfile> foundUserProfile = userProfileRepository.findByFirstName("Arsen");

        assertTrue(foundUserProfile.isEmpty());
    }

    @Test
    void testFindUserProfileByLastNameShouldReturnUserProfileWhenLastNameFound() {
        UserProfile userProfile = userProfileRepository.save(new UserProfile("Sean", "McDonald", Instant.now().minusSeconds(32763487), "+17607749310"));

        Optional<UserProfile> foundUserProfile = userProfileRepository.findByLastName("McDonald");

        assertTrue(foundUserProfile.isPresent());
        assertEquals(userProfile.getId(), foundUserProfile.get().getId());
        assertEquals(userProfile.getFirstName(), foundUserProfile.get().getFirstName());
        assertEquals(userProfile.getLastName(), foundUserProfile.get().getLastName());
        assertEquals(userProfile.getBirthday(), foundUserProfile.get().getBirthday());
        assertEquals(userProfile.getCellPhoneNumber(), foundUserProfile.get().getCellPhoneNumber());
    }

    @Test
    void testFindUserProfileByLastNameShouldReturnEmptyWhenLastNameNotFound() {
        userProfileRepository.save(new UserProfile("Katherina", "River", Instant.now().minusSeconds(13847483), "+19932661279"));

        Optional<UserProfile> foundUserProfile = userProfileRepository.findByLastName("Andrew");

        assertTrue(foundUserProfile.isEmpty());
    }

    @Test
    void testFindUserProfileByFirstNameAndLastNameShouldReturnUserProfileWhenFirstNameAndLastNameFound() {
        UserProfile userProfile = userProfileRepository.save(new UserProfile("Mario", "Hope", Instant.now().minusSeconds(8231274), "+11932361572"));

        Optional<UserProfile> foundUserProfile = userProfileRepository.findByFirstNameAndLastName("Mario", "Hope");

        assertTrue(foundUserProfile.isPresent());
        assertEquals(userProfile.getId(), foundUserProfile.get().getId());
        assertEquals(userProfile.getFirstName(), foundUserProfile.get().getFirstName());
        assertEquals(userProfile.getLastName(), foundUserProfile.get().getLastName());
        assertEquals(userProfile.getBirthday(), foundUserProfile.get().getBirthday());
        assertEquals(userProfile.getCellPhoneNumber(), foundUserProfile.get().getCellPhoneNumber());
    }

    @Test
    void testFindUserProfileByFirstNameAndLastNameShouldReturnEmptyWhenFirstNameAndLastNameNotFound() {
        userProfileRepository.save(new UserProfile("River", "Hope", Instant.now().minusSeconds(387298), "+11936371872"));

        Optional<UserProfile> foundUserProfile = userProfileRepository.findByFirstNameAndLastName("Maria", "McAlister");

        assertTrue(foundUserProfile.isEmpty());
    }

    @Test
    void testExistsUserProfileByFirstNameShouldReturnTrueWhenFirstNameExists() {
        userProfileRepository.save(new UserProfile("Andrew", "Lake", Instant.now().minusSeconds(39894), "+13218908189"));
        boolean result = userProfileRepository.existsByFirstName("Andrew");
        assertTrue(result);
    }

    @Test
    void testExistsUserProfileByFirstNameShouldReturnFalseWhenFirstNameNotExists() {
        userProfileRepository.save(new UserProfile("George", "Lincoln", Instant.now().minusSeconds(23598923), "+11938193900"));
        boolean result = userProfileRepository.existsByFirstName("Andrew");
        assertFalse(result);
    }

    @Test
    void testExistsUserProfileByLastNameShouldReturnTrueWhenLastNameExists() {
        userProfileRepository.save(new UserProfile("Diana", "Fox", Instant.now().minusSeconds(688990971), "+16771898390"));
        boolean result = userProfileRepository.existsByLastName("Fox");
        assertTrue(result);
    }

    @Test
    void testExistsUserProfileByLastNameShouldReturnFalseWhenLastNameNotExists() {
        userProfileRepository.save(new UserProfile("Richard", "Lawrence", Instant.now().minusSeconds(10000), "+15942349023"));
        boolean result = userProfileRepository.existsByLastName("Jared");
        assertFalse(result);
    }

    @Test
    void testExistsUserProfileByFirstNameAndLastNameShouldReturnTrueWhenFirstNameAndLastNameExists() {
        userProfileRepository.save(new UserProfile("Michael", "Brooks", Instant.now(), "+16771898390"));
        boolean result = userProfileRepository.existsByFirstNameAndLastName("Michael", "Brooks");
        assertTrue(result);
    }

    @Test
    void testExistsUserProfileByFirstNameAndLastNameShouldReturnFalseWhenFirstNameAndLastNameNotExists() {
        userProfileRepository.save(new UserProfile("Emily", "Walsh", Instant.now(), "+13431871893"));
        boolean result = userProfileRepository.existsByFirstNameAndLastName("Eric", "Brooks");
        assertFalse(result);
    }
}