package com.example.loginapp.service;

import com.example.loginapp.model.UserStore;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserStore userStore;

    public CustomUserDetailsService(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String encodedPassword = userStore.getEncodedPassword(username);
        if (encodedPassword == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return User.withUsername(username)
                .password(encodedPassword)
                .roles("USER")
                .build();
    }
}
