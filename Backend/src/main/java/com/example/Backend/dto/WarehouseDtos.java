package com.example.Backend.dto;

import java.time.LocalDate;
import java.util.List;

public final class WarehouseDtos {
    private WarehouseDtos() {
    }

    public record CategoryDto(String maLoai, String tenLoai) {
    }

    public record ProductDto(
            String maSp,
            String tenSp,
            String dvt,
            double giaNhap,
            String maLoai,
            double soLuongTon
    ) {
    }

    public record PartnerDto(
            String maDt,
            String tenDt,
            String dchi,
            String sdt,
            int loaiDt
    ) {
    }

    public record EmployeeDto(
            String maNv,
            String tenNv,
            String dchi,
            String sdt,
            int loaiNv
    ) {
    }

    public record ReceiptItemDto(
            String maSp,
            String tenSp,
            double soLuong,
            double donGia
    ) {
    }

    public record ReceiptDto(
            String maPn,
            LocalDate ngayNhap,
            String maNv,
            String maDt,
            List<ReceiptItemDto> items
    ) {
    }

    public record IssueItemDto(
            String maSp,
            String tenSp,
            double soLuong,
            double donGia
    ) {
    }

    public record IssueDto(
            String maPx,
            LocalDate ngayXuat,
            String maNv,
            String maDt,
            List<IssueItemDto> items
    ) {
    }

    public record StocktakeItemDto(
            String maSp,
            String tenSp,
            double slHeThong,
            double slThucTe,
            String lyDo
    ) {
    }

    public record StocktakeDto(
            String maPkk,
            LocalDate ngayKk,
            String maNv,
            String ghiChu,
            String status,
            List<StocktakeItemDto> items
    ) {
    }

    public record DashboardSummaryDto(
            long totalProducts,
            long totalPartners,
            long totalEmployees,
            double totalStock,
            long lowStockCount
    ) {
    }

    public record LowStockDto(
            String maSp,
            String tenSp,
            String dvt,
            double soLuongTon
    ) {
    }

    public record ImportByPartnerDto(
            String maDt,
            String tenDt,
            long tongSoPhieu,
            double tongGiaTri
    ) {
    }

    public record StocktakeDifferenceDto(
            String maPkk,
            LocalDate ngayKk,
            String maSp,
            String tenSp,
            double slHeThong,
            double slThucTe,
            double chenhLech,
            String lyDo
    ) {
    }

    public record LoginRequest(String email, String username, String password) {
    }

    public record RegisterRequest(
            String hoTen,
            String email,
            String password,
            int loaiNv
    ) {
    }

    public record AuthUserDto(
            String email,
            String token,
            String maNv,
            String tenNv,
            int loaiNv,
            String role
    ) {
    }

    public record ErrorDto(String message) {
    }
}
