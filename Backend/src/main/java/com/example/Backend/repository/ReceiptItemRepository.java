package com.example.Backend.repository;

import com.example.Backend.entity.ReceiptItemEntity;
import com.example.Backend.entity.ReceiptItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptItemRepository extends JpaRepository<ReceiptItemEntity, ReceiptItemId> {
}
