package com.example.auth.dto.ProfileDtos;

public class LocationDto {
    private Long locationId;
    private String country;
    private String city;

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
