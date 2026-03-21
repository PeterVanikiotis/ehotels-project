package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.AvailableRoomDto;
import ca.ehotels.backend.repository.CustomerSearchRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerController(CustomerSearchRepository customerSearchRepository) {
        this.customerSearchRepository = customerSearchRepository;
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
}