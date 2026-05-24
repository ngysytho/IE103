package com.example.Backend.repository;

import com.example.Backend.entity.StocktakeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocktakeRepository extends JpaRepository<StocktakeEntity, String> {
}
