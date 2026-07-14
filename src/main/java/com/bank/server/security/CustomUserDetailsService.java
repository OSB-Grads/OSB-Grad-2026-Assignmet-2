package com.bank.server.security;

import com.bank.server.entity.Auth;
import com.bank.server.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthRepository authRepository;
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Auth auth  = authRepository.findByUsername(username).orElseThrow(()
                -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(auth);
    }
}
