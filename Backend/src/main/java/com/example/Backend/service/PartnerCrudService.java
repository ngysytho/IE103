package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.PartnerDto;
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
public class PartnerCrudService {
    private final JdbcTemplate jdbcTemplate;

    public PartnerCrudService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PartnerDto> getPartners(String loaiDtIn) {
        List<Integer> filters = parseIntFilters(loaiDtIn);
        if (filters.isEmpty()) {
            return jdbcTemplate.query(
                    """
                    SELECT trim(MADT) AS MADT, TENDT, DCHI, SDT, LOAIDT
                    FROM DOITAC
                    ORDER BY MADT
                    """,
                    (rs, rowNum) -> mapPartner(rs)
            );
        }

        String placeholders = placeholders(filters.size());
        return jdbcTemplate.query(
                """
                SELECT trim(MADT) AS MADT, TENDT, DCHI, SDT, LOAIDT
                FROM DOITAC
                WHERE LOAIDT IN (%s)
                ORDER BY MADT
                """.formatted(placeholders),
                (rs, rowNum) -> mapPartner(rs),
                filters.toArray()
        );
    }

    public PartnerDto getPartner(String maDt) {
        return queryOne(
                """
                SELECT trim(MADT) AS MADT, TENDT, DCHI, SDT, LOAIDT
                FROM DOITAC
                WHERE trim(MADT) = ?
                """,
                (rs, rowNum) -> mapPartner(rs),
                clean(maDt)
        );
    }

    public PartnerDto createPartner(PartnerDto payload) {
        jdbcTemplate.update(
                "INSERT INTO DOITAC (MADT, TENDT, DCHI, SDT, LOAIDT) VALUES (?, ?, ?, ?, ?)",
                clean(payload.maDt()),
                payload.tenDt(),
                payload.dchi(),
                payload.sdt(),
                payload.loaiDt()
        );
        return getPartner(payload.maDt());
    }

    public PartnerDto updatePartner(String maDt, PartnerDto payload) {
        PartnerDto current = getPartner(maDt);
        jdbcTemplate.update(
                """
                UPDATE DOITAC
                SET TENDT = ?, DCHI = ?, SDT = ?, LOAIDT = ?
                WHERE trim(MADT) = ?
                """,
                Optional.ofNullable(payload.tenDt()).orElse(current.tenDt()),
                Optional.ofNullable(payload.dchi()).orElse(current.dchi()),
                Optional.ofNullable(payload.sdt()).orElse(current.sdt()),
                payload.loaiDt(),
                clean(maDt)
        );
        return getPartner(maDt);
    }

    public void deletePartner(String maDt) {
        jdbcTemplate.update("DELETE FROM DOITAC WHERE trim(MADT) = ?", clean(maDt));
    }

    private PartnerDto mapPartner(ResultSet rs) throws SQLException {
        return new PartnerDto(
                rs.getString("MADT"),
                rs.getString("TENDT"),
                rs.getString("DCHI"),
                rs.getString("SDT"),
                rs.getInt("LOAIDT")
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
