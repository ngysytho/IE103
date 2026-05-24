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
@Table(name = "SANPHAM")
public class ProductEntity {
    @Id
    @Column(name = "MASP", length = 8)
    private String maSp;

    @Column(name = "TENSP", length = 50)
    private String tenSp;

    @Column(name = "DVT", length = 20)
    private String dvt;

    @Column(name = "GIANHAP")
    private Double giaNhap;

    @Column(name = "MALOAI", length = 8)
    private String maLoai;

    @Column(name = "SOLUONG_TON")
    private Double soLuongTon;
}
