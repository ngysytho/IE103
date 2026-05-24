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
@Table(name = "PHIEUKIEMKE")
public class StocktakeEntity {
    @Id
    @Column(name = "MAPKK", length = 8)
    private String maPkk;

    @Column(name = "NGAYKK")
    private LocalDate ngayKk;

    @Column(name = "MANV", length = 8)
    private String maNv;

    @Column(name = "GHICHU", length = 250)
    private String ghiChu;

    @Column(name = "TRANGTHAI", length = 20)
    private String trangThai;
}
