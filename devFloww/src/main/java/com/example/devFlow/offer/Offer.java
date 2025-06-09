package com.example.devFlow.offer;

import com.example.devFlow.project.Project;
import com.example.devFlow.user.User;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class Offer {

    public enum OfferStatus {
        Accept,
        Pending,
        Done
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "created_at", columnDefinition = "datetime")
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    public Offer() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.status = OfferStatus.Accept;
    }

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public OfferStatus getStatus() { return status; }
    public void setStatus(OfferStatus status) { this.status = status; }
}
