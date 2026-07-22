package com.bank.server.security;
import com.bank.server.entity.Auth;
import com.bank.server.enums.Role;
import com.bank.server.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private AuthRepository authRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    @Test
    void loadUserByCustomerId_ShouldReturnUserDetails_WhenCustomerExists() {
        Auth auth = new Auth();
        auth.setId("CUST001");
        auth.setUsername("ammar");
        auth.setPasswordHash("password");
        auth.setRole(Role.CUSTOMER);
        when(authRepository.findById("CUST001"))
                .thenReturn(Optional.of(auth));
        UserDetails userDetails =
                customUserDetailsService.loadUserByCustomerId("CUST001");
        assertInstanceOf(CustomUserDetails.class, userDetails);
        assertEquals("CUST001",
                ((CustomUserDetails) userDetails).getCustomerId());
    }
    @Test
    void loadUserByCustomerId_ShouldThrowException_WhenCustomerDoesNotExist() {
        when(authRepository.findById("CUST001"))
                .thenReturn(Optional.empty());
        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByCustomerId("CUST001")
        );
    }
    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUsernameExists() {
        Auth auth = new Auth();
        auth.setId("CUST001");
        auth.setUsername("ammar");
        auth.setPasswordHash("password");
        auth.setRole(Role.CUSTOMER);
        when(authRepository.findByUsername("ammar"))
                .thenReturn(Optional.of(auth));
        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("ammar");
        assertInstanceOf(CustomUserDetails.class, userDetails);
        assertEquals("ammar", userDetails.getUsername());
    }
}