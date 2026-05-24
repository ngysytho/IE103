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
@Table(name = "CT_PHIEUKIEMKE")
public class StocktakeItemEntity {
    @EmbeddedId
    private StocktakeItemId id;

    @Column(name = "SL_HETHONG")
    private Double slHeThong;

    @Column(name = "SL_THUCTE")
    private Double slThucTe;

    @Column(name = "LYDO", length = 250)
    private String lyDo;
}
