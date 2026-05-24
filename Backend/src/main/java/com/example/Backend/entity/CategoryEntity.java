package com.example.Backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "LOAISP")
public class CategoryEntity {
    @Id
    @Column(name = "MALOAI", length = 8)
    private String maLoai;

    @Column(name = "TENLOAI", length = 50)
    private String tenLoai;
}
