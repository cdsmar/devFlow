package com.example.devFlow.registration;

import com.example.devFlow.user.User;
import com.example.devFlow.user.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class EmailCheckController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public EmailCheckController(UserService userService) {
    	this.userService=userService;
    	this.passwordEncoder = new BCryptPasswordEncoder(); 
    }

    @PostMapping("/check-email")
    public String checkEmail(@ModelAttribute RegistrationRequestEmail registrationRequestEmail, Model model) {
        if (userService.findByEmail(registrationRequestEmail.email()).isPresent()) {
            model.addAttribute("error", "Email already registered.");
            model.addAttribute("registrationRequestEmail", registrationRequestEmail);
            return "signup";
        }

        model.addAttribute("email", registrationRequestEmail.email());
        model.addAttribute("registrationRequest", new RegistrationRequest("", "", "",registrationRequestEmail.email()));
        return "signup_info";
    }
    
    @PostMapping("/check-username")
public String checkUsername(@ModelAttribute RegistrationRequest registrationRequest,
                            Model model,
                            HttpSession session) {

    if (userService.findByUsername(registrationRequest.username()).isPresent()) {
        model.addAttribute("error", "Username already registered.");
        model.addAttribute("registrationRequest", registrationRequest);
        return "signup_info";
    }

    if (registrationRequest.password() == null || registrationRequest.password().length() < 6) {
        model.addAttribute("passwordError", "Password must be at least 6 characters.");
        model.addAttribute("registrationRequest", registrationRequest);
        return "signup_info";
    }

    User savedUser = userService.registerUserDetails(registrationRequest);

    session.setAttribute("username", savedUser.getUsername());
    session.setAttribute("role", savedUser.getRole());
    session.setAttribute("isLoggedIn", true);
    session.setAttribute("userId", savedUser.getId());
    System.out.println("Registered and set session:");
    System.out.println(" - Username: " + savedUser.getUsername());
    System.out.println(" - Role: " + savedUser.getRole());
    System.out.println(" - Id: " + savedUser.getId());
    if ("client".equalsIgnoreCase(registrationRequest.role())) {
        return "redirect:/create_profile?userId=" + savedUser.getId();
    } else if ("developer".equalsIgnoreCase(registrationRequest.role())) {
        return "redirect:/create_profile_dev?userId=" + savedUser.getId();
    }

    return "redirect:/";
}



}
