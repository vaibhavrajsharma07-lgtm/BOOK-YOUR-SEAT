
package com.bookyourseat.controller;

import com.bookyourseat.dto.BookingRequest;
import com.bookyourseat.entity.Booking;
import com.bookyourseat.service.BookingService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // CREATE BOOKING
    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest request,
            HttpSession session) {

        try {

            // Get logged-in customer ID from session
            Long userId = (Long) session.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "message", "Please login before booking."
                        ));
            }

            // Validate required information
            if (request.getSalonId() == null
                    || request.getServiceId() == null
                    || request.getAppointmentDate() == null
                    || request.getStartTime() == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message", "Please provide all booking details."
                        ));
            }

            // Create booking through service layer
            Booking booking = bookingService.createBooking(
                    userId,
                    request.getSalonId(),
                    request.getServiceId(),
                    request.getAppointmentDate(),
                    request.getStartTime()
            );

            // Return booking confirmation
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Booking created successfully!",
                            "bookingReference", booking.getBookingReference(),
                            "bookingStatus", booking.getBookingStatus(),
                            "appointmentDate", booking.getAppointmentDate().toString(),
                            "startTime", booking.getStartTime().toString(),
                            "endTime", booking.getEndTime().toString(),
                            "totalPrice", booking.getTotalPrice()
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message", e.getMessage()
                    ));
        }
    }
}
