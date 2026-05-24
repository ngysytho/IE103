package com.example.Backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PHIEUNHAP")
public class ReceiptEntity {
    @Id
    @Column(name = "MAPN", length = 8)
    private String maPn;

    @Column(name = "NGAYNHAP")
    private LocalDate ngayNhap;

    @Column(name = "MANV", length = 8)
    private String maNv;

    @Column(name = "MADT", length = 8)
    private String maDt;
}
