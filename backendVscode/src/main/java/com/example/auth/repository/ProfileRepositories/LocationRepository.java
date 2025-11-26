package com.example.auth.repository.ProfileRepositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auth.entity.ProfileEntities.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}

