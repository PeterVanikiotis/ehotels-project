package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.AvailableRoomDto;
import ca.ehotels.backend.model.BookingDto;
import ca.ehotels.backend.model.ConvertBookingRequest;
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
        if (request.getSsn() == null || request.getSsn().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Employee SSN is required.");
        }

        if (request.getDrivingLicenseNumber() == null || request.getDrivingLicenseNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Customer driving license number is required.");
        }

        if (request.getHotelId() == null || request.getRoomNumber() == null) {
            return ResponseEntity.badRequest().body("Hotel and room are required.");
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            return ResponseEntity.badRequest().body("Start and end dates are required.");
        }

        if (!request.getEndDate().isAfter(request.getStartDate())) {
            return ResponseEntity.badRequest().body("End date must be after start date.");
        }

        int rowsInserted = employeeRepository.createDirectRenting(request);

        if (rowsInserted == 0) {
            return ResponseEntity.badRequest().body("Renting failed. Employee, room, or customer was not found.");
        }

        return ResponseEntity.ok("Renting created successfully.");
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(@RequestParam String ssn) {
        EmployeeInfoDto employee = employeeRepository.getEmployeeInfo(ssn);

        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee was not found.");
        }

        List<BookingDto> bookings = employeeRepository.getActiveBookingsForEmployeeHotel(ssn);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/convert-booking-to-renting")
    public ResponseEntity<String> convertBookingToRenting(@RequestBody ConvertBookingRequest request) {
        if (request.getSsn() == null || request.getSsn().trim().isEmpty() || request.getBookingId() == null) {
            return ResponseEntity.badRequest().body("Employee SSN and booking ID are required.");
        }

        if (request.getPaymentConfirmed() == null || !request.getPaymentConfirmed()) {
            return ResponseEntity.badRequest().body("Payment must be confirmed before conversion.");
        }

        try {
            int rows = employeeRepository.convertBookingToRenting(request);

            if (rows == 0) {
                return ResponseEntity.badRequest().body("Conversion failed. Booking was not found or is not active.");
            }

            return ResponseEntity.ok("Booking converted to renting successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Conversion failed: " + e.getMessage());
        }
    }
}