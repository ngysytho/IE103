package com.example.Backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class StocktakeItemId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "MAPKK", length = 8)
    private String maPkk;

    @Column(name = "MASP", length = 8)
    private String maSp;
}
