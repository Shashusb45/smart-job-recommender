package com.shashwath.smartjobrecommender.controller;


import com.shashwath.smartjobrecommender.model.Job;
import com.shashwath.smartjobrecommender.repository.JobRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobRepository repo;
    public JobController(JobRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Job> getAll() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Job> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Job create(@RequestBody Job job) { return repo.save(job); }

    @PutMapping("/{id}")
    public ResponseEntity<Job> update(@PathVariable Long id, @RequestBody Job updated) {
        return repo.findById(id).map(job -> {
            job.setTitle(updated.getTitle());
            job.setDescription(updated.getDescription());
            job.setSkills(updated.getSkills());
            job.setLocation(updated.getLocation());
            return ResponseEntity.ok(repo.save(job));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

