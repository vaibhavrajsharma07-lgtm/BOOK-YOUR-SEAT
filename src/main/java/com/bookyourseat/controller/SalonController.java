
package com.bookyourseat.controller;

import com.bookyourseat.entity.Salon;
import com.bookyourseat.entity.SalonService;
import com.bookyourseat.repository.SalonRepository;
import com.bookyourseat.repository.SalonServiceRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salons")
@CrossOrigin
public class SalonController {

    private final SalonRepository salonRepository;
    private final SalonServiceRepository salonServiceRepository;

    public SalonController(
            SalonRepository salonRepository,
            SalonServiceRepository salonServiceRepository) {

        this.salonRepository = salonRepository;
        this.salonServiceRepository = salonServiceRepository;
    }

    // Get all salons
    @GetMapping
    public List<Map<String, Object>> getAllSalons() {

        return salonRepository.findAll()
                .stream()
                .map(this::salonToMap)
                .collect(Collectors.toList());
    }

    // Get salon details with its services
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSalonById(
            @PathVariable Long id) {

        Optional<Salon> optionalSalon = salonRepository.findById(id);

        if (optionalSalon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Salon salon = optionalSalon.get();

        List<Map<String, Object>> services =
                salonServiceRepository.findBySalonId(id)
                        .stream()
                        .map(this::serviceToMap)
                        .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("salon", salonToMap(salon));
        response.put("services", services);

        return ResponseEntity.ok(response);
    }

    // Convert salon details into JSON-friendly data
    private Map<String, Object> salonToMap(Salon salon) {

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("id", salon.getId());
        data.put("salonName", salon.getSalonName());
        data.put("address", salon.getAddress());
        data.put("city", salon.getCity());
        data.put("openingTime", salon.getOpeningTime());
        data.put("closingTime", salon.getClosingTime());
        data.put("description", salon.getDescription());
        data.put("email", salon.getEmail());
        data.put("phone", salon.getPhone());
        data.put("rating", salon.getRating());
        data.put("image", salon.getImage());
        data.put("status", salon.getStatus());

        return data;
    }

    // Convert service details into JSON-friendly data
    private Map<String, Object> serviceToMap(SalonService service) {

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("id", service.getId());
        data.put("serviceName", service.getServiceName());
        data.put("description", service.getDescription());
        data.put("durationMinutes", service.getDurationMinutes());
        data.put("price", service.getPrice());
        data.put("salonId", service.getSalon().getId());

        return data;
    }
}
