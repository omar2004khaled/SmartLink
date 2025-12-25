package com.example.auth.entity.CompanyTest;

import com.example.auth.entity.CompanyLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
@ExtendWith(MockitoExtension.class)
class CompanyLocationTest {

    @Test
    void testCompanyLocationCreation() {
        CompanyLocation location = new CompanyLocation();
        location.setCompanyId(100L);
        location.setLocationId(1L);

        assertEquals(100L, location.getCompanyId());
        assertEquals(1L, location.getLocationId());
    }

    @Test
    void testCompanyLocationAllArgsConstructor() {
        CompanyLocation location = new CompanyLocation(100L, 1L);

        assertEquals(100L, location.getCompanyId());
        assertEquals(1L, location.getLocationId());
    }

    @Test
    void testCompanyLocationIdEquals() {
        CompanyLocation.CompanyLocationId id1 = new CompanyLocation.CompanyLocationId(100L, 1L);
        CompanyLocation.CompanyLocationId id2 = new CompanyLocation.CompanyLocationId(100L, 1L);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testCompanyLocationIdNotEquals() {
        CompanyLocation.CompanyLocationId id1 = new CompanyLocation.CompanyLocationId(100L, 1L);
        CompanyLocation.CompanyLocationId id2 = new CompanyLocation.CompanyLocationId(100L, 2L);

        assertNotEquals(id1, id2);
    }
}