package com.example.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Entity
@Table(name = "Company_Locations")
@IdClass(CompanyLocation.CompanyLocationId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyLocation {

    @Id
    @Column(name = "CompanyId")
    private Long companyId;

    @Id
    @Column(name = "LocationId")
    private Long locationId;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyLocationId implements Serializable {
        private Long companyId;
        private Long locationId;
    }
}
