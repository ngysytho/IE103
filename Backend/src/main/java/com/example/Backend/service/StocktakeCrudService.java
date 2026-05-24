package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.ProductDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeItemDto;
import com.example.Backend.exception.ApiException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StocktakeCrudService {
    private final JdbcTemplate jdbcTemplate;
    private final ProductCrudService productCrudService;

    public StocktakeCrudService(JdbcTemplate jdbcTemplate, ProductCrudService productCrudService) {
        this.jdbcTemplate = jdbcTemplate;
        this.productCrudService = productCrudService;
    }

    public List<StocktakeDto> getStocktakes() {
        List<StocktakeDto> headers = jdbcTemplate.query(
                """
                SELECT trim(MAPKK) AS MAPKK, NGAYKK, trim(MANV) AS MANV, GHICHU, TRANGTHAI
                FROM PHIEUKIEMKE
                ORDER BY NGAYKK DESC, MAPKK DESC
                """,
                (rs, rowNum) -> new StocktakeDto(
                        rs.getString("MAPKK"),
                        rs.getDate("NGAYKK").toLocalDate(),
                        rs.getString("MANV"),
                        rs.getString("GHICHU"),
                        rs.getString("TRANGTHAI"),
                        List.of()
                )
        );

        return headers.stream()
                .map(stocktake -> new StocktakeDto(
                        stocktake.maPkk(),
                        stocktake.ngayKk(),
                        stocktake.maNv(),
                        stocktake.ghiChu(),
                        stocktake.status(),
                        getStocktakeItems(stocktake.maPkk())
                ))
                .toList();
    }

    public StocktakeDto getStocktake(String maPkk) {
        StocktakeDto header = queryOne(
                """
                SELECT trim(MAPKK) AS MAPKK, NGAYKK, trim(MANV) AS MANV, GHICHU, TRANGTHAI
                FROM PHIEUKIEMKE
                WHERE trim(MAPKK) = ?
                """,
                (rs, rowNum) -> new StocktakeDto(
                        rs.getString("MAPKK"),
                        rs.getDate("NGAYKK").toLocalDate(),
                        rs.getString("MANV"),
                        rs.getString("GHICHU"),
                        rs.getString("TRANGTHAI"),
                        List.of()
                ),
                clean(maPkk)
        );

        return new StocktakeDto(
                header.maPkk(),
                header.ngayKk(),
                header.maNv(),
                header.ghiChu(),
                header.status(),
                getStocktakeItems(maPkk)
        );
    }

    @Transactional
    public StocktakeDto createStocktake(StocktakeDto payload, String maNv) {
        validateStocktake(payload);
        jdbcTemplate.update(
                """
                INSERT INTO PHIEUKIEMKE (MAPKK, NGAYKK, MANV, GHICHU, TRANGTHAI)
                VALUES (?, ?, ?, ?, 'pending')
                """,
                clean(payload.maPkk()),
                Date.valueOf(payload.ngayKk()),
                clean(maNv),
                payload.ghiChu()
        );

        for (StocktakeItemDto item : emptyIfNull(payload.items())) {
            ProductDto product = productCrudService.getProduct(item.maSp());
            jdbcTemplate.update(
                    """
                    INSERT INTO CT_PHIEUKIEMKE (MAPKK, MASP, SL_HETHONG, SL_THUCTE, LYDO)
                    VALUES (?, ?, ?, ?, ?)
                    """,
                    clean(payload.maPkk()),
                    clean(item.maSp()),
                    product.soLuongTon(),
                    item.slThucTe(),
                    item.lyDo()
            );
        }

        return getStocktake(payload.maPkk());
    }

    @Transactional
    public StocktakeDto approveStocktake(String maPkk) {
        StocktakeDto stocktake = getStocktake(maPkk);

        if ("approved".equals(stocktake.status())) {
            return stocktake;
        }

        jdbcTemplate.update(
                """
                UPDATE SANPHAM S
                SET SOLUONG_TON = CT.SL_THUCTE
                FROM CT_PHIEUKIEMKE CT
                WHERE CT.MASP = S.MASP
                  AND trim(CT.MAPKK) = ?
                """,
                clean(maPkk)
        );
        jdbcTemplate.update(
                "UPDATE PHIEUKIEMKE SET TRANGTHAI = 'approved' WHERE trim(MAPKK) = ?",
                clean(maPkk)
        );

        return getStocktake(maPkk);
    }

    private List<StocktakeItemDto> getStocktakeItems(String maPkk) {
        return jdbcTemplate.query(
                """
                SELECT trim(CT.MASP) AS MASP, S.TENSP, CT.SL_HETHONG, CT.SL_THUCTE, CT.LYDO
                FROM CT_PHIEUKIEMKE CT
                JOIN SANPHAM S ON S.MASP = CT.MASP
                WHERE trim(CT.MAPKK) = ?
                ORDER BY CT.MASP
                """,
                (rs, rowNum) -> new StocktakeItemDto(
                        rs.getString("MASP"),
                        rs.getString("TENSP"),
                        rs.getDouble("SL_HETHONG"),
                        rs.getDouble("SL_THUCTE"),
                        rs.getString("LYDO")
                ),
                clean(maPkk)
        );
    }

    private void validateStocktake(StocktakeDto payload) {
        if (payload == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Dữ liệu phiếu kiểm kê không được rỗng");
        }

        validateDocumentHeader(payload.maPkk(), payload.ngayKk(), "DT000002", "phiếu kiểm kê");

        if (payload.items() == null || payload.items().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Phiếu kiểm kê phải có ít nhất một sản phẩm");
        }

        Set<String> productCodes = new HashSet<>();
        for (StocktakeItemDto item : payload.items()) {
            validateProductCode(item.maSp(), productCodes, "phiếu kiểm kê");
            if (item.slThucTe() < 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Số lượng thực tế không được âm");
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
