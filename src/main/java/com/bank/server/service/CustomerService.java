package com.bank.server.service;

import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.UpdateCustomerDTO;
import com.bank.server.entity.Customer;
import com.bank.server.enums.LogType;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.exception.EmailAlreadyExistsException;
import com.bank.server.exception.UnderAgeException;
import com.bank.server.mapper.CustomerMapper;
import com.bank.server.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final LoggerService loggerService;

    public CustomerDTO createCustomer(CustomerDTO dto) {

        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "CUSTOMER_CREATE",
                    "Email already exists"
            );
        }

        LocalDate dateOfBirth = LocalDate.parse(dto.getDateOfBirth());
        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();

        if (age < 18) {
            throw new UnderAgeException(
                    "CUSTOMER_CREATE",
                    "Customer must be at least 18 years old"
            );
        }

        Customer customer = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);

        loggerService.log(
                "CUSTOMER_CREATE",
                "Customer created successfully with ID: "
                        + savedCustomer.getId(),
                LogType.SUCCESS
        );

        return customerMapper.toDto(savedCustomer);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public CustomerDTO getCustomerById(String id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "CUSTOMER_FETCH",
                        "Customer not found"
                ));

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

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCustomer(String id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "CUSTOMER_DELETE",
                        "Customer not found"
                ));

        customerRepository.delete(customer);

        loggerService.log(
                "CUSTOMER_DELETE",
                "Customer deleted successfully with ID: " + id,
                LogType.SUCCESS
        );
    }

    public CustomerDTO getMyProfile(String customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "CUSTOMER_FETCH",
                        "Customer not found"
                ));

        loggerService.log(
                "CUSTOMER_FETCH",
                "Customer fetched successfully",
                LogType.SUCCESS
        );

        return customerMapper.toDto(customer);
    }

    public CustomerDTO updateMyProfile(
            String customerId,
            UpdateCustomerDTO updateDto) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "CUSTOMER_UPDATE",
                        "Customer not found"
                ));

        if (updateDto.getEmail() != null) {
            customer.setEmail(updateDto.getEmail());
        }

        if (updateDto.getAddress() != null) {
            customer.setAddress(updateDto.getAddress());
        }

        if (updateDto.getPhone() != null) {
            customer.setPhone(updateDto.getPhone());
        }

        Customer updatedCustomer =
                customerRepository.save(customer);

        loggerService.log(
                "CUSTOMER_UPDATED",
                "Customer updated successfully",
                LogType.SUCCESS
        );

        return customerMapper.toDto(updatedCustomer);
    }
}