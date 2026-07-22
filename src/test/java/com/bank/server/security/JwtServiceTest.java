package com.bank.server.security;
import com.bank.server.entity.Auth;
import com.bank.server.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "7ca5c89189b501793d2aed198523aacd7af48a9a85084cfc5d0706ca58fc5ef1");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration",
                3600000L);
    }
    @Test
    void generateToken_ShouldReturnToken() {
        Auth auth = new Auth();
        auth.setId("CUST001");
        auth.setUsername("ammar");
        auth.setPasswordHash("password");
        auth.setRole(Role.CUSTOMER);
        CustomUserDetails userDetails = new CustomUserDetails(auth);
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        String customerId = jwtService.extractCustomerId(token);
        assertEquals("CUST001", customerId);
        String role = jwtService.extractRole(token);
        assertEquals("ROLE_CUSTOMER", role);
    }
    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        Auth auth = new Auth();
        auth.setId("CUST001");
        auth.setUsername("ammar");
        auth.setPasswordHash("password");
        auth.setRole(Role.CUSTOMER);
        CustomUserDetails userDetails = new CustomUserDetails(auth);
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }
    @Test
    void isTokenValid_ShouldReturnFalse_WhenCustomerIdDoesNotMatch() {
        Auth auth1 = new Auth();
        auth1.setId("CUST001");
        auth1.setUsername("ammar");
        auth1.setPasswordHash("password");
        auth1.setRole(Role.CUSTOMER);
        CustomUserDetails user1 = new CustomUserDetails(auth1);
        String token = jwtService.generateToken(user1);
        Auth auth2 = new Auth();
        auth2.setId("CUST002");
        auth2.setUsername("john");
        auth2.setPasswordHash("password");
        auth2.setRole(Role.CUSTOMER);
        CustomUserDetails user2 = new CustomUserDetails(auth2);
        boolean isValid = jwtService.isTokenValid(token, user2);
        assertFalse(isValid);
    }
    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        Auth auth = new Auth();
        auth.setId("CUST001");
        auth.setUsername("ammar");
        auth.setPasswordHash("password");
        auth.setRole(Role.CUSTOMER);
        CustomUserDetails userDetails = new CustomUserDetails(auth);
        String token = jwtService.generateToken(userDetails);
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }
}