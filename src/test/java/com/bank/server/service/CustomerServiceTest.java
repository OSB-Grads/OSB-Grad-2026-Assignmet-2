package com.bank.server.service;

import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.UpdateCustomerDTO;
import com.bank.server.entity.Customer;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.exception.EmailAlreadyExistsException;
import com.bank.server.exception.UnderAgeException;
import com.bank.server.mapper.CustomerMapper;
import com.bank.server.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private LoggerService loggerService;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getCustomerById_ShouldReturnCustomerDTO() {
        Customer customer = new Customer();
        customer.setId("1");
        customer.setFirstName("Ammar");
        customer.setLastName("Hasan");
        customer.setEmail("ammar@gmail.com");
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId("1");
        customerDTO.setFirstName("Ammar");
        customerDTO.setLastName("Hasan");
        customerDTO.setEmail("ammar@gmail.com");
        when(customerRepository.findById("1"))
                .thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer))
                .thenReturn(customerDTO);
        CustomerDTO result = customerService.getCustomerById("1");
        assertEquals(customerDTO, result);
        verify(customerRepository).findById("1");
        verify(customerMapper).toDto(customer);
        verify(loggerService).log(anyString(), anyString(), any());
    }
    @Test
    void getCustomerById_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.findById("1"))
                .thenReturn(Optional.empty());
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerById("1")
        );
        verify(customerRepository).findById("1");
    }
    @Test
    void createCustomer_ShouldReturnCustomerDTO() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId("1");
        dto.setFirstName("Ammar");
        dto.setLastName("Hasan");
        dto.setEmail("ammar@gmail.com");
        dto.setDateOfBirth("2000-05-20");
        Customer customer = new Customer();
        customer.setId("1");
        customer.setFirstName("Ammar");
        customer.setLastName("Hasan");
        customer.setEmail("ammar@gmail.com");
        when(customerRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);
        when(customerMapper.toEntity(dto))
                .thenReturn(customer);
        when(customerRepository.save(customer))
                .thenReturn(customer);
        when(customerMapper.toDto(customer))
                .thenReturn(dto);
        CustomerDTO result = customerService.createCustomer(dto);
        assertEquals(dto, result);
        verify(customerRepository).existsByEmail(dto.getEmail());
        verify(customerMapper).toEntity(dto);
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(customer);
        verify(loggerService).log(anyString(), anyString(), any());
    }
    @Test
    void createCustomer_ShouldThrowException_WhenEmailAlreadyExists() {
        CustomerDTO dto = new CustomerDTO();
        dto.setEmail("ammar@gmail.com");
        dto.setDateOfBirth("2000-05-20");
        when(customerRepository.existsByEmail(dto.getEmail()))
                .thenReturn(true);
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> customerService.createCustomer(dto)
        );
        verify(customerRepository).existsByEmail(dto.getEmail());
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toEntity(any());
        verify(customerMapper, never()).toDto(any());
        verify(loggerService, never()).log(anyString(), anyString(), any());
    }
    @Test
    void createCustomer_ShouldThrowException_WhenCustomerIsUnderAge() {
        CustomerDTO dto = new CustomerDTO();
        dto.setEmail("ammar@gmail.com");
        dto.setDateOfBirth("2012-01-01");
        when(customerRepository.existsByEmail(dto.getEmail()))
                .thenReturn(false);
        assertThrows(
                UnderAgeException.class,
                () -> customerService.createCustomer(dto)
        );
        verify(customerRepository).existsByEmail(dto.getEmail());
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toEntity(any());
        verify(customerMapper, never()).toDto(any());
        verify(loggerService, never()).log(anyString(), anyString(), any());
    }
    @Test
    void updateMyProfile_ShouldReturnUpdatedCustomerDTO() {
        Customer customer = new Customer();
        customer.setId("1");
        customer.setEmail("old@gmail.com");
        customer.setPhone("1111111111");
        customer.setAddress("Old Address");
        UpdateCustomerDTO updateDTO = new UpdateCustomerDTO();
        updateDTO.setEmail("new@gmail.com");
        updateDTO.setPhone("9999999999");
        updateDTO.setAddress("New Address");
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId("1");
        customerDTO.setEmail("new@gmail.com");
        customerDTO.setPhone("9999999999");
        customerDTO.setAddress("New Address");
        when(customerRepository.findById("1"))
                .thenReturn(Optional.of(customer));
        when(customerRepository.save(customer))
                .thenReturn(customer);
        when(customerMapper.toDto(customer))
                .thenReturn(customerDTO);
        CustomerDTO result =
                customerService.updateMyProfile("1", updateDTO);
        assertEquals(customerDTO, result);
        assertEquals("new@gmail.com", customer.getEmail());
        assertEquals("9999999999", customer.getPhone());
        assertEquals("New Address", customer.getAddress());
        verify(customerRepository).findById("1");
        verify(customerRepository).save(customer);
        verify(customerMapper).toDto(customer);
        verify(loggerService).log(anyString(), anyString(), any());
    }

    @Test
    void getAllCustomer_ShouldReturnCustomerDTOList() {
        Customer customer1 = new Customer();
        customer1.setId("1");
        customer1.setFirstName("Ammar");
        Customer customer2 = new Customer();
        customer2.setId("2");
        customer2.setFirstName("John");
        CustomerDTO dto1 = new CustomerDTO();
        dto1.setId("1");
        dto1.setFirstName("Ammar");
        CustomerDTO dto2 = new CustomerDTO();
        dto2.setId("2");
        dto2.setFirstName("John");
        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));
        when(customerMapper.toDto(customer1))
                .thenReturn(dto1);
        when(customerMapper.toDto(customer2))
                .thenReturn(dto2);
        List<CustomerDTO> result = customerService.getAllCustomer();
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(customerRepository).findAll();
        verify(customerMapper).toDto(customer1);
        verify(customerMapper).toDto(customer2);
        verify(loggerService).log(anyString(), anyString(), any());
    }
    @Test
    void getMyProfile_ShouldReturnCustomerDTO() {
        Customer customer = new Customer();
        customer.setId("1");
        customer.setFirstName("Ammar");
        CustomerDTO dto = new CustomerDTO();
        dto.setId("1");
        dto.setFirstName("Ammar");
        when(customerRepository.findById("1"))
                .thenReturn(Optional.of(customer));
        when(customerMapper.toDto(customer))
                .thenReturn(dto);
        CustomerDTO result = customerService.getMyProfile("1");
        assertEquals(dto, result);
        verify(customerRepository).findById("1");
        verify(customerMapper).toDto(customer);
        verify(loggerService).log(anyString(), anyString(), any());
    }
    @Test
    void getMyProfile_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.findById("1"))
                .thenReturn(Optional.empty());
        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getMyProfile("1")
        );
        verify(customerRepository).findById("1");
        verify(customerMapper, never()).toDto(any());
        verify(loggerService, never()).log(anyString(), anyString(), any());
    }
}
