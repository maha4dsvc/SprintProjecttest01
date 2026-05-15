package com.example.loginapp.controller;

import com.example.loginapp.model.UserStore;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final UserStore userStore;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserStore userStore, PasswordEncoder passwordEncoder) {
        this.userStore = userStore;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        if (username.isBlank() || password.isBlank()) {
            model.addAttribute("errorMessage", "Username and password are required.");
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Password must be at least 6 characters.");
            return "register";
        }
        if (userStore.exists(username)) {
            model.addAttribute("errorMessage", "Username already taken. Please choose another.");
            return "register";
        }

        userStore.register(username, passwordEncoder.encode(password));
        return "redirect:/login?registered=true";
    }
}
