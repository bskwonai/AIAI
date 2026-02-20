package com.example.aiai.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chatbots")
public class Chatbot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Workspace workspace;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String model;

    @Lob
    @Column(nullable = false)
    private String systemPrompt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() { this.createdAt = Instant.now(); }

    public Long getId() { return id; }
    public Workspace getWorkspace() { return workspace; }
    public void setWorkspace(Workspace workspace) { this.workspace = workspace; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
    public Instant getCreatedAt() { return createdAt; }
}
