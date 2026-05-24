package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.DashboardSummaryDto;
import com.example.Backend.dto.WarehouseDtos.ImportByPartnerDto;
import com.example.Backend.dto.WarehouseDtos.LowStockDto;
import com.example.Backend.dto.WarehouseDtos.StocktakeDifferenceDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardQueryService {
    private final JdbcTemplate jdbcTemplate;

    public DashboardQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    private long scalarLong(String sql) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0 : value;
    }

    private double scalarDouble(String sql) {
        Double value = jdbcTemplate.queryForObject(sql, Double.class);
        return value == null ? 0 : value;
    }
}
