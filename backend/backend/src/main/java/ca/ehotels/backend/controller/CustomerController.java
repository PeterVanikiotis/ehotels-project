package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.AvailableRoomDto;
import ca.ehotels.backend.model.AvailableRoomsPerAreaDto;
import ca.ehotels.backend.model.CreateBookingRequest;
import ca.ehotels.backend.model.CustomerDto;
import ca.ehotels.backend.model.HotelCapacityDto;
import ca.ehotels.backend.repository.BookingRepository;
import ca.ehotels.backend.repository.CustomerSearchRepository;
import ca.ehotels.backend.model.CreateCustomerRequest;
import ca.ehotels.backend.repository.CustomerRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerSearchRepository customerSearchRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;

    public CustomerController(
            CustomerSearchRepository customerSearchRepository,
            BookingRepository bookingRepository,
            CustomerRepository customerRepository
    ) {
        this.customerSearchRepository = customerSearchRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/search-rooms")
    public ResponseEntity<?> searchRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) Integer hotelId,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer minTotalRooms,
            @RequestParam(required = false) Double maxPrice
    ) {
        if (!endDate.isAfter(startDate)) {
            return ResponseEntity.badRequest().body("End date must be after start date.");
        }

        List<AvailableRoomDto> rooms = customerSearchRepository.searchAvailableRooms(
                startDate, endDate, capacity, area, hotelId, rating, minTotalRooms, maxPrice
        );
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/hotels-with-capacity")
    public List<HotelCapacityDto> getHotelsWithCapacity(
            @RequestParam(required = false) String area
    ) {
        return customerSearchRepository.getHotelsWithCapacity(area);
    }

    @GetMapping("/available-rooms-per-area")
    public ResponseEntity<?> getAvailableRoomsPerArea(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String area
    ) {
        if (!endDate.isAfter(startDate)) {
            return ResponseEntity.badRequest().body("End date must be after start date.");
        }

        List<AvailableRoomsPerAreaDto> results = customerSearchRepository.getAvailableRoomsPerArea(startDate, endDate, area);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/areas")
    public List<String> getAreas() {
        return customerSearchRepository.getAreas();
    }

    @GetMapping("/get")
    public CustomerDto getCustomer(@RequestParam String license) {
        return customerRepository.getCustomer(license);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateCustomer(@RequestBody CustomerDto request) {
        int rows = customerRepository.updateCustomer(request);

        if (rows == 0) {
            return ResponseEntity.badRequest().body("Update failed");
        }

        return ResponseEntity.ok("Customer updated");
    }

    @PostMapping("/book")
    public ResponseEntity<String> createBooking(@RequestBody CreateBookingRequest request) {
        // Basic date check
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body("End date must be after start date.");
        }

        try {
            bookingRepository.createBooking(request);
            return ResponseEntity.ok("Booking created successfully!");
        } catch (RuntimeException e) {
            // Returns the "License not found" or "Room already booked" message
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An unexpected error occurred.");
        }
    }


    @PostMapping("/create")
    public ResponseEntity<String> createCustomer(@RequestBody CreateCustomerRequest request) {
        if (isBlank(request.getDrivingLicenseNumber())
                || isBlank(request.getFirstName())
                || isBlank(request.getLastName())
                || isBlank(request.getStreetName())
                || isBlank(request.getStreetNumber())
                || isBlank(request.getPostalCode())
                || isBlank(request.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("Please fill in all customer fields.");
        }

        try {
            int rowsInserted = customerRepository.createCustomer(request);

            if (rowsInserted < 2) {
                return ResponseEntity.badRequest().body("Customer creation failed.");
            }

            return ResponseEntity.ok("Customer created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Customer creation failed. That licence or phone may already exist.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}