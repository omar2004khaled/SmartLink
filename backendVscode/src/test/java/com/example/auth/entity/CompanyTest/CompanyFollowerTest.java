package com.example.auth.entity.CompanyTest;

import com.example.auth.entity.CompanyFollower;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CompanyFollowerTest {

    @Test
    void testCompanyFollowerCreation() {
        CompanyFollower follower = new CompanyFollower();
        follower.setFollowerId(1L);
        follower.setCompanyId(100L);

        assertEquals(1L, follower.getFollowerId());
        assertEquals(100L, follower.getCompanyId());
        assertNull(follower.getFollowedAt());
    }

    @Test
    void testCompanyFollowerAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        CompanyFollower follower = new CompanyFollower(1L, 100L, now);

        assertEquals(1L, follower.getFollowerId());
        assertEquals(100L, follower.getCompanyId());
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