package com.bank.server.service;

import com.bank.server.dto.CustomerDTO;
import com.bank.server.entity.Customer;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.mapper.CustomerMapper;
import com.bank.server.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final LoggerService loggerService;

    public CustomerDTO createCustomer(CustomerDTO dto) {

        if (customerRepository.existsByUsername(dto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new UsernameAlreadyExistsException("Email already exists");
        }
        Customer customer = customerMapper.toEntity(dto);
        customer.setId(UuidGeneratorUtil.generateUuid());
        customer.setRole("CUSTOMER");
        Customer savedCustomer = customerRepository.save(customer);
        loggerService.log(
                "CUSTOMER_CREATE",
                "Customer created successfully with ID: " + savedCustomer.getId(),
                LogType.SUCCESS
        );
        return customerMapper.toDto(savedCustomer);
    }

    public CustomerDTO getCustomerById(String id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        loggerService.log(
                "CUSTOMER_FETCH",
                "Customer fetched successfully with ID: " + id,
                LogType.SUCCESS
        );
        return customerMapper.toDto(customer);
    }

    public List<CustomerDTO> getAllCustomer() {

        List<CustomerDTO> customers = customerRepository.findAll()
                .stream()
                .map(customerMapper::toDto)
                .toList();

        loggerService.log(
                "CUSTOMER_LIST",
                "Fetched all customers successfully",
                LogType.SUCCESS
        );

        return customers;
    }

    public CustomerDTO updateCustomer(String id, CustomerDTO dto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException
                        ("Customer not found with ID: " + id));
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setNationalId(dto.getNationalId());
        Customer updatedCustomer = customerRepository.save(customer);

        loggerService.log(
                "CUSTOMER_UPDATE",
                "Customer updated successfully with ID: " + id,
                LogType.SUCCESS
        );

        return customerMapper.toDto(updatedCustomer);
    }

    public void deleteCustomer(String id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        customerRepository.delete(customer);

        loggerService.log(
                "CUSTOMER_DELETE",
                "Customer deleted successfully with ID: " + id,
                LogType.SUCCESS
        );
    }
}