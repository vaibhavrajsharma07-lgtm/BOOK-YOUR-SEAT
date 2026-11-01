
package com.bookyourseat.controller;

import com.bookyourseat.entity.Booking;
import com.bookyourseat.repository.BookingRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/my-bookings")
public class MyBookingController {

    private final BookingRepository bookingRepository;

    public MyBookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public ResponseEntity<?> getMyBookings(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please login first."));
        }

        List<Booking> bookings = bookingRepository.findAll().stream()
                .filter(booking ->
                        booking.getUser().getId().equals(userId))
                .toList();

        List<Map<String, Object>> response = bookings.stream()
                .map(booking -> Map.<String, Object>of(
                        "id", booking.getId(),
                        "bookingReference", booking.getBookingReference(),
                        "salonName", booking.getSalon().getSalonName(),
                        "serviceName", booking.getSalonService().getServiceName(),
                        "appointmentDate", booking.getAppointmentDate().toString(),
                        "startTime", booking.getStartTime().toString(),
                        "endTime", booking.getEndTime().toString(),
                        "totalPrice", booking.getTotalPrice(),
                        "bookingStatus", booking.getBookingStatus()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}
