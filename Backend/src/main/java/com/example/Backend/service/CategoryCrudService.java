package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.CategoryDto;
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
public class CategoryCrudService {
    private final JdbcTemplate jdbcTemplate;

    public CategoryCrudService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CategoryDto> getCategories() {
        return jdbcTemplate.query(
                "SELECT trim(MALOAI) AS MALOAI, TENLOAI FROM LOAISP ORDER BY MALOAI",
                (rs, rowNum) -> mapCategory(rs)
        );
    }

    public CategoryDto getCategory(String maLoai) {
        return queryOne(
                "SELECT trim(MALOAI) AS MALOAI, TENLOAI FROM LOAISP WHERE trim(MALOAI) = ?",
                (rs, rowNum) -> mapCategory(rs),
                clean(maLoai)
        );
    }

    public CategoryDto createCategory(CategoryDto payload) {
        jdbcTemplate.update(
                "INSERT INTO LOAISP (MALOAI, TENLOAI) VALUES (?, ?)",
                clean(payload.maLoai()),
                payload.tenLoai()
        );
        return getCategory(payload.maLoai());
    }

    public CategoryDto updateCategory(String maLoai, CategoryDto payload) {
        CategoryDto current = getCategory(maLoai);
        jdbcTemplate.update(
                "UPDATE LOAISP SET TENLOAI = ? WHERE trim(MALOAI) = ?",
                Optional.ofNullable(payload.tenLoai()).orElse(current.tenLoai()),
                clean(maLoai)
        );
        return getCategory(maLoai);
    }

    public void deleteCategory(String maLoai) {
        jdbcTemplate.update("DELETE FROM LOAISP WHERE trim(MALOAI) = ?", clean(maLoai));
    }

    private CategoryDto mapCategory(ResultSet rs) throws SQLException {
        return new CategoryDto(rs.getString("MALOAI"), rs.getString("TENLOAI"));
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
