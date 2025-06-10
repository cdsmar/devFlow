package com.example.devFlow.project;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.example.devFlow.comment.Comment;
import com.example.devFlow.comment.CommentRepository;
import com.example.devFlow.user.User;
import com.example.devFlow.user.UserRepository;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    public ProjectController(ProjectRepository projectRepository, UserRepository userRepository, ProjectService projectService,CommentRepository commentRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.commentRepository=commentRepository;
    }

    @PostMapping("/create_project")
    public String createProject(
            @RequestParam("userId") Long userId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "isPrivate", defaultValue = "false") boolean isPrivate,
            @RequestParam(value = "showDevPrice", defaultValue = "false") boolean showDevPrice,
            @RequestParam("category") ProjectCategory category,
            @RequestParam("subcategory") ProjectSubcategory subcategory,
            @RequestParam(value = "paymentMethod", required = false) PaymentMethod paymentMethod,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam("estimatedDuration") EstimatedDuration estimatedDuration,
            @RequestParam("offerDurationDays") int offerDurationDays,
            @RequestParam(value = "suggestedTechnologies", required = false) List<String> suggestedTechnologies
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid userId"));

        Project project = new Project();
        project.setUser(user);
        project.setTitle(title);
        project.setDescription(description);
        project.setPrivate(isPrivate);
        project.setShowDevPrice(showDevPrice);
        project.setCategory(category);
        project.setSubcategory(subcategory);
        project.setPaymentMethod(paymentMethod);
        project.setMaxPrice(maxPrice);
        project.setEstimatedDuration(estimatedDuration);
        project.setOfferDurationDays(offerDurationDays);
        project.setSuggestedTechnologies(suggestedTechnologies);

        projectRepository.save(project);

        return "redirect:/client_dashboard?userId=" + userId;
    }

    @GetMapping("/client_dashboard")
    public String showProfileForm(@RequestParam("userId") Long userId, Model model) {
        model.addAttribute("userId", userId);
        return "client_dashboard";
    }

    @GetMapping("/projects")
    public String listProjects(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "is_private", required = false) Integer isPrivateParam,
            @RequestParam(name = "category", required = false) ProjectCategory category,
            @RequestParam(name = "subcategory", required = false) ProjectSubcategory subcategory,
            @RequestParam(name = "tags", required = false) String tagsParam,
            @RequestParam(name = "date", required = false) String dateFilter,
            Model model) {
    
        List<Project> projects = projectRepository.filterProjects(
            (query != null && !query.isBlank()) ? query : null,
            category,
            subcategory
        );
    
        if (isPrivateParam != null) {
            final boolean finalIsPrivate = (isPrivateParam == 1);
            projects.removeIf(project -> project.isPrivate() != finalIsPrivate);
        }
    
        if (tagsParam != null && !tagsParam.isBlank()) {
            List<String> tags = List.of(tagsParam.split(","));
            projects.removeIf(project ->
                project.getSuggestedTechnologies() == null ||
                project.getSuggestedTechnologies().stream().noneMatch(tags::contains)
            );
        }
    
        if (dateFilter != null) {
            LocalDateTime now = LocalDateTime.now();
            switch (dateFilter) {
                case "last24h":
                    projects.removeIf(project -> project.getDatePosted() == null || project.getDatePosted().isBefore(now.minusHours(24)));
                    break;
                case "lastWeek":
                    projects.removeIf(project -> project.getDatePosted() == null || project.getDatePosted().isBefore(now.minusDays(7)));
                    break;
                case "lastMonth":
                    projects.removeIf(project -> project.getDatePosted() == null || project.getDatePosted().isBefore(now.minusDays(30)));
                    break;
            }
        }
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        projects.forEach(project -> {
            if (project.getDatePosted() != null) {
                project.setFormattedDatePosted(project.getDatePosted().format(formatter));
            }
        });
    
        model.addAttribute("projects", projects);
        model.addAttribute("query", query);
        model.addAttribute("is_private", isPrivateParam);
        model.addAttribute("category", category);
        model.addAttribute("subcategory", subcategory);
        model.addAttribute("selectedTags", tagsParam);
        model.addAttribute("dateFilter", dateFilter);
    
        return "projects";
    }
    


@GetMapping("/projects/{id}")
public String getProjectById(@PathVariable Long id, Model model) {
    System.out.println("Fetching project with id: " + id);
    List<Comment> comments = commentRepository.findByProjectId(id);
    model.addAttribute("comments", comments);

    var projectOpt = projectService.findById(id);
    if (projectOpt.isPresent()) {
        model.addAttribute("project", projectOpt.get());
        return "project";

    } else {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
    }
}


}
