package com.example.Backend.repository;

import com.example.Backend.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
}
