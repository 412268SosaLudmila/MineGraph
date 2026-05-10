package com.minegraph.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    // In-memory users: admin/admin123, analyst/analyst123
    private final Map<String, String[]> users;

    public UserDetailsServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.users = Map.of(
                "admin", new String[]{passwordEncoder.encode("admin123"), "ROLE_ADMIN"},
                "analyst", new String[]{passwordEncoder.encode("analyst123"), "ROLE_ANALYST"}
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] credentials = users.get(username);
        if (credentials == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        return new User(username, credentials[0],
                List.of(new SimpleGrantedAuthority(credentials[1])));
    }
}
