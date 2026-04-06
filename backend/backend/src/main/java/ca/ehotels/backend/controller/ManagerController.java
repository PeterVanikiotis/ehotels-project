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
        if (hotel == null) return ResponseEntity.status(403).body("Unauthorized Manager SSN.");
        return ResponseEntity.ok(hotel);
    }

    @PostMapping("/hotel/update")
    public ResponseEntity<String> updateHotel(@RequestBody HotelDto hotel) {
        if (hotel.getRating() == null || hotel.getRating() < 1 || hotel.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5.");
        }
        hotelRepository.updateHotel(hotel);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/employees")
    public List<EmployeeDto> getEmployees(@RequestParam Integer hotelId) {
        return employeeRepository.getEmployeesByHotel(hotelId);
    }

    @PostMapping("/employee/save")
    public ResponseEntity<String> saveEmployee(@RequestBody EmployeeDto emp, @RequestParam Integer hotelId) {
        try {
            employeeRepository.saveOrUpdateEmployee(emp, hotelId);
            return ResponseEntity.ok("Saved");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/employee/delete")
    public ResponseEntity<String> deleteEmployee(@RequestParam String targetSsn) {
        try {
            employeeRepository.deleteEmployee(targetSsn);
            return ResponseEntity.ok("Employee removed successfully.");
        } catch (Exception e) {
            // This catches the 'fk_hotel_manager' constraint violation
            return ResponseEntity.badRequest().body("Error: This employee is currently assigned as a Hotel Manager. Reassign the manager role before deleting.");
        }
    }

    @GetMapping("/customers")
    public List<CustomerDto> getCustomers() {
        return customerRepository.getAllCustomers();
    }

    @DeleteMapping("/customer/delete")
    public ResponseEntity<String> deleteCustomer(@RequestParam String license) {
        try {
            customerRepository.deleteCustomer(license);
            return ResponseEntity.ok("Customer removed.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cannot delete: Customer has active bookings.");
        }
    }
}