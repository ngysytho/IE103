package com.example.Backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CT_PHIEUXUAT")
public class IssueItemEntity {
    @EmbeddedId
    private IssueItemId id;

    @Column(name = "SOLUONG")
    private Double soLuong;

    @Column(name = "DONGIA")
    private Double donGia;
}
