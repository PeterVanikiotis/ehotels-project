package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.*;
import ca.ehotels.backend.repository.EmployeeRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ca.ehotels.backend.repository.BookingArchiveRepository;
import ca.ehotels.backend.repository.BookingRepository;
import ca.ehotels.backend.repository.RentingArchiveRepository;
import ca.ehotels.backend.repository.RentingRepository;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final BookingRepository bookingRepository;
    private final RentingRepository rentingRepository;
    private final BookingArchiveRepository bookingArchiveRepository;
    private final RentingArchiveRepository rentingArchiveRepository;

    public EmployeeController(
            EmployeeRepository employeeRepository,
            BookingRepository bookingRepository,
            RentingRepository rentingRepository,
            BookingArchiveRepository bookingArchiveRepository,
            RentingArchiveRepository rentingArchiveRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.bookingRepository = bookingRepository;
        this.rentingRepository = rentingRepository;
        this.bookingArchiveRepository = bookingArchiveRepository;
        this.rentingArchiveRepository = rentingArchiveRepository;
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

    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms(@RequestParam String ssn) {
        return ResponseEntity.ok(employeeRepository.getRoomsForEmployeeHotel(ssn));
    }

    @PostMapping("/update-room")
    public ResponseEntity<String> updateRoom(
            @RequestBody RoomDto room,
            @RequestParam String ssn) {

        int rows = employeeRepository.updateRoom(room, ssn);

        if (rows == 0) {
            return ResponseEntity.badRequest().body("Update failed.");
        }

        return ResponseEntity.ok("Room updated successfully.");
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
            BookingDto booking = bookingRepository.findBookingById(request.getBookingId());
            if (booking == null) {
                return ResponseEntity.badRequest().body("Booking not found.");
            }
            int rows = employeeRepository.convertBookingToRenting(request);

            if (rows == 0) {
                return ResponseEntity.badRequest().body("Conversion failed.");
            }
            BookingArchive archive = BookingArchive.fromBooking(booking);
            bookingArchiveRepository.save(archive);
            bookingRepository.deleteById(request.getBookingId());

            return ResponseEntity.ok("Booking converted and archived.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Conversion failed: " + e.getMessage());
        }
    }

    @GetMapping("/rentings")
    public ResponseEntity<?> getRentings(@RequestParam String ssn) {
        EmployeeInfoDto employee = employeeRepository.getEmployeeInfo(ssn);

        if (employee == null) {
            return ResponseEntity.badRequest().body("Employee was not found.");
        }

        List<RentingDto> rentings = employeeRepository.getActiveRentingsForEmployeeHotel(ssn);
        return ResponseEntity.ok(rentings);
    }

    @PostMapping("/checkout-renting")
    public ResponseEntity<String> checkoutRenting(@RequestBody CheckoutRentingRequest request) {
        if (request.getSsn() == null || request.getSsn().trim().isEmpty() || request.getRentingId() == null) {
            return ResponseEntity.badRequest().body("Employee SSN and renting ID are required.");
        }
        try {
            RentingDto renting = rentingRepository.findRentingById(request.getRentingId());
            if (renting == null) {
                return ResponseEntity.badRequest().body("Renting not found.");
            }
            int rows = employeeRepository.checkoutRenting(request);
            if (rows == 0) {
                return ResponseEntity.badRequest().body("Checkout failed.");
            }
            RentingDto updatedRenting = rentingRepository.findRentingById(request.getRentingId());
            RentingArchive archive = RentingArchive.fromRenting(updatedRenting);
            rentingArchiveRepository.save(archive);
            rentingRepository.deleteById(request.getRentingId());

            return ResponseEntity.ok("Customer checked out and archived successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Checkout failed: " + e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getEmployee(@RequestParam String ssn) {
        try {
            EmployeeDto employee = employeeRepository.getEmployee(ssn);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Employee not found.");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateEmployee(@RequestBody EmployeeDto request) {
        if (request.getSsn() == null || request.getSsn().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("SSN is required.");
        }

        int rows = employeeRepository.updateEmployee(request);

        if (rows == 0) {
            return ResponseEntity.badRequest().body("Update failed.");
        }

        return ResponseEntity.ok("Employee updated successfully.");
    }
}