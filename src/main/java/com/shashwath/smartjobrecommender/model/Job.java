package com.shashwath.smartjobrecommender.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")   // matches DB
    private Long jobId;

    @Column(name = "job_title") // matches DB
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private String location;

    // DB creates value automatically
    @Column(insertable = false, updatable = false)
    private Timestamp posted;


    public Job() {}

    public Job(Long jobId, String title, String description, String skills, String location) {
        this.jobId = jobId;
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.location = location;
    }

    // getters and setters
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Timestamp getPosted() { return posted; }
}
