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
@Table(name = "NHANVIEN")
public class EmployeeEntity {
    @Id
    @Column(name = "MANV", length = 8)
    private String maNv;

    @Column(name = "TENNV", length = 50)
    private String tenNv;

    @Column(name = "DCHI", length = 100)
    private String dchi;

    @Column(name = "SDT", length = 11)
    private String sdt;

    @Column(name = "LOAINV")
    private Integer loaiNv;
}
