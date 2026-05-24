package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.ProductDto;
import com.example.Backend.exception.ApiException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCrudService {
    private final JdbcTemplate jdbcTemplate;

    public ProductCrudService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductDto> getProducts() {
        return jdbcTemplate.query(
                """
                SELECT trim(MASP) AS MASP, TENSP, DVT, GIANHAP, trim(MALOAI) AS MALOAI, SOLUONG_TON
                FROM SANPHAM
                ORDER BY MASP
                """,
                (rs, rowNum) -> mapProduct(rs)
        );
    }

    public ProductDto getProduct(String maSp) {
        return queryOne(
                """
                SELECT trim(MASP) AS MASP, TENSP, DVT, GIANHAP, trim(MALOAI) AS MALOAI, SOLUONG_TON
                FROM SANPHAM
                WHERE trim(MASP) = ?
                """,
                (rs, rowNum) -> mapProduct(rs),
                clean(maSp)
        );
    }

    public ProductDto createProduct(ProductDto payload) {
        jdbcTemplate.update(
                """
                INSERT INTO SANPHAM (MASP, TENSP, DVT, GIANHAP, MALOAI, SOLUONG_TON)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                clean(payload.maSp()),
                payload.tenSp(),
                payload.dvt(),
                payload.giaNhap(),
                clean(payload.maLoai()),
                payload.soLuongTon()
        );
        return getProduct(payload.maSp());
    }

    public ProductDto updateProduct(String maSp, ProductDto payload) {
        ProductDto current = getProduct(maSp);
        jdbcTemplate.update(
                """
                UPDATE SANPHAM
                SET TENSP = ?, DVT = ?, GIANHAP = ?, MALOAI = ?, SOLUONG_TON = ?
                WHERE trim(MASP) = ?
                """,
                Optional.ofNullable(payload.tenSp()).orElse(current.tenSp()),
                Optional.ofNullable(payload.dvt()).orElse(current.dvt()),
                payload.giaNhap(),
                Optional.ofNullable(payload.maLoai()).map(this::clean).orElse(current.maLoai()),
                payload.soLuongTon(),
                clean(maSp)
        );
        return getProduct(maSp);
    }

    public void deleteProduct(String maSp) {
        jdbcTemplate.update("DELETE FROM SANPHAM WHERE trim(MASP) = ?", clean(maSp));
    }

    private ProductDto mapProduct(ResultSet rs) throws SQLException {
        return new ProductDto(
                rs.getString("MASP"),
                rs.getString("TENSP"),
                rs.getString("DVT"),
                rs.getDouble("GIANHAP"),
                rs.getString("MALOAI"),
                rs.getDouble("SOLUONG_TON")
        );
    }

    private <T> T queryOne(String sql, org.springframework.jdbc.core.RowMapper<T> mapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, mapper, args);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu");
        }
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }
}
