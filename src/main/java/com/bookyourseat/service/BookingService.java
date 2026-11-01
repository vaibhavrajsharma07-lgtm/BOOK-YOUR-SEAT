
package com.bookyourseat.service;

import com.bookyourseat.entity.Booking;
import com.bookyourseat.entity.User;
import com.bookyourseat.entity.Salon;
import com.bookyourseat.entity.SalonService;

import com.bookyourseat.repository.BookingRepository;
import com.bookyourseat.repository.UserRepository;
import com.bookyourseat.repository.SalonRepository;
import com.bookyourseat.repository.SalonServiceRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SalonRepository salonRepository;
    private final SalonServiceRepository salonServiceRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            SalonRepository salonRepository,
            SalonServiceRepository salonServiceRepository) {

        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.salonRepository = salonRepository;
        this.salonServiceRepository = salonServiceRepository;
    }

    @Transactional
    public Booking createBooking(
            Long userId,
            Long salonId,
            Long serviceId,
            LocalDate appointmentDate,
            LocalTime startTime) {

        // 1. Validate appointment date and time
        if (appointmentDate == null || startTime == null) {
            throw new IllegalArgumentException(
                    "Appointment date and time are required."
            );
        }

        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        if (appointmentDate.equals(LocalDate.now())
                && !startTime.isAfter(LocalTime.now())) {
            throw new IllegalArgumentException(
                    "Please select a future appointment time."
            );
        }

        // 2. Retrieve customer
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found."
                ));

        // 3. Retrieve salon
        Salon salon = salonRepository.findById(salonId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Salon not found."
                ));

        if (!"ACTIVE".equalsIgnoreCase(salon.getStatus())) {
            throw new IllegalArgumentException(
                    "This salon is currently unavailable."
            );
        }

        // 4. Retrieve selected service
        SalonService salonService =
                salonServiceRepository.findById(serviceId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Selected service not found."
                        ));

        // 5. Verify service belongs to selected salon
        if (!salonService.getSalon().getId().equals(salonId)) {
            throw new IllegalArgumentException(
                    "This service does not belong to the selected salon."
            );
        }

        // 6. Calculate appointment end time
        LocalTime endTime = startTime.plusMinutes(
                salonService.getDurationMinutes()
        );

        // 7. Validate salon opening hours
        if (salon.getOpeningTime() != null
                && startTime.isBefore(salon.getOpeningTime())) {
            throw new IllegalArgumentException(
                    "Selected time is before salon opening hours."
            );
        }

        if (salon.getClosingTime() != null
                && endTime.isAfter(salon.getClosingTime())) {
            throw new IllegalArgumentException(
                    "Appointment exceeds salon closing hours."
            );
        }

        // 8. Check existing booking
        if (bookingRepository.existsBySalon_IdAndAppointmentDateAndStartTime(
                salonId, appointmentDate, startTime)) {

            throw new IllegalArgumentException(
                    "This time slot is already booked. Please select another time."
            );
        }

        // 9. Generate unique booking reference
        String bookingReference = "BYS-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        // 10. Build booking object
        Booking booking = Booking.builder()
                .bookingReference(bookingReference)
                .user(user)
                .salon(salon)
                .salonService(salonService)
                .appointmentDate(appointmentDate)
                .startTime(startTime)
                .endTime(endTime)
                .totalPrice(salonService.getPrice())
                .bookingStatus("PENDING")
                .build();

        // 11. Save booking in MySQL
        return bookingRepository.save(booking);
    }
}
