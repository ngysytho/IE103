package com.example.Backend.service;

import com.example.Backend.dto.WarehouseDtos.AuthUserDto;
import com.example.Backend.dto.WarehouseDtos.EmployeeDto;
import com.example.Backend.dto.WarehouseDtos.LoginRequest;
import com.example.Backend.dto.WarehouseDtos.RegisterRequest;
import com.example.Backend.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class DemoAuthService {
    private final WarehouseService warehouseService;
    private final JdbcTemplate jdbcTemplate;

    public DemoAuthService(WarehouseService warehouseService, JdbcTemplate jdbcTemplate) {
        this.warehouseService = warehouseService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void ensureAccountStorage() {
        jdbcTemplate.execute(
                """
                CREATE TABLE IF NOT EXISTS TAIKHOAN (
                    EMAIL VARCHAR(100) PRIMARY KEY,
                    MATKHAU VARCHAR(100) NOT NULL,
                    HOTEN VARCHAR(50) NOT NULL,
                    MANV CHAR(8) NOT NULL UNIQUE,
                    LOAINV INT NOT NULL,
                    NGAYTAO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_taikhoan_nhanvien
                        FOREIGN KEY (MANV) REFERENCES NHANVIEN(MANV),
                    CONSTRAINT kiem_tra_taikhoan_loainv
                        CHECK (LOAINV IN (0, 1, 2))
                )
                """
        );
        jdbcTemplate.update(
                """
                INSERT INTO NHANVIEN (MANV, TENNV, DCHI, SDT, LOAINV)
                VALUES
                    ('NVQL0001', 'Nhóm Quản lý', 'TP.HCM', '0988000001', 0),
                    ('NVNH0001', 'Nhóm NV Nhập kho', 'TP.HCM', '0988000002', 1),
                    ('NVXU0001', 'Nhóm NV Xuất kho', 'TP.HCM', '0988000003', 2)
                ON CONFLICT (MANV) DO NOTHING
                """
        );
        seedDefaultAccount("quanly@gmail.com", "Nhóm Quản lý", "NVQL0001", 0);
        seedDefaultAccount("nhapkho@gmail.com", "Nhóm NV Nhập kho", "NVNH0001", 1);
        seedDefaultAccount("xuatkho@gmail.com", "Nhóm NV Xuất kho", "NVXU0001", 2);
    }

    public AuthUserDto login(LoginRequest request) {
        String email = normalizeEmail(request.email() != null ? request.email() : request.username());
        String password = request.password() == null ? "" : request.password();

        Account account = findAccount(email);
        if (account == null || !account.password().equals(password)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Sai email hoặc mật khẩu");
        }

        EmployeeDto employee = warehouseService.getEmployee(account.maNv());
        return toAuthUser(email, employee);
    }

    @Transactional
    public AuthUserDto register(RegisterRequest request) {
        ensureAccountStorage();
        String email = normalizeEmail(request.email());
        String hoTen = request.hoTen() == null ? "" : request.hoTen().trim();
        String password = request.password() == null ? "" : request.password();

        if (hoTen.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Vui lòng nhập họ tên");
        }
        if (email.isBlank() || !email.contains("@")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email không hợp lệ");
        }
        if (password.length() < 4) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Mật khẩu tối thiểu 4 ký tự");
        }
        if (request.loaiNv() < 0 || request.loaiNv() > 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Loại tài khoản không hợp lệ");
        }
        if (findAccount(email) != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email đã tồn tại");
        }

        String maNv = nextEmployeeCode(request.loaiNv());
        jdbcTemplate.update(
                """
                INSERT INTO NHANVIEN (MANV, TENNV, DCHI, SDT, LOAINV)
                VALUES (?, ?, '', '', ?)
                """,
                maNv,
                hoTen,
                request.loaiNv()
        );
        jdbcTemplate.update(
                """
                INSERT INTO TAIKHOAN (EMAIL, MATKHAU, HOTEN, MANV, LOAINV)
                VALUES (?, ?, ?, ?, ?)
                """,
                email,
                password,
                hoTen,
                maNv,
                request.loaiNv()
        );

        return toAuthUser(email, warehouseService.getEmployee(maNv));
    }

    public AuthUserDto requireUser(HttpServletRequest request) {
        String email = emailFromRequest(request);
        Account account = email == null ? null : findAccount(email);

        if (account == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập");
        }

        EmployeeDto employee = warehouseService.getEmployee(account.maNv());
        return toAuthUser(email, employee);
    }

    public AuthUserDto getCurrentUser(HttpServletRequest request) {
        return requireUser(request);
    }

    private AuthUserDto toAuthUser(String email, EmployeeDto employee) {
        return new AuthUserDto(
                email,
                createToken(email),
                employee.maNv(),
                employee.tenNv(),
                employee.loaiNv(),
                roleCode(employee.loaiNv())
        );
    }

    private String emailFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }

        String token = header.substring("Bearer ".length()).trim();
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            int separator = decoded.indexOf(':');
            return separator < 0 ? decoded : decoded.substring(0, separator);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private String createToken(String email) {
        String raw = email + ":ie103-demo";
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private String roleCode(int loaiNv) {
        return switch (loaiNv) {
            case 0 -> "nhom_quan_ly";
            case 1 -> "nhom_nv_nhap";
            case 2 -> "nhom_nv_xuat";
            default -> "unknown";
        };
    }

    private Account findAccount(String email) {
        ensureAccountStorage();
        try {
            return jdbcTemplate.queryForObject(
                    """
                    SELECT lower(EMAIL) AS EMAIL, MATKHAU, trim(MANV) AS MANV, LOAINV
                    FROM TAIKHOAN
                    WHERE lower(EMAIL) = ?
                    """,
                    (rs, rowNum) -> new Account(
                            rs.getString("EMAIL"),
                            rs.getString("MATKHAU"),
                            rs.getString("MANV"),
                            rs.getInt("LOAINV")
                    ),
                    email
            );
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private String nextEmployeeCode(int loaiNv) {
        String prefix = switch (loaiNv) {
            case 0 -> "NVQL";
            case 1 -> "NVNH";
            case 2 -> "NVXU";
            default -> "NV";
        };
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) + 1 FROM NHANVIEN WHERE MANV LIKE ?",
                Long.class,
                prefix + "%"
        );
        long next = count == null ? 1 : count;
        String maNv = String.format("%s%04d", prefix, next);

        while (employeeExists(maNv)) {
            next += 1;
            maNv = String.format("%s%04d", prefix, next);
        }

        return maNv;
    }

    private boolean employeeExists(String maNv) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM NHANVIEN WHERE MANV = ?",
                Long.class,
                maNv
        );
        return count != null && count > 0;
    }

    private void seedDefaultAccount(String email, String hoTen, String maNv, int loaiNv) {
        jdbcTemplate.update(
                """
                UPDATE TAIKHOAN
                SET EMAIL = ?, MATKHAU = '123456', HOTEN = ?, LOAINV = ?
                WHERE MANV = ?
                  AND NOT EXISTS (SELECT 1 FROM TAIKHOAN WHERE EMAIL = ?)
                """,
                email,
                hoTen,
                loaiNv,
                maNv,
                email
        );
        jdbcTemplate.update(
                """
                INSERT INTO TAIKHOAN (EMAIL, MATKHAU, HOTEN, MANV, LOAINV)
                VALUES (?, '123456', ?, ?, ?)
                ON CONFLICT (EMAIL) DO NOTHING
                """,
                email,
                hoTen,
                maNv,
                loaiNv
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private record Account(String email, String password, String maNv, int loaiNv) {
    }
}
