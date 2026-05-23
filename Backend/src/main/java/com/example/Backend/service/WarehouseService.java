package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.CategoryDto;
import com.example.Backend.dto.WarehouseDtos.DashboardSummaryDto;
import com.example.Backend.dto.WarehouseDtos.EmployeeDto;
import com.example.Backend.dto.WarehouseDtos.ImportByPartnerDto;
import com.example.Backend.dto.WarehouseDtos.IssueDto;
import com.example.Backend.dto.WarehouseDtos.IssueItemDto;
import com.example.Backend.dto.WarehouseDtos.LowStockDto;
import com.example.Backend.dto.WarehouseDtos.PartnerDto;
import com.example.Backend.dto.WarehouseDtos.ProductDto;
import com.example.Backend.dto.WarehouseDtos.ReceiptDto;
import com.example.Backend.dto.WarehouseDtos.ReceiptItemDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDifferenceDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeItemDto;
import com.example.Backend.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public WarehouseService(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public List<CategoryDto> getCategories() {
        return jdbcTemplate.query(
                "SELECT trim(MALOAI) AS MALOAI, TENLOAI FROM LOAISP ORDER BY MALOAI",
                (rs, rowNum) -> mapCategory(rs)
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

    public CategoryDto getCategory(String maLoai) {
        return queryOne(
                "SELECT trim(MALOAI) AS MALOAI, TENLOAI FROM LOAISP WHERE trim(MALOAI) = ?",
                (rs, rowNum) -> mapCategory(rs),
                clean(maLoai)
        );
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
            ProductDto product = getProduct(item.maSp());
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

    public DashboardSummaryDto getDashboardSummary() {
        long totalProducts = scalarLong("SELECT COUNT(*) FROM SANPHAM");
        long totalPartners = scalarLong("SELECT COUNT(*) FROM DOITAC");
        long totalEmployees = scalarLong("SELECT COUNT(*) FROM NHANVIEN");
        double totalStock = scalarDouble("SELECT COALESCE(SUM(SOLUONG_TON), 0) FROM SANPHAM");
        long lowStockCount = scalarLong("SELECT COUNT(*) FROM SANPHAM WHERE SOLUONG_TON < 10");

        return new DashboardSummaryDto(
                totalProducts,
                totalPartners,
                totalEmployees,
                totalStock,
                lowStockCount
        );
    }

    public List<LowStockDto> getLowStockReport() {
        return jdbcTemplate.query(
                """
                SELECT ma_sp, ten_sp, dvt, so_luong_ton
                FROM sp_cursor_canhbaotonkho()
                """,
                (rs, rowNum) -> new LowStockDto(
                        rs.getString("ma_sp"),
                        rs.getString("ten_sp"),
                        rs.getString("dvt"),
                        rs.getDouble("so_luong_ton")
                )
        );
    }

    public List<ImportByPartnerDto> getImportByPartnerReport() {
        return jdbcTemplate.query(
                """
                SELECT ma_dt, ten_dt, tong_so_phieu, tong_gia_tri
                FROM sp_cursor_thongkenhaphang()
                """,
                (rs, rowNum) -> new ImportByPartnerDto(
                        rs.getString("ma_dt"),
                        rs.getString("ten_dt"),
                        rs.getLong("tong_so_phieu"),
                        rs.getDouble("tong_gia_tri")
                )
        );
    }

    public List<StocktakeDifferenceDto> getStocktakeDifferenceReport() {
        return jdbcTemplate.query(
                """
                SELECT ma_pkk, ngay_kk, ma_sp, ten_sp, sl_hethong, sl_thucte, chenhlech, lydo
                FROM sp_cursor_kiemtrachenhlech()
                """,
                (rs, rowNum) -> new StocktakeDifferenceDto(
                        rs.getString("ma_pkk"),
                        rs.getDate("ngay_kk").toLocalDate(),
                        rs.getString("ma_sp"),
                        rs.getString("ten_sp"),
                        rs.getDouble("sl_hethong"),
                        rs.getDouble("sl_thucte"),
                        rs.getDouble("chenhlech"),
                        rs.getString("lydo")
                )
        );
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

    private CategoryDto mapCategory(ResultSet rs) throws SQLException {
        return new CategoryDto(rs.getString("MALOAI"), rs.getString("TENLOAI"));
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

    private PartnerDto mapPartner(ResultSet rs) throws SQLException {
        return new PartnerDto(
                rs.getString("MADT"),
                rs.getString("TENDT"),
                rs.getString("DCHI"),
                rs.getString("SDT"),
                rs.getInt("LOAIDT")
        );
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

    private long scalarLong(String sql) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0 : value;
    }

    private double scalarDouble(String sql) {
        Double value = jdbcTemplate.queryForObject(sql, Double.class);
        return value == null ? 0 : value;
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

    private <T> List<T> emptyIfNull(List<T> items) {
        return items == null ? List.of() : items;
    }
}
