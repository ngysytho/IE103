package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.IssueDto;
import com.example.Backend.dto.WarehouseDtos.IssueItemDto;
import com.example.Backend.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IssueCrudService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public IssueCrudService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<IssueDto> getIssues() {
        List<IssueDto> headers = jdbcTemplate.query(
                """
                SELECT trim(MAPX) AS MAPX, NGAYXUAT, trim(MANV) AS MANV, trim(MADT) AS MADT
                FROM PHIEUXUAT
                ORDER BY NGAYXUAT DESC, MAPX DESC
                """,
                (rs, rowNum) -> new IssueDto(
                        rs.getString("MAPX"),
                        rs.getDate("NGAYXUAT").toLocalDate(),
                        rs.getString("MANV"),
                        rs.getString("MADT"),
                        List.of()
                )
        );

        return headers.stream()
                .map(issue -> new IssueDto(
                        issue.maPx(),
                        issue.ngayXuat(),
                        issue.maNv(),
                        issue.maDt(),
                        getIssueItems(issue.maPx())
                ))
                .toList();
    }

    public IssueDto getIssue(String maPx) {
        IssueDto header = queryOne(
                """
                SELECT trim(MAPX) AS MAPX, NGAYXUAT, trim(MANV) AS MANV, trim(MADT) AS MADT
                FROM PHIEUXUAT
                WHERE trim(MAPX) = ?
                """,
                (rs, rowNum) -> new IssueDto(
                        rs.getString("MAPX"),
                        rs.getDate("NGAYXUAT").toLocalDate(),
                        rs.getString("MANV"),
                        rs.getString("MADT"),
                        List.of()
                ),
                clean(maPx)
        );

        return new IssueDto(
                header.maPx(),
                header.ngayXuat(),
                header.maNv(),
                header.maDt(),
                getIssueItems(maPx)
        );
    }

    @Transactional
    public IssueDto createIssue(IssueDto payload, String maNv) {
        validateIssue(payload);
        jdbcTemplate.update(
                """
                CALL sp_lapphieuxuat(
                    CAST(? AS VARCHAR),
                    CAST(? AS VARCHAR),
                    CAST(? AS VARCHAR),
                    CAST(? AS VARCHAR),
                    CAST(? AS JSONB)
                )
                """,
                clean(payload.maPx()),
                payload.ngayXuat().toString(),
                clean(maNv),
                clean(payload.maDt()),
                issueItemsJson(payload.items())
        );
        return getIssue(payload.maPx());
    }

    private List<IssueItemDto> getIssueItems(String maPx) {
        return jdbcTemplate.query(
                """
                SELECT trim(CT.MASP) AS MASP, S.TENSP, CT.SOLUONG, CT.DONGIA
                FROM CT_PHIEUXUAT CT
                JOIN SANPHAM S ON S.MASP = CT.MASP
                WHERE trim(CT.MAPX) = ?
                ORDER BY CT.MASP
                """,
                (rs, rowNum) -> new IssueItemDto(
                        rs.getString("MASP"),
                        rs.getString("TENSP"),
                        rs.getDouble("SOLUONG"),
                        rs.getDouble("DONGIA")
                ),
                clean(maPx)
        );
    }

    private String issueItemsJson(List<IssueItemDto> items) {
        ArrayNode node = objectMapper.createArrayNode();
        for (IssueItemDto item : emptyIfNull(items)) {
            ObjectNode itemNode = objectMapper.createObjectNode();
            itemNode.put("maSp", clean(item.maSp()));
            itemNode.put("soLuong", item.soLuong());
            itemNode.put("donGia", item.donGia());
            node.add(itemNode);
        }
        return node.toString();
    }

    private void validateIssue(IssueDto payload) {
        if (payload == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dữ liệu phiếu xuất không được rỗng");
        }

        validateDocumentHeader(payload.maPx(), payload.ngayXuat(), payload.maDt(), "phiếu xuất");

        if (payload.items() == null || payload.items().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Phiếu xuất phải có ít nhất một sản phẩm");
        }

        Set<String> productCodes = new HashSet<>();
        for (IssueItemDto item : payload.items()) {
            validateProductCode(item.maSp(), productCodes, "phiếu xuất");
            if (item.soLuong() <= 0 || item.donGia() <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Số lượng và đơn giá xuất phải lớn hơn 0");
            }
        }
    }

    private void validateDocumentHeader(String documentCode, LocalDate date, String partnerCode, String documentName) {
        if (clean(documentCode) == null || clean(documentCode).isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã " + documentName + " không được rỗng");
        }
        if (clean(documentCode).length() > 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã " + documentName + " tối đa 8 ký tự");
        }
        if (date == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Ngày lập " + documentName + " không được rỗng");
        }
        if (partnerCode != null && clean(partnerCode).length() > 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã đối tác tối đa 8 ký tự");
        }
    }

    private void validateProductCode(String maSp, Set<String> productCodes, String documentName) {
        String productCode = clean(maSp);
        if (productCode == null || productCode.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã sản phẩm trong " + documentName + " không được rỗng");
        }
        if (productCode.length() > 8) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mã sản phẩm tối đa 8 ký tự");
        }
        if (!productCodes.add(productCode)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mỗi sản phẩm chỉ được chọn một dòng trong " + documentName);
        }
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

    private <T> List<T> emptyIfNull(List<T> items) {
        return items == null ? List.of() : items;
    }
}
