package com.example.Backend.repository;

import com.example.Backend.entity.PartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<PartnerEntity, String> {
}
