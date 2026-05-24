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
@Table(name = "DOITAC")
public class PartnerEntity {
    @Id
    @Column(name = "MADT", length = 8)
    private String maDt;

    @Column(name = "TENDT", length = 50)
    private String tenDt;

    @Column(name = "DCHI", length = 100)
    private String dchi;

    @Column(name = "SDT", length = 11)
    private String sdt;

    @Column(name = "LOAIDT")
    private Integer loaiDt;
}
