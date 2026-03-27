package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.AvailableRoomDto;
import ca.ehotels.backend.model.CreateRentingRequest;
import ca.ehotels.backend.model.EmployeeInfoDto;
import ca.ehotels.backend.repository.EmployeeRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/info")
    public ResponseEntity<?> getEmployeeInfo(@RequestParam String ssn) {
        EmployeeInfoDto employee = employeeRepository.getEmployeeInfo(ssn);

        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee was not found.");
        }

        return ResponseEntity.ok(employee);
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<?> getAvailableRooms(
            @RequestParam String ssn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        if (!endDate.isAfter(startDate)) {
            return ResponseEntity.badRequest().body("End date must be after start date.");
        }

        EmployeeInfoDto employee = employeeRepository.getEmployeeInfo(ssn);
        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee was not found.");
        }

        List<AvailableRoomDto> rooms =
                employeeRepository.getAvailableRoomsForEmployeeHotel(ssn, startDate, endDate);

        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/rent")
    public ResponseEntity<String> createRenting(@RequestBody CreateRentingRequest request) {
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body("End date must be after start date.");
        }

        int rowsInserted = employeeRepository.createDirectRenting(request);

        if (rowsInserted == 0) {
            return ResponseEntity.badRequest().body(
                    "Renting failed. Employee, room, or customer was not found."
            );
        }

        return ResponseEntity.ok("Renting created successfully.");
    }
}