package com.bank.server.controller;

import com.bank.server.dto.CustomerDTO;
import com.bank.server.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Create Customer
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CustomerDTO dto) {

        CustomerDTO createdCustomer = customerService.createCustomer(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdCustomer);
    }

    // Get Customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(
            @PathVariable String id) {

        CustomerDTO customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(customer);
    }

    // Get All Customers
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {

        List<CustomerDTO> customers = customerService.getAllCustomer();

        return ResponseEntity.ok(customers);
    }

    // Update Customer
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(
            @PathVariable String id,
            @Valid @RequestBody CustomerDTO dto) {

        CustomerDTO updatedCustomer =
                customerService.updateCustomer(id, dto);

        return ResponseEntity.ok(updatedCustomer);
    }

    // Delete Customer
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().body("Customer with customerId: " + id + "deleted" );
    }
}