package com.example.devFlow.registration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.devFlow.offer.Offer;
import com.example.devFlow.offer.OfferRepository;
import com.example.devFlow.profile.DeveloperProfile;
import com.example.devFlow.profile.DeveloperProfileRepository;
import com.example.devFlow.profile.Profile;
import com.example.devFlow.profile.ProfileRepository;
import com.example.devFlow.project.Project;
import com.example.devFlow.project.ProjectRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {
    private final ProfileRepository profileRepository;
    private final DeveloperProfileRepository developerProfileRepository;

    private final ProjectRepository projectRepository;
    private final OfferRepository offerRepository;
    public PageController(ProjectRepository projectRepository,OfferRepository offerRepository,ProfileRepository profileRepository,DeveloperProfileRepository developerProfileRepository){
        this.projectRepository = projectRepository;
        this.offerRepository=offerRepository;
        this.profileRepository=profileRepository;
        this.developerProfileRepository=developerProfileRepository;
    }
    @GetMapping("/")
    public String getIndex(HttpSession session) {
        session.invalidate();
        return "index";
    }
    @GetMapping("/custom_logout")
    public String logout(HttpSession session) {
        session.invalidate();
        System.out.println("Session invalidated.");
        return "index";
    }

    

    @GetMapping("/register")
    public String showSignupEmailForm(Model model) {
        model.addAttribute("registrationRequestEmail", new RegistrationRequestEmail(""));
        return "signup";
    }
    
    @GetMapping("/signup_info")
    public String showSignupUsernameForm(@RequestParam String email, Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest("", "", "", email));
        return "signup_info";
    }


    @GetMapping("/success")
    public String success() {
        return "success";
    }
    @PostMapping("/success")
public String successPost() {
    return "success";
}

@GetMapping("/notifications")
public String notifications(HttpSession session, Model model) {
    Long userId = (Long) session.getAttribute("userId");
    if (userId == null) {
        return "redirect:/login";
    }

    List<Project> userProjects = projectRepository.findByUserId(userId);
    List<Long> projectIds = userProjects.stream()
                                       .map(Project::getId)
                                       .collect(Collectors.toList());

    // Φέρνουμε μόνο offers που είναι Pending ή Accept (ό,τι θέλεις)
    List<Offer.OfferStatus> statuses = List.of(Offer.OfferStatus.Pending, Offer.OfferStatus.Accept);
    List<Offer> offers = offerRepository.findByProjectIdInAndStatusIn(projectIds, statuses);

    model.addAttribute("offers", offers);
    return "notifications";
}
@PostMapping("/offers/{offerId}/status")
@ResponseBody
public ResponseEntity<String> updateOfferStatus(@PathVariable Long offerId, @RequestParam String status) {
    try {
        Offer offer = offerRepository.findById(offerId)
                            .orElseThrow(() -> new RuntimeException("Offer not found"));

        // Μετατροπή του status string σε Enum
        Offer.OfferStatus newStatus = Offer.OfferStatus.valueOf(status);

        offer.setStatus(newStatus);
        offerRepository.save(offer);

        return ResponseEntity.ok("Status updated");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body("Invalid status value");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status");
    }
}


@GetMapping("/profile/{username}")
public String showUserProfile(@PathVariable String username, Model model) {
    Optional<Profile> profileOptional = profileRepository.findByUserUsername(username);

    if (profileOptional.isEmpty()) {
        return "redirect:/error";
    }

    Profile profile = profileOptional.get();
    model.addAttribute("profile", profile);
    model.addAttribute("user", profile.getUser());

    return "view_client_profile"; 
}
/*@GetMapping("/devprofile/{username}")
public String showDeveloperProfile(@PathVariable String username, Model model) {
    Optional<DeveloperProfile> profileOptional = developerProfileRepository.findByUserUsername(username);

    if (profileOptional.isEmpty()) {
        return "redirect:/error"; 
    }

    DeveloperProfile profile = profileOptional.get();
    model.addAttribute("profile", profile);
    model.addAttribute("user", profile.getUser());

    return "view_developer_profile";
}*/


    
}



