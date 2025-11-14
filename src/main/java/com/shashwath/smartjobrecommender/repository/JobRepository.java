package com.shashwath.smartjobrecommender.repository;
import com.shashwath.smartjobrecommender.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    // later: custom queries
}
