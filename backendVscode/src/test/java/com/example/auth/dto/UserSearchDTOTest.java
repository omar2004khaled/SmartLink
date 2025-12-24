package com.example.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSearchDTOTest {

    @Test
    void constructor_ValidParameters_CreatesDTO() {
        // Arrange
        Long id = 1L;
        String fullName = "John Doe";
        String email = "john.doe@email.com";
        String phoneNumber = "123456789";

        // Act
        String userType = "JOB_SEEKER";

        // Act
        UserSearchDTO dto = new UserSearchDTO(id, fullName, email, phoneNumber, userType);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(fullName, dto.getFullName());
        assertEquals(email, dto.getEmail());
        assertEquals(phoneNumber, dto.getPhoneNumber());
    }

    @Test
    void constructor_NullParameters_CreatesDTO() {
        // Arrange & Act
        UserSearchDTO dto = new UserSearchDTO(null, null, null, null, null);

        // Assert
        assertNull(dto.getId());
        assertNull(dto.getFullName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
    }

    @Test
    void setId_ValidId_SetsId() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");
        Long newId = 2L;

        // Act
        dto.setId(newId);

        // Assert
        assertEquals(newId, dto.getId());
    }

    @Test
    void setId_NullId_SetsNull() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");

        // Act
        dto.setId(null);

        // Assert
        assertNull(dto.getId());
    }

    @Test
    void setFullName_ValidName_SetsName() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");
        String newName = "Jane Doe";

        // Act
        dto.setFullName(newName);

        // Assert
        assertEquals(newName, dto.getFullName());
    }

    @Test
    void setFullName_NullName_SetsNull() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");

        // Act
        dto.setFullName(null);

        // Assert
        assertNull(dto.getFullName());
    }

    @Test
    void setFullName_EmptyName_SetsEmpty() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");
        String emptyName = "";

        // Act
        dto.setFullName(emptyName);

        // Assert
        assertEquals(emptyName, dto.getFullName());
    }

    @Test
    void setEmail_ValidEmail_SetsEmail() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");
        String newEmail = "jane@email.com";

        // Act
        dto.setEmail(newEmail);

        // Assert
        assertEquals(newEmail, dto.getEmail());
    }

    @Test
    void setEmail_NullEmail_SetsNull() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");

        // Act
        dto.setEmail(null);

        // Assert
        assertNull(dto.getEmail());
    }

    @Test
    void setPhoneNumber_ValidPhoneNumber_SetsPhoneNumber() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");
        String newPhone = "987654321";

        // Act
        dto.setPhoneNumber(newPhone);

        // Assert
        assertEquals(newPhone, dto.getPhoneNumber());
    }

    @Test
    void setPhoneNumber_NullPhoneNumber_SetsNull() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");

        // Act
        dto.setPhoneNumber(null);

        // Assert
        assertNull(dto.getPhoneNumber());
    }

    @Test
    void getters_AfterConstruction_ReturnCorrectValues() {
        // Arrange
        Long id = 5L;
        String fullName = "Alice Johnson";
        String email = "alice.johnson@email.com";
        String phoneNumber = "555123456";

        // Act
        String userType = "JOB_SEEKER";

        // Act
        UserSearchDTO dto = new UserSearchDTO(id, fullName, email, phoneNumber, userType);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(fullName, dto.getFullName());
        assertEquals(email, dto.getEmail());
        assertEquals(phoneNumber, dto.getPhoneNumber());
    }

    @Test
    void setters_ChainedCalls_WorkCorrectly() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "John", "john@email.com", "123", "JOB_SEEKER");

        // Act
        dto.setId(10L);
        dto.setFullName("Updated Name");
        dto.setEmail("updated@email.com");
        dto.setPhoneNumber("999888777");

        // Assert
        assertEquals(10L, dto.getId());
        assertEquals("Updated Name", dto.getFullName());
        assertEquals("updated@email.com", dto.getEmail());
        assertEquals("999888777", dto.getPhoneNumber());
    }

    @Test
    void constructor_LongValues_HandlesCorrectly() {
        // Arrange
        Long largeId = Long.MAX_VALUE;
        String longName = "A".repeat(100);
        String longEmail = "a".repeat(50) + "@" + "b".repeat(50) + ".com";
        String longPhone = "1".repeat(20);

        String userType = "JOB_SEEKER";

        // Act
        UserSearchDTO dto = new UserSearchDTO(largeId, longName, longEmail, longPhone, userType);

        // Assert
        assertEquals(largeId, dto.getId());
        assertEquals(longName, dto.getFullName());
        assertEquals(longEmail, dto.getEmail());
        assertEquals(longPhone, dto.getPhoneNumber());
    }

    @Test
    void constructor_SpecialCharacters_HandlesCorrectly() {
        // Arrange
        Long id = 1L;
        String nameWithSpecialChars = "José María O'Connor-Smith";
        String emailWithSpecialChars = "josé.maría@ñoño.com";
        String phoneWithSpecialChars = "+1-555-123-4567";


        String userType = "JOB_SEEKER";

        // Act
        UserSearchDTO dto = new UserSearchDTO(id, nameWithSpecialChars, emailWithSpecialChars, phoneWithSpecialChars, userType);

        // Assert
        assertEquals(id, dto.getId());
        assertEquals(nameWithSpecialChars, dto.getFullName());
        assertEquals(emailWithSpecialChars, dto.getEmail());
        assertEquals(phoneWithSpecialChars, dto.getPhoneNumber());
    }

    @Test
    void setters_OverwriteValues_WorkCorrectly() {
        // Arrange
        UserSearchDTO dto = new UserSearchDTO(1L, "Original", "original@email.com", "111", "JOB_SEEKER");

        // Act - Multiple overwrites
        dto.setFullName("First Update");
        dto.setFullName("Second Update");
        dto.setEmail("first@update.com");
        dto.setEmail("second@update.com");

        // Assert
        assertEquals("Second Update", dto.getFullName());
        assertEquals("second@update.com", dto.getEmail());
    }
}