package ca.ehotels.backend.controller;

import ca.ehotels.backend.model.*;
import ca.ehotels.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

    private final ManagerRepository managerRepository;
    private final HotelRepository hotelRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    public ManagerController(ManagerRepository managerRepository,
                             HotelRepository hotelRepository,
                             EmployeeRepository employeeRepository,
                             CustomerRepository customerRepository) {
        this.managerRepository = managerRepository;
        this.hotelRepository = hotelRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String ssn) {
        HotelDto hotel = managerRepository.getManagedHotel(ssn);
        if (hotel == null) return ResponseEntity.status(403).body("Access Denied: You are not a registered Manager.");
        return ResponseEntity.ok(hotel);
    }

    @PostMapping("/hotel/update")
    public ResponseEntity<String> updateHotel(@RequestBody HotelDto hotel) {
        hotelRepository.updateHotel(hotel);
        return ResponseEntity.ok("Hotel information updated.");
    }

    @GetMapping("/employees")
    public List<EmployeeDto> getEmployees(@RequestParam Integer hotelId) {
        return employeeRepository.getEmployeesByHotel(hotelId);
    }

    @DeleteMapping("/customer/delete")
    public ResponseEntity<String> deleteCustomer(@RequestParam String license) {
        try {
            customerRepository.deleteCustomer(license);
            return ResponseEntity.ok("Customer profile permanently removed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete customer: They have active bookings/rentings.");
        }
    }
}