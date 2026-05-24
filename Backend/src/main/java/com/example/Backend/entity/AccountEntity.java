package com.example.Backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TAIKHOAN")
public class AccountEntity {
    @Id
    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "MATKHAU", nullable = false, length = 100)
    private String matKhau;

    @Column(name = "HOTEN", nullable = false, length = 50)
    private String hoTen;

    @Column(name = "MANV", nullable = false, length = 8, unique = true)
    private String maNv;

    @Column(name = "LOAINV", nullable = false)
    private Integer loaiNv;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;
}
