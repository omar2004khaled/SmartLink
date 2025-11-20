package com.example.auth.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
//This class represents the data structure for user registration requests, typically used in REST API endpoints for authentication.
//This DTO ensures that incoming registration data meets basic validation criteria before being processed by the business logic layer.
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 5, max = 50, message = "Full name must be between 5-50 characters")
    @Pattern(regexp = "^[A-Za-z\\s\\-']+$", message = "Invalid full name format")
    private String fullName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(min = 6, max = 40, message = "Email must be between 6-40 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8-20 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotNull(message = "Gender is required")
    private com.example.auth.enums.Gender gender;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public com.example.auth.enums.Gender getGender() { return gender; }
    public void setGender(com.example.auth.enums.Gender gender) { this.gender = gender; }
}