package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.ReceiptDto;
import com.example.Backend.dto.WarehouseDtos.ReceiptItemDto;
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
public class ReceiptCrudService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public ReceiptCrudService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<ReceiptDto> getReceipts() {
        List<ReceiptDto> headers = jdbcTemplate.query(
                """
                SELECT trim(MAPN) AS MAPN, NGAYNHAP, trim(MANV) AS MANV, trim(MADT) AS MADT
                FROM PHIEUNHAP
                ORDER BY NGAYNHAP DESC, MAPN DESC
                """,
                (rs, rowNum) -> new ReceiptDto(
                        rs.getString("MAPN"),
                        rs.getDate("NGAYNHAP").toLocalDate(),
                        rs.getString("MANV"),
                        rs.getString("MADT"),
                        List.of()
                )
        );

        return headers.stream()
                .map(receipt -> new ReceiptDto(
                        receipt.maPn(),
                        receipt.ngayNhap(),
                        receipt.maNv(),
                        receipt.maDt(),
                        getReceiptItems(receipt.maPn())
                ))
                .toList();
    }

    public ReceiptDto getReceipt(String maPn) {
        ReceiptDto header = queryOne(
                """
                SELECT trim(MAPN) AS MAPN, NGAYNHAP, trim(MANV) AS MANV, trim(MADT) AS MADT
                FROM PHIEUNHAP
                WHERE trim(MAPN) = ?
                """,
                (rs, rowNum) -> new ReceiptDto(
                        rs.getString("MAPN"),
                        rs.getDate("NGAYNHAP").toLocalDate(),
                        rs.getString("MANV"),
                        rs.getString("MADT"),
                        List.of()
                ),
                clean(maPn)
        );

        return new ReceiptDto(
                header.maPn(),
                header.ngayNhap(),
                header.maNv(),
                header.maDt(),
                getReceiptItems(maPn)
        );
    }

    @Transactional
    public ReceiptDto createReceipt(ReceiptDto payload, String maNv) {
        validateReceipt(payload);
        jdbcTemplate.update(
                """
                CALL sp_lapphieunhap(
                    CAST(? AS VARCHAR),
                    CAST(? AS VARCHAR),
                    CAST(? AS VARCHAR),
                    CAST(? AS VARCHAR),
                    CAST(? AS JSONB)
                )
                """,
                clean(payload.maPn()),
                payload.ngayNhap().toString(),
                clean(maNv),
                clean(payload.maDt()),
                receiptItemsJson(payload.items())
        );
        return getReceipt(payload.maPn());
    }

    private List<ReceiptItemDto> getReceiptItems(String maPn) {
        return jdbcTemplate.query(
                """
                SELECT trim(CT.MASP) AS MASP, S.TENSP, CT.SOLUONG, CT.DONGIA
                FROM CT_PHIEUNHAP CT
                JOIN SANPHAM S ON S.MASP = CT.MASP
                WHERE trim(CT.MAPN) = ?
                ORDER BY CT.MASP
                """,
                (rs, rowNum) -> new ReceiptItemDto(
                        rs.getString("MASP"),
                        rs.getString("TENSP"),
                        rs.getDouble("SOLUONG"),
                        rs.getDouble("DONGIA")
                ),
                clean(maPn)
        );
    }

    private String receiptItemsJson(List<ReceiptItemDto> items) {
        ArrayNode node = objectMapper.createArrayNode();
        for (ReceiptItemDto item : emptyIfNull(items)) {
            ObjectNode itemNode = objectMapper.createObjectNode();
            itemNode.put("maSp", clean(item.maSp()));
            itemNode.put("soLuong", item.soLuong());
            itemNode.put("donGia", item.donGia());
            node.add(itemNode);
        }
        return node.toString();
    }

    private void validateReceipt(ReceiptDto payload) {
        if (payload == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dữ liệu phiếu nhập không được rỗng");
        }

        validateDocumentHeader(payload.maPn(), payload.ngayNhap(), payload.maDt(), "phiếu nhập");

        if (payload.items() == null || payload.items().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Phiếu nhập phải có ít nhất một sản phẩm");
        }

        Set<String> productCodes = new HashSet<>();
        for (ReceiptItemDto item : payload.items()) {
            validateProductCode(item.maSp(), productCodes, "phiếu nhập");
            if (item.soLuong() <= 0 || item.donGia() <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Số lượng và đơn giá nhập phải lớn hơn 0");
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
