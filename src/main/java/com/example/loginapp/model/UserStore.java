package com.example.loginapp.model;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStore {

    private final ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();

    public UserStore(PasswordEncoder passwordEncoder) {
        users.put("admin", passwordEncoder.encode("password"));
    }

    public boolean exists(String username) {
        return users.containsKey(username);
    }

    public void register(String username, String encodedPassword) {
        users.put(username, encodedPassword);
    }

    public String getEncodedPassword(String username) {
        return users.get(username);
    }
}
