package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.EmployeeDto;
import com.example.Backend.exception.ApiException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeCrudService {
    private final JdbcTemplate jdbcTemplate;

    public EmployeeCrudService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EmployeeDto> getEmployees(String loaiNvIn) {
        List<Integer> filters = parseIntFilters(loaiNvIn);
        if (filters.isEmpty()) {
            return jdbcTemplate.query(
                    """
                    SELECT trim(MANV) AS MANV, TENNV, DCHI, SDT, LOAINV
                    FROM NHANVIEN
                    ORDER BY MANV
                    """,
                    (rs, rowNum) -> mapEmployee(rs)
            );
        }

        String placeholders = placeholders(filters.size());
        return jdbcTemplate.query(
                """
                SELECT trim(MANV) AS MANV, TENNV, DCHI, SDT, LOAINV
                FROM NHANVIEN
                WHERE LOAINV IN (%s)
                ORDER BY MANV
                """.formatted(placeholders),
                (rs, rowNum) -> mapEmployee(rs),
                filters.toArray()
        );
    }

    public EmployeeDto getEmployee(String maNv) {
        return queryOne(
                """
                SELECT trim(MANV) AS MANV, TENNV, DCHI, SDT, LOAINV
                FROM NHANVIEN
                WHERE trim(MANV) = ?
                """,
                (rs, rowNum) -> mapEmployee(rs),
                clean(maNv)
        );
    }

    public EmployeeDto createEmployee(EmployeeDto payload) {
        jdbcTemplate.update(
                "INSERT INTO NHANVIEN (MANV, TENNV, DCHI, SDT, LOAINV) VALUES (?, ?, ?, ?, ?)",
                clean(payload.maNv()),
                payload.tenNv(),
                payload.dchi(),
                payload.sdt(),
                payload.loaiNv()
        );
        return getEmployee(payload.maNv());
    }

    public EmployeeDto updateEmployee(String maNv, EmployeeDto payload) {
        EmployeeDto current = getEmployee(maNv);
        jdbcTemplate.update(
                """
                UPDATE NHANVIEN
                SET TENNV = ?, DCHI = ?, SDT = ?, LOAINV = ?
                WHERE trim(MANV) = ?
                """,
                Optional.ofNullable(payload.tenNv()).orElse(current.tenNv()),
                Optional.ofNullable(payload.dchi()).orElse(current.dchi()),
                Optional.ofNullable(payload.sdt()).orElse(current.sdt()),
                payload.loaiNv(),
                clean(maNv)
        );
        return getEmployee(maNv);
    }

    public void deleteEmployee(String maNv) {
        jdbcTemplate.update("DELETE FROM NHANVIEN WHERE trim(MANV) = ?", clean(maNv));
    }

    private EmployeeDto mapEmployee(ResultSet rs) throws SQLException {
        return new EmployeeDto(
                rs.getString("MANV"),
                rs.getString("TENNV"),
                rs.getString("DCHI"),
                rs.getString("SDT"),
                rs.getInt("LOAINV")
        );
    }

    private <T> T queryOne(String sql, org.springframework.jdbc.core.RowMapper<T> mapper, Object... args) {
        try {
            return jdbcTemplate.queryForObject(sql, mapper, args);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu");
        }
    }

    private List<Integer> parseIntFilters(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String placeholders(int size) {
        return java.util.stream.IntStream.range(0, size)
                .mapToObj(index -> "?")
                .collect(Collectors.joining(", "));
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }
}
