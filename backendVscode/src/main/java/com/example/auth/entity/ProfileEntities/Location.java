package com.example.auth.entity.ProfileEntities;

import jakarta.persistence.*;


@Entity(name = "ProfileLocation")
@Table(name = "profile_location")

public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LocationId")
    private Long locationId;

    @Column(name = "Country", nullable = false)
    private String country;

    @Column(name = "City")
    private String city;


    public Location() {
    }


    public Location(Long locationId, String country, String city) {
        this.locationId = locationId;
        this.country = country;
        this.city = city;
    }

    public Long getLocationId() {
        return this.locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}