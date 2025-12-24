package com.example.auth.entity.CompanyTest;

import com.example.auth.entity.CompanyFollower;
import com.example.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class CompanyFollowerTest {

    private User followerUser;
    private User companyUser;

    @BeforeEach
    void setUp() {
        followerUser = new User();
        followerUser.setId(1L);

        companyUser = new User();
        companyUser.setId(100L);
    }

    @Test
    void testCompanyFollowerCreation() {
        CompanyFollower follower = new CompanyFollower();
        follower.setFollower(followerUser);
        follower.setCompany(companyUser);

        assertEquals(followerUser, follower.getFollower());
        assertEquals(companyUser, follower.getCompany());
        assertNull(follower.getFollowedAt());
    }

    @Test
    void testCompanyFollowerAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        CompanyFollower follower = new CompanyFollower(followerUser, companyUser, now);

        assertEquals(followerUser, follower.getFollower());
        assertEquals(companyUser, follower.getCompany());
        assertEquals(now, follower.getFollowedAt());
    }

    @Test
    void testCompanyFollowerIdEquals() {
        CompanyFollower.CompanyFollowerId id1 = new CompanyFollower.CompanyFollowerId(1L, 100L);
        CompanyFollower.CompanyFollowerId id2 = new CompanyFollower.CompanyFollowerId(1L, 100L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testCompanyFollowerIdNotEquals() {
        CompanyFollower.CompanyFollowerId id1 = new CompanyFollower.CompanyFollowerId(1L, 100L);
        CompanyFollower.CompanyFollowerId id2 = new CompanyFollower.CompanyFollowerId(2L, 100L);

        assertNotEquals(id1, id2);
    }
}