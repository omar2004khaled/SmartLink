package com.example.auth.service;

import com.example.auth.dto.UserSearchDTO;
import com.example.auth.entity.User;
import com.example.auth.enums.Gender;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SearchService searchService;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setFullName("John Doe");
        testUser1.setEmail("john.doe@email.com");
        testUser1.setPhoneNumber("123456789");
        testUser1.setEnabled(true);
        testUser1.setUserType("JOB_SEEKER");
        testUser1.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser1.setGender(Gender.MALE);

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setFullName("Jane Smith");
        testUser2.setEmail("jane.smith@email.com");
        testUser2.setPhoneNumber("987654321");
        testUser2.setEnabled(true);
        testUser2.setUserType("JOB_SEEKER");
        testUser2.setBirthDate(LocalDate.of(1992, 5, 15));
        testUser2.setGender(Gender.FEMALE);

        testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setFullName("Johnny Johnson");
        testUser3.setEmail("johnny.johnson@email.com");
        testUser3.setPhoneNumber("555666777");
        testUser3.setEnabled(true);
        testUser3.setUserType("JOB_SEEKER");
        testUser3.setBirthDate(LocalDate.of(1988, 12, 25));
        testUser3.setGender(Gender.MALE);
    }

    @Test
    void searchUsers_ValidQuery_ReturnsMatchingUsers() {
        // Arrange
        String query = "john";
        Long currentUserId = 5L;
        List<User> mockUsers = Arrays.asList(testUser1, testUser3);
        
        when(userRepository.searchUsers(query, currentUserId)).thenReturn(mockUsers);

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        UserSearchDTO dto1 = result.get(0);
        assertEquals(testUser1.getId(), dto1.getId());
        assertEquals(testUser1.getFullName(), dto1.getFullName());
        assertEquals(testUser1.getEmail(), dto1.getEmail());
        assertEquals(testUser1.getPhoneNumber(), dto1.getPhoneNumber());
        
        UserSearchDTO dto2 = result.get(1);
        assertEquals(testUser3.getId(), dto2.getId());
        assertEquals(testUser3.getFullName(), dto2.getFullName());
        assertEquals(testUser3.getEmail(), dto2.getEmail());
        assertEquals(testUser3.getPhoneNumber(), dto2.getPhoneNumber());

        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_NoMatches_ReturnsEmptyList() {
        // Arrange
        String query = "nonexistent";
        Long currentUserId = 1L;
        
        when(userRepository.searchUsers(query, currentUserId)).thenReturn(Collections.emptyList());

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_NullQuery_ThrowsIllegalArgumentException() {
        // Arrange
        String query = null;
        Long currentUserId = 1L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> searchService.searchUsers(query, currentUserId)
        );

        assertEquals("Search query cannot be empty", exception.getMessage());
        verify(userRepository, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_EmptyQuery_ThrowsIllegalArgumentException() {
        // Arrange
        String query = "";
        Long currentUserId = 1L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> searchService.searchUsers(query, currentUserId)
        );

        assertEquals("Search query cannot be empty", exception.getMessage());
        verify(userRepository, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_WhitespaceQuery_ThrowsIllegalArgumentException() {
        // Arrange
        String query = "   ";
        Long currentUserId = 1L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> searchService.searchUsers(query, currentUserId)
        );

        assertEquals("Search query cannot be empty", exception.getMessage());
        verify(userRepository, never()).searchUsers(anyString(), anyLong());
    }

    @Test
    void searchUsers_QueryWithLeadingTrailingSpaces_TrimsAndSearches() {
        // Arrange
        String query = "  john  ";
        String trimmedQuery = "john";
        Long currentUserId = 1L;
        List<User> mockUsers = Arrays.asList(testUser1);
        
        when(userRepository.searchUsers(trimmedQuery, currentUserId)).thenReturn(mockUsers);

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser1.getFullName(), result.get(0).getFullName());

        verify(userRepository, times(1)).searchUsers(trimmedQuery, currentUserId);
    }

    @Test
    void searchUsers_SingleUser_ReturnsCorrectDTO() {
        // Arrange
        String query = "jane";
        Long currentUserId = 1L;
        List<User> mockUsers = Arrays.asList(testUser2);
        
        when(userRepository.searchUsers(query, currentUserId)).thenReturn(mockUsers);

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        UserSearchDTO dto = result.get(0);
        assertEquals(testUser2.getId(), dto.getId());
        assertEquals(testUser2.getFullName(), dto.getFullName());
        assertEquals(testUser2.getEmail(), dto.getEmail());
        assertEquals(testUser2.getPhoneNumber(), dto.getPhoneNumber());

        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_MultipleUsers_ReturnsAllDTOs() {
        // Arrange
        String query = "test";
        Long currentUserId = 10L;
        List<User> mockUsers = Arrays.asList(testUser1, testUser2, testUser3);
        
        when(userRepository.searchUsers(query, currentUserId)).thenReturn(mockUsers);

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify all users are converted to DTOs
        assertEquals(testUser1.getId(), result.get(0).getId());
        assertEquals(testUser2.getId(), result.get(1).getId());
        assertEquals(testUser3.getId(), result.get(2).getId());

        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_UserWithNullPhoneNumber_HandlesGracefully() {
        // Arrange
        String query = "test";
        Long currentUserId = 1L;
        
        User userWithNullPhone = new User();
        userWithNullPhone.setId(4L);
        userWithNullPhone.setFullName("Test User");
        userWithNullPhone.setEmail("test@email.com");
        userWithNullPhone.setPhoneNumber(null);
        
        List<User> mockUsers = Arrays.asList(userWithNullPhone);
        
        when(userRepository.searchUsers(query, currentUserId)).thenReturn(mockUsers);

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        
        UserSearchDTO dto = result.get(0);
        assertEquals(userWithNullPhone.getId(), dto.getId());
        assertEquals(userWithNullPhone.getFullName(), dto.getFullName());
        assertEquals(userWithNullPhone.getEmail(), dto.getEmail());
        assertNull(dto.getPhoneNumber());

        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_RepositoryThrowsException_PropagatesException() {
        // Arrange
        String query = "test";
        Long currentUserId = 1L;
        RuntimeException repositoryException = new RuntimeException("Database error");
        
        when(userRepository.searchUsers(query, currentUserId)).thenThrow(repositoryException);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> searchService.searchUsers(query, currentUserId)
        );

        assertEquals("Database error", exception.getMessage());
        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_CaseInsensitiveQuery_CallsRepository() {
        // Arrange
        String query = "JOHN";
        Long currentUserId = 1L;
        List<User> mockUsers = Arrays.asList(testUser1);
        
        when(userRepository.searchUsers(query, currentUserId)).thenReturn(mockUsers);

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }

    @Test
    void searchUsers_SpecialCharactersInQuery_CallsRepository() {
        // Arrange
        String query = "john@doe.com";
        Long currentUserId = 1L;
        List<User> mockUsers = Collections.emptyList();
        
        when(userRepository.searchUsers(query, currentUserId)).thenReturn(mockUsers);

        // Act
        List<UserSearchDTO> result = searchService.searchUsers(query, currentUserId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).searchUsers(query, currentUserId);
    }
}