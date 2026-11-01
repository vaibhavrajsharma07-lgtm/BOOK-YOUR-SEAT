

package com.bookyourseat.repository;

import com.bookyourseat.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalonServiceRepository
        extends JpaRepository<SalonService, Long> {

    // Find all services belonging to a particular salon
    List<SalonService> findBySalonId(Long salonId);

}
