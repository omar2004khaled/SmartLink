package com.example.auth.dto;


import com.example.auth.dto.ProfileDtos.SemiEducationDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SemiEducationDtoTest {

    @Test
    void testGetterAndSetterMethods() {
        // Given
        SemiEducationDto dto = new SemiEducationDto();

        // When
        dto.setId(1L);
        dto.setFieldOfStudy("Computer Science");
        dto.setDescription("Bachelor's degree in Computer Science");

        // Then
        assertEquals(1L, dto.getId());
        assertEquals("Computer Science", dto.getFieldOfStudy());
        assertEquals("Bachelor's degree in Computer Science", dto.getDescription());
    }

    @Test
    void testDefaultConstructor() {
        // When
        SemiEducationDto dto = new SemiEducationDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getFieldOfStudy());
        assertNull(dto.getDescription());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        SemiEducationDto dto1 = new SemiEducationDto();
        dto1.setId(1L);
        dto1.setFieldOfStudy("Computer Science");

        SemiEducationDto dto2 = new SemiEducationDto();
        dto2.setId(1L);
        dto2.setFieldOfStudy("Computer Science");

        SemiEducationDto dto3 = new SemiEducationDto();
        dto3.setId(2L);
        dto3.setFieldOfStudy("Mathematics");

        // Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        SemiEducationDto dto = new SemiEducationDto();
        dto.setId(1L);
        dto.setFieldOfStudy("Physics");
        dto.setDescription("Master's in Physics");

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Physics"));
        assertTrue(toString.contains("1"));
    }

    @Test
    void testFieldOfStudyEdgeCases() {
        // Given
        SemiEducationDto dto = new SemiEducationDto();

        // When - Empty string
        dto.setFieldOfStudy("");
        assertEquals("", dto.getFieldOfStudy());

        // When - Long string
        String longField = "International Business Administration with focus on Digital Marketing";
        dto.setFieldOfStudy(longField);
        assertEquals(longField, dto.getFieldOfStudy());

        // When - Special characters
        dto.setFieldOfStudy("Computer Science & Engineering (CSE)");
        assertEquals("Computer Science & Engineering (CSE)", dto.getFieldOfStudy());
    }

    @Test
    void testDescriptionEdgeCases() {
        // Given
        SemiEducationDto dto = new SemiEducationDto();

        // When - Empty description
        dto.setDescription("");
        assertEquals("", dto.getDescription());

        // When - Very long description
        String longDesc = "Completed a comprehensive program covering algorithms, data structures, " +
                "software engineering principles, database management, web technologies, " +
                "and machine learning fundamentals with a GPA of 3.8/4.0.";
        dto.setDescription(longDesc);
        assertEquals(longDesc, dto.getDescription());

        // When - Description with line breaks
        dto.setDescription("Bachelor's Degree\nMajor: Computer Science\nMinor: Mathematics");
        assertEquals("Bachelor's Degree\nMajor: Computer Science\nMinor: Mathematics", dto.getDescription());
    }

    @Test
    void testNullId() {
        // Given
        SemiEducationDto dto = new SemiEducationDto();

        // When
        dto.setId(null);
        dto.setFieldOfStudy("Engineering");

        // Then
        assertNull(dto.getId());
        assertEquals("Engineering", dto.getFieldOfStudy());
    }

    @Test
    void testObjectIdentity() {
        // Given
        SemiEducationDto dto1 = new SemiEducationDto();
        dto1.setId(1L);

        SemiEducationDto dto2 = dto1;

        // Then
        assertSame(dto1, dto2);
    }
}