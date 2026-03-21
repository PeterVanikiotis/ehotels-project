package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.AvailableRoomDto;
import ca.ehotels.backend.model.CreateBookingRequest;
import ca.ehotels.backend.repository.BookingRepository;
import ca.ehotels.backend.repository.CustomerSearchRepository;
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

    public CustomerController(
            CustomerSearchRepository customerSearchRepository,
            BookingRepository bookingRepository
    ) {
        this.customerSearchRepository = customerSearchRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/search-rooms")
    public List<AvailableRoomDto> searchRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) List<Integer> chainIds,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer minTotalRooms,
            @RequestParam(required = false) Double maxPrice
    ) {
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        return customerSearchRepository.searchAvailableRooms(
                startDate,
                endDate,
                capacity,
                area,
                chainIds,
                rating,
                minTotalRooms,
                maxPrice
        );
    }

    @GetMapping("/hotel-chains")
    public List<Map<String, Object>> getHotelChains() {
        return customerSearchRepository.getHotelChains();
    }

    @GetMapping("/areas")
    public List<String> getAreas() {
        return customerSearchRepository.getAreas();
    }

    @PostMapping("/book")
    public ResponseEntity<String> createBooking(@RequestBody CreateBookingRequest request) {
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body("End date must be after start date.");
        }

        int rowsInserted = bookingRepository.createBooking(request);

        if (rowsInserted == 0) {
            return ResponseEntity.badRequest().body("Booking failed. Customer or room was not found.");
        }

        return ResponseEntity.ok("Booking created successfully.");
    }
}