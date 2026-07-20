package com.bank.server.controller;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.UpdateCustomerDTO;
import com.bank.server.security.CustomUserDetails;
import com.bank.server.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
//    @PostMapping
//    public ResponseEntity<CustomerDTO> createCustomer(
//            @Valid @RequestBody CustomerDTO dto) {
//
//        CustomerDTO createdCustomer = customerService.createCustomer(dto);
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(createdCustomer);
//    }
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @PathVariable String id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomer();
        return ResponseEntity.ok(customers);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().body("Customer deleted successfully" );
    }
    @GetMapping("/me")
    public ResponseEntity<CustomerDTO> getMyProfile(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String customerId = user.getCustomerId();
        return ResponseEntity.ok(
                customerService.getMyProfile(customerId)
        );
    }
    @PatchMapping("/me")
    public ResponseEntity<CustomerDTO> updateMyProfile
            (Authentication authentication, @RequestBody UpdateCustomerDTO updateDto) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String customerId = user.getCustomerId();
        return ResponseEntity.ok(
                customerService.updateMyProfile(customerId,updateDto)
        );
    }
}