package com.example.Backend.repository;

import com.example.Backend.entity.IssueItemEntity;
import com.example.Backend.entity.IssueItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueItemRepository extends JpaRepository<IssueItemEntity, IssueItemId> {
}
