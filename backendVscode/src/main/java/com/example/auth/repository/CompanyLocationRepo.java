package com.example.auth.repository;

import com.example.auth.entity.CompanyLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyLocationRepo extends JpaRepository<CompanyLocation,CompanyLocation.CompanyLocationId> {

    @Query("SELECT cl FROM CompanyLocation cl WHERE cl.companyId = :companyId")
    List<CompanyLocation> findByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Query("DELETE FROM CompanyLocation cl WHERE cl.companyId = :companyId")
    void deleteByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Query("DELETE FROM CompanyLocation cl WHERE cl.companyId = :companyId AND cl.locationId = :locationId")
    void deleteByCompanyIdAndLocationId(@Param("companyId") Long companyId,
                                               @Param("locationId") Long locationId);

    boolean existsByCompanyIdAndLocationId(Long companyId, Long locationId);
}
