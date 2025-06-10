package com.example.devFlow.profile;

import com.example.devFlow.user.User;
import com.example.devFlow.user.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class DeveloperProfileController {
    

    private final DeveloperProfileRepository developerProfileRepository;
    private final UserRepository userRepository;

    public DeveloperProfileController(DeveloperProfileRepository developerProfileRepository, UserRepository userRepository) {
        this.developerProfileRepository = developerProfileRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/create_profile_dev")
    public String showDeveloperProfileForm(@RequestParam("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "create_profile_dev"; 
    }

    @PostMapping("/create_dev")
    public String createDeveloperProfile(@RequestParam("userId") Long userId,
                                         @RequestParam("firstName") String firstName,
                                         @RequestParam("lastName") String lastName,
                                         @RequestParam("gender") String gender,
                                         @RequestParam("description") String description,
                                         @RequestParam("skills") String skillsInput,
                                         @RequestParam("cvFile") MultipartFile cvFile) throws IOException {

        User user = userRepository.findById(userId).orElseThrow();

        DeveloperProfile profile = new DeveloperProfile();
        profile.setUser(user);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setGender(gender);
        profile.setDescription(description);

        if (!cvFile.isEmpty()) {
            String projectDir = System.getProperty("user.dir");
            String uploadDir = projectDir + File.separator + "cvs" + File.separator + userId;
        
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
        
            String filePath = uploadDir + File.separator + cvFile.getOriginalFilename();
            File dest = new File(filePath);
            cvFile.transferTo(dest);
            profile.setCvFileName(filePath);
        }

        List<String> skills = Arrays.stream(skillsInput.split(","))
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .toList();
        profile.setSkills(skills);

        developerProfileRepository.save(profile);

        return "redirect:/projects?userId=" + userId;
    }
    @GetMapping("/view_profile_dev")
    public String viewProfile(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        System.out.println("User id from session: " + userId);
        model.addAttribute("userId", userId);
    
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (!optionalUser.isPresent()) {
            return "redirect:/login"; 
        }
        
        User user = optionalUser.get();
        model.addAttribute("user", user);
        Optional<DeveloperProfile> optionalProfile = developerProfileRepository.findByUser(user);
    
        if (!optionalProfile.isPresent()) {
            return "redirect:/create_profile_dev";
        }
        
        DeveloperProfile profile = optionalProfile.get();
        
        model.addAttribute("profile", profile);
        return "view_profile_dev";
    }
    @PostMapping("/update_profile_dev")
public String updateDeveloperProfile(
        @RequestParam("email") String email,
        @RequestParam("username") String username,
        @RequestParam("firstName") String firstName,
        @RequestParam("lastName") String lastName,
        @RequestParam("description") String description,
        @RequestParam("skills") List<String> skills,
        @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
        HttpSession session, Model model) {

    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
        return "redirect:/login"; // User not logged in
    }

    Optional<User> optionalUser = userRepository.findById(userId);
    if (!optionalUser.isPresent()) {
        return "redirect:/login"; // User not found
    }
    User user = optionalUser.get();

    Optional<DeveloperProfile> optionalProfile = developerProfileRepository.findByUser(user);
    if (!optionalProfile.isPresent()) {
        return "redirect:/create_profile_dev?userId=" + userId;
    }
    DeveloperProfile profile = optionalProfile.get();

    user.setEmail(email);
    user.setUsername(username);
    userRepository.save(user);

    profile.setFirstName(firstName);
    profile.setLastName(lastName);
    profile.setDescription(description);
    profile.setSkills(skills);

    if (cvFile != null && !cvFile.isEmpty()) {
        try {
            String projectDir = System.getProperty("user.dir");
            String uploadDir = projectDir + File.separator + "cvs" + File.separator + userId;

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = cvFile.getOriginalFilename();
            String filePath = uploadDir + File.separator + originalFilename;
            File dest = new File(filePath);
            cvFile.transferTo(dest);

            profile.setCvFileName(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to upload CV file.");
            return "view_profile_dev";
        }
    }

    developerProfileRepository.save(profile);

    return "redirect:/view_profile_dev";
}

@GetMapping("/developers")
public String showDevelopers(Model model) {
    List<DeveloperProfile> developerProfiles = developerProfileRepository.findAll();
    model.addAttribute("developers", developerProfiles);
    return "dev_profiles";
}

}
