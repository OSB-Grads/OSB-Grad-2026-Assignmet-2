package com.bank.server.service;

import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.UpdateCustomerDTO;
import com.bank.server.entity.Auth;
import com.bank.server.entity.Customer;
import com.bank.server.enums.LogType;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.exception.UsernameAlreadyExistsException;
import com.bank.server.mapper.CustomerMapper;
import com.bank.server.repository.AuthRepository;
import com.bank.server.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final LoggerService loggerService;
    private final AuthRepository authRepository;
    public CustomerDTO createCustomer(CustomerDTO dto) {

        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new UsernameAlreadyExistsException("Email already exists");
        }
        Customer customer = customerMapper.toEntity(dto);
        Customer savedCustomer = customerRepository.save(customer);
        loggerService.log(
                "CUSTOMER_CREATE",
                "Customer created successfully with ID: " + savedCustomer.getId(),
                LogType.SUCCESS
        );
        return customerMapper.toDto(savedCustomer);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    public CustomerDTO getMyProfile(String username) {
        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        String customerId = auth.getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        loggerService.log(
                "CUSTOMER_FETCH",
                "Customer fetched successfully",
                LogType.SUCCESS
        );
        return customerMapper.toDto(customer);
    }
    public CustomerDTO updateMyProfile(String username, UpdateCustomerDTO updateDto) {
        Auth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        String customerId = auth.getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        if(updateDto.getEmail()!=null) {
            customer.setEmail(updateDto.getEmail());
        }
        if(updateDto.getAddress() != null) {
            customer.setAddress(updateDto.getAddress());
        }
        if(updateDto.getPhone() != null) {
            customer.setPhone(updateDto.getPhone());
        }
        Customer updatedCustomer = customerRepository.save(customer);
        loggerService.log("CUSTOMER_UPDATED",
                "Customer updated successfully",
                LogType.SUCCESS);
        return customerMapper.toDto(updatedCustomer);
    }
}
