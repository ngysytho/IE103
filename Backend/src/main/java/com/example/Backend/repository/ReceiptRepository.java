package com.example.Backend.repository;

import com.example.Backend.entity.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<ReceiptEntity, String> {
}
