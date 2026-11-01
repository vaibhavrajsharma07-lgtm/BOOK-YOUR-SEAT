
package com.bookyourseat.repository;

import com.bookyourseat.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsBySalon_IdAndAppointmentDateAndStartTime(
            Long salonId,
            LocalDate appointmentDate,
            LocalTime startTime
    );

}
