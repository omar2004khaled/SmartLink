package com.example.auth.repository;

import com.example.auth.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LocationRepo extends JpaRepository<Location,Long> {
    Optional<Location> findByCityAndCountry(String city,String Country);
//    Optional<Location> findById(Long locationId);


}
