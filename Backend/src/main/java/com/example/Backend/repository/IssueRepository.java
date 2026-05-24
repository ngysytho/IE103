package com.example.Backend.repository;

import com.example.Backend.entity.IssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRepository extends JpaRepository<IssueEntity, String> {
}
