package com.example.Backend.repository;

import com.example.Backend.entity.StocktakeItemEntity;
import com.example.Backend.entity.StocktakeItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocktakeItemRepository extends JpaRepository<StocktakeItemEntity, StocktakeItemId> {
}
