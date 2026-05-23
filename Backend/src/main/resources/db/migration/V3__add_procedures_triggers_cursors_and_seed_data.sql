ALTER TABLE PHIEUKIEMKE
ADD COLUMN IF NOT EXISTS TRANGTHAI VARCHAR(20) DEFAULT 'pending';

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'kiem_tra_trang_thai_kiem_ke'
    ) THEN
        ALTER TABLE PHIEUKIEMKE
        ADD CONSTRAINT kiem_tra_trang_thai_kiem_ke
        CHECK (TRANGTHAI IN ('pending', 'approved'));
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'kiem_tra_sl_kiem_ke'
    ) THEN
        ALTER TABLE CT_PHIEUKIEMKE
        ADD CONSTRAINT kiem_tra_sl_kiem_ke
        CHECK (SL_HETHONG >= 0 AND SL_THUCTE >= 0);
    END IF;
END $$;

CREATE OR REPLACE FUNCTION fn_kiem_tra_doi_tac_phieu_nhap()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_loaidt INT;
BEGIN
    SELECT LOAIDT INTO v_loaidt
    FROM DOITAC
    WHERE MADT = NEW.MADT;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy đối tác %!', trim(NEW.MADT);
    END IF;

    IF v_loaidt NOT IN (1, 2) THEN
        RAISE EXCEPTION 'Đối tác % là Khách hàng, không thể lập Phiếu nhập hàng!', trim(NEW.MADT);
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_kiem_tra_doi_tac_phieu_nhap ON PHIEUNHAP;
CREATE TRIGGER trg_kiem_tra_doi_tac_phieu_nhap
BEFORE INSERT OR UPDATE OF MADT ON PHIEUNHAP
FOR EACH ROW
EXECUTE FUNCTION fn_kiem_tra_doi_tac_phieu_nhap();

CREATE OR REPLACE FUNCTION fn_kiem_tra_doi_tac_phieu_xuat()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_loaidt INT;
BEGIN
    SELECT LOAIDT INTO v_loaidt
    FROM DOITAC
    WHERE MADT = NEW.MADT;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy đối tác %!', trim(NEW.MADT);
    END IF;

    IF v_loaidt NOT IN (0, 2) THEN
        RAISE EXCEPTION 'Lỗi: Đối tác % là Nhà cung cấp, không thể lập Phiếu xuất hàng!', trim(NEW.MADT);
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_kiem_tra_doi_tac_phieu_xuat ON PHIEUXUAT;
CREATE TRIGGER trg_kiem_tra_doi_tac_phieu_xuat
BEFORE INSERT OR UPDATE OF MADT ON PHIEUXUAT
FOR EACH ROW
EXECUTE FUNCTION fn_kiem_tra_doi_tac_phieu_xuat();

CREATE OR REPLACE FUNCTION fn_kiem_tra_quyen_nhan_vien_nhap()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_loainv INT;
BEGIN
    SELECT LOAINV INTO v_loainv
    FROM NHANVIEN
    WHERE MANV = NEW.MANV;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy nhân viên %!', trim(NEW.MANV);
    END IF;

    IF v_loainv NOT IN (0, 1) THEN
        RAISE EXCEPTION 'Lỗi: Nhân viên % không có quyền lập Phiếu nhập kho!', trim(NEW.MANV);
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_kiem_tra_quyen_nhan_vien_nhap ON PHIEUNHAP;
CREATE TRIGGER trg_kiem_tra_quyen_nhan_vien_nhap
BEFORE INSERT OR UPDATE OF MANV ON PHIEUNHAP
FOR EACH ROW
EXECUTE FUNCTION fn_kiem_tra_quyen_nhan_vien_nhap();

CREATE OR REPLACE FUNCTION fn_kiem_tra_quyen_nhan_vien_xuat()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_loainv INT;
BEGIN
    SELECT LOAINV INTO v_loainv
    FROM NHANVIEN
    WHERE MANV = NEW.MANV;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Không tìm thấy nhân viên %!', trim(NEW.MANV);
    END IF;

    IF v_loainv NOT IN (0, 2) THEN
        RAISE EXCEPTION 'Lỗi: Nhân viên % không có quyền lập Phiếu xuất kho!', trim(NEW.MANV);
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_kiem_tra_quyen_nhan_vien_xuat ON PHIEUXUAT;
CREATE TRIGGER trg_kiem_tra_quyen_nhan_vien_xuat
BEFORE INSERT OR UPDATE OF MANV ON PHIEUXUAT
FOR EACH ROW
EXECUTE FUNCTION fn_kiem_tra_quyen_nhan_vien_xuat();

CREATE OR REPLACE FUNCTION fn_bao_ve_doi_tac_dang_duoc_su_dung()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.LOAIDT NOT IN (1, 2)
        AND EXISTS (SELECT 1 FROM PHIEUNHAP WHERE MADT = NEW.MADT)
    THEN
        RAISE EXCEPTION 'Đối tác % là Khách hàng, không thể lập Phiếu nhập hàng!', trim(NEW.MADT);
    END IF;

    IF NEW.LOAIDT NOT IN (0, 2)
        AND EXISTS (SELECT 1 FROM PHIEUXUAT WHERE MADT = NEW.MADT)
    THEN
        RAISE EXCEPTION 'Lỗi: Đối tác % là Nhà cung cấp, không thể lập Phiếu xuất hàng!', trim(NEW.MADT);
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_bao_ve_doi_tac_dang_duoc_su_dung ON DOITAC;
CREATE TRIGGER trg_bao_ve_doi_tac_dang_duoc_su_dung
BEFORE UPDATE OF LOAIDT ON DOITAC
FOR EACH ROW
EXECUTE FUNCTION fn_bao_ve_doi_tac_dang_duoc_su_dung();

CREATE OR REPLACE FUNCTION fn_bao_ve_nhan_vien_dang_lap_phieu()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.LOAINV NOT IN (0, 1)
        AND EXISTS (SELECT 1 FROM PHIEUNHAP WHERE MANV = NEW.MANV)
    THEN
        RAISE EXCEPTION 'Lỗi: Nhân viên % không có quyền lập Phiếu nhập kho!', trim(NEW.MANV);
    END IF;

    IF NEW.LOAINV NOT IN (0, 2)
        AND EXISTS (SELECT 1 FROM PHIEUXUAT WHERE MANV = NEW.MANV)
    THEN
        RAISE EXCEPTION 'Lỗi: Nhân viên % không có quyền lập Phiếu xuất kho!', trim(NEW.MANV);
    END IF;

    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_bao_ve_nhan_vien_dang_lap_phieu ON NHANVIEN;
CREATE TRIGGER trg_bao_ve_nhan_vien_dang_lap_phieu
BEFORE UPDATE OF LOAINV ON NHANVIEN
FOR EACH ROW
EXECUTE FUNCTION fn_bao_ve_nhan_vien_dang_lap_phieu();

CREATE OR REPLACE FUNCTION fn_cap_nhat_ton_kho_nhap()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE SANPHAM
        SET SOLUONG_TON = COALESCE(SOLUONG_TON, 0) + NEW.SOLUONG
        WHERE MASP = NEW.MASP;

        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        IF NEW.MASP = OLD.MASP THEN
            UPDATE SANPHAM
            SET SOLUONG_TON = COALESCE(SOLUONG_TON, 0) + NEW.SOLUONG - OLD.SOLUONG
            WHERE MASP = NEW.MASP;
        ELSE
            UPDATE SANPHAM
            SET SOLUONG_TON = COALESCE(SOLUONG_TON, 0) - OLD.SOLUONG
            WHERE MASP = OLD.MASP;

            UPDATE SANPHAM
            SET SOLUONG_TON = COALESCE(SOLUONG_TON, 0) + NEW.SOLUONG
            WHERE MASP = NEW.MASP;
        END IF;

        RETURN NEW;
    ELSE
        UPDATE SANPHAM
        SET SOLUONG_TON = COALESCE(SOLUONG_TON, 0) - OLD.SOLUONG
        WHERE MASP = OLD.MASP;

        RETURN OLD;
    END IF;
END;
$$;

DROP TRIGGER IF EXISTS trg_capnhattonkhonhap ON CT_PHIEUNHAP;
CREATE TRIGGER trg_capnhattonkhonhap
AFTER INSERT OR UPDATE OR DELETE ON CT_PHIEUNHAP
FOR EACH ROW
EXECUTE FUNCTION fn_cap_nhat_ton_kho_nhap();

CREATE OR REPLACE FUNCTION fn_cap_nhat_ton_kho_xuat()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    v_ton DOUBLE PRECISION;
BEGIN
    IF TG_OP = 'INSERT' THEN
        SELECT SOLUONG_TON INTO v_ton
        FROM SANPHAM
        WHERE MASP = NEW.MASP
        FOR UPDATE;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'Không tìm thấy sản phẩm %!', trim(NEW.MASP);
        END IF;

        IF v_ton < NEW.SOLUONG THEN
            RAISE EXCEPTION 'Lỗi: Số lượng xuất % lớn hơn số lượng tồn kho hiện tại % của sản phẩm %!',
                NEW.SOLUONG, v_ton, trim(NEW.MASP);
        END IF;

        UPDATE SANPHAM
        SET SOLUONG_TON = v_ton - NEW.SOLUONG
        WHERE MASP = NEW.MASP;

        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        IF NEW.MASP = OLD.MASP THEN
            SELECT SOLUONG_TON + OLD.SOLUONG INTO v_ton
            FROM SANPHAM
            WHERE MASP = NEW.MASP
            FOR UPDATE;

            IF v_ton < NEW.SOLUONG THEN
                RAISE EXCEPTION 'Lỗi: Số lượng xuất % lớn hơn số lượng tồn kho hiện tại % của sản phẩm %!',
                    NEW.SOLUONG, v_ton, trim(NEW.MASP);
            END IF;

            UPDATE SANPHAM
            SET SOLUONG_TON = v_ton - NEW.SOLUONG
            WHERE MASP = NEW.MASP;
        ELSE
            UPDATE SANPHAM
            SET SOLUONG_TON = COALESCE(SOLUONG_TON, 0) + OLD.SOLUONG
            WHERE MASP = OLD.MASP;

            SELECT SOLUONG_TON INTO v_ton
            FROM SANPHAM
            WHERE MASP = NEW.MASP
            FOR UPDATE;

            IF NOT FOUND THEN
                RAISE EXCEPTION 'Không tìm thấy sản phẩm %!', trim(NEW.MASP);
            END IF;

            IF v_ton < NEW.SOLUONG THEN
                RAISE EXCEPTION 'Lỗi: Số lượng xuất % lớn hơn số lượng tồn kho hiện tại % của sản phẩm %!',
                    NEW.SOLUONG, v_ton, trim(NEW.MASP);
            END IF;

            UPDATE SANPHAM
            SET SOLUONG_TON = v_ton - NEW.SOLUONG
            WHERE MASP = NEW.MASP;
        END IF;

        RETURN NEW;
    ELSE
        UPDATE SANPHAM
        SET SOLUONG_TON = COALESCE(SOLUONG_TON, 0) + OLD.SOLUONG
        WHERE MASP = OLD.MASP;

        RETURN OLD;
    END IF;
END;
$$;

DROP TRIGGER IF EXISTS trg_capnhattonkhoxuat ON CT_PHIEUXUAT;
CREATE TRIGGER trg_capnhattonkhoxuat
BEFORE INSERT OR UPDATE OR DELETE ON CT_PHIEUXUAT
FOR EACH ROW
EXECUTE FUNCTION fn_cap_nhat_ton_kho_xuat();

CREATE OR REPLACE PROCEDURE sp_lapphieunhap(
    p_mapn CHAR(8),
    p_ngaynhap DATE,
    p_manv CHAR(8),
    p_madt CHAR(8),
    p_items JSONB
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_item JSONB;
BEGIN
    IF p_items IS NULL OR jsonb_array_length(p_items) = 0 THEN
        RAISE EXCEPTION 'Phiếu nhập phải có ít nhất một sản phẩm!';
    END IF;

    INSERT INTO PHIEUNHAP (MAPN, NGAYNHAP, MANV, MADT)
    VALUES (p_mapn, p_ngaynhap, p_manv, p_madt);

    FOR v_item IN SELECT value FROM jsonb_array_elements(p_items)
    LOOP
        INSERT INTO CT_PHIEUNHAP (MAPN, MASP, SOLUONG, DONGIA)
        VALUES (
            p_mapn,
            v_item ->> 'maSp',
            (v_item ->> 'soLuong')::DOUBLE PRECISION,
            (v_item ->> 'donGia')::DOUBLE PRECISION
        );
    END LOOP;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_lapphieuxuat(
    p_mapx CHAR(8),
    p_ngayxuat DATE,
    p_manv CHAR(8),
    p_madt CHAR(8),
    p_items JSONB
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_item JSONB;
BEGIN
    IF p_items IS NULL OR jsonb_array_length(p_items) = 0 THEN
        RAISE EXCEPTION 'Phiếu xuất phải có ít nhất một sản phẩm!';
    END IF;

    INSERT INTO PHIEUXUAT (MAPX, NGAYXUAT, MANV, MADT)
    VALUES (p_mapx, p_ngayxuat, p_manv, p_madt);

    FOR v_item IN SELECT value FROM jsonb_array_elements(p_items)
    LOOP
        INSERT INTO CT_PHIEUXUAT (MAPX, MASP, SOLUONG, DONGIA)
        VALUES (
            p_mapx,
            v_item ->> 'maSp',
            (v_item ->> 'soLuong')::DOUBLE PRECISION,
            (v_item ->> 'donGia')::DOUBLE PRECISION
        );
    END LOOP;
END;
$$;

CREATE OR REPLACE FUNCTION sp_cursor_canhbaotonkho()
RETURNS TABLE (
    ma_sp TEXT,
    ten_sp TEXT,
    dvt TEXT,
    so_luong_ton DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
DECLARE
    cur CURSOR FOR
        SELECT trim(S.MASP) AS ma_sp,
               S.TENSP AS ten_sp,
               S.DVT AS dvt,
               S.SOLUONG_TON AS so_luong_ton
        FROM SANPHAM S
        WHERE S.SOLUONG_TON < 10
        ORDER BY S.SOLUONG_TON ASC, S.MASP ASC;
    rec RECORD;
BEGIN
    OPEN cur;
    LOOP
        FETCH cur INTO rec;
        EXIT WHEN NOT FOUND;

        ma_sp := rec.ma_sp;
        ten_sp := rec.ten_sp;
        dvt := rec.dvt;
        so_luong_ton := rec.so_luong_ton;

        RETURN NEXT;
    END LOOP;
    CLOSE cur;
END;
$$;

CREATE OR REPLACE FUNCTION sp_cursor_thongkenhaphang()
RETURNS TABLE (
    ma_dt TEXT,
    ten_dt TEXT,
    tong_so_phieu BIGINT,
    tong_gia_tri DOUBLE PRECISION
)
LANGUAGE plpgsql
AS $$
DECLARE
    cur CURSOR FOR
        SELECT trim(D.MADT) AS ma_dt,
               D.TENDT AS ten_dt,
               COUNT(DISTINCT PN.MAPN) AS tong_so_phieu,
               COALESCE(SUM(CT.SOLUONG * CT.DONGIA), 0)::DOUBLE PRECISION AS tong_gia_tri
        FROM DOITAC D
        JOIN PHIEUNHAP PN ON PN.MADT = D.MADT
        JOIN CT_PHIEUNHAP CT ON CT.MAPN = PN.MAPN
        GROUP BY D.MADT, D.TENDT
        ORDER BY tong_gia_tri DESC;
    rec RECORD;
BEGIN
    OPEN cur;
    LOOP
        FETCH cur INTO rec;
        EXIT WHEN NOT FOUND;

        ma_dt := rec.ma_dt;
        ten_dt := rec.ten_dt;
        tong_so_phieu := rec.tong_so_phieu;
        tong_gia_tri := rec.tong_gia_tri;

        RETURN NEXT;
    END LOOP;
    CLOSE cur;
END;
$$;

CREATE OR REPLACE FUNCTION sp_cursor_kiemtrachenhlech()
RETURNS TABLE (
    ma_pkk TEXT,
    ngay_kk DATE,
    ma_sp TEXT,
    ten_sp TEXT,
    sl_hethong DOUBLE PRECISION,
    sl_thucte DOUBLE PRECISION,
    chenhlech DOUBLE PRECISION,
    lydo TEXT
)
LANGUAGE plpgsql
AS $$
DECLARE
    cur CURSOR FOR
        SELECT trim(PK.MAPKK) AS ma_pkk,
               PK.NGAYKK AS ngay_kk,
               trim(S.MASP) AS ma_sp,
               S.TENSP AS ten_sp,
               CT.SL_HETHONG AS sl_hethong,
               CT.SL_THUCTE AS sl_thucte,
               (CT.SL_THUCTE - CT.SL_HETHONG)::DOUBLE PRECISION AS chenhlech,
               CT.LYDO AS lydo
        FROM CT_PHIEUKIEMKE CT
        JOIN PHIEUKIEMKE PK ON PK.MAPKK = CT.MAPKK
        JOIN SANPHAM S ON S.MASP = CT.MASP
        WHERE CT.SL_HETHONG <> CT.SL_THUCTE
        ORDER BY PK.NGAYKK DESC, S.MASP ASC;
    rec RECORD;
BEGIN
    OPEN cur;
    LOOP
        FETCH cur INTO rec;
        EXIT WHEN NOT FOUND;

        ma_pkk := rec.ma_pkk;
        ngay_kk := rec.ngay_kk;
        ma_sp := rec.ma_sp;
        ten_sp := rec.ten_sp;
        sl_hethong := rec.sl_hethong;
        sl_thucte := rec.sl_thucte;
        chenhlech := rec.chenhlech;
        lydo := rec.lydo;

        RETURN NEXT;
    END LOOP;
    CLOSE cur;
END;
$$;

INSERT INTO LOAISP (MALOAI, TENLOAI) VALUES
('LSP00001', 'Điện tử'),
('LSP00002', 'Gia dụng'),
('LSP00003', 'Văn phòng')
ON CONFLICT (MALOAI) DO NOTHING;

INSERT INTO SANPHAM (MASP, TENSP, DVT, GIANHAP, MALOAI, SOLUONG_TON) VALUES
('SP000001', 'Máy in HP', 'Cái', 2500000, 'LSP00001', 12),
('SP000002', 'Ấm siêu tốc', 'Cái', 450000, 'LSP00002', 4),
('SP000003', 'Giấy A4 Double A', 'Ram', 72000, 'LSP00003', 25),
('SP000004', 'Chuột Logitech', 'Cái', 180000, 'LSP00001', 8)
ON CONFLICT (MASP) DO NOTHING;

INSERT INTO DOITAC (MADT, TENDT, DCHI, SDT, LOAIDT) VALUES
('DT000001', 'Công ty Cung ứng ABC', 'Q1, TP.HCM', '0909123456', 1),
('DT000002', 'Khách hàng Minh Anh', 'Q10, TP.HCM', '0911222333', 0),
('DT000003', 'Đối tác Song Hành', 'Thủ Đức, TP.HCM', '0933444555', 2)
ON CONFLICT (MADT) DO NOTHING;

INSERT INTO NHANVIEN (MANV, TENNV, DCHI, SDT, LOAINV) VALUES
('NVQL0001', 'Nhóm Quản lý', 'TP.HCM', '0988000001', 0),
('NVNH0001', 'Nhóm NV Nhập kho', 'TP.HCM', '0988000002', 1),
('NVXU0001', 'Nhóm NV Xuất kho', 'TP.HCM', '0988000003', 2)
ON CONFLICT (MANV) DO NOTHING;

INSERT INTO PHIEUNHAP (MAPN, NGAYNHAP, MANV, MADT) VALUES
('PN000001', CURRENT_DATE - INTERVAL '3 day', 'NVNH0001', 'DT000001')
ON CONFLICT (MAPN) DO NOTHING;

INSERT INTO CT_PHIEUNHAP (MAPN, MASP, SOLUONG, DONGIA) VALUES
('PN000001', 'SP000001', 3, 2450000)
ON CONFLICT (MAPN, MASP) DO NOTHING;

INSERT INTO PHIEUKIEMKE (MAPKK, NGAYKK, MANV, GHICHU, TRANGTHAI) VALUES
('KK000001', CURRENT_DATE - INTERVAL '1 day', 'NVQL0001', 'Mẫu kiểm kê có chênh lệch để demo Cursor', 'pending')
ON CONFLICT (MAPKK) DO NOTHING;

INSERT INTO CT_PHIEUKIEMKE (MAPKK, MASP, SL_HETHONG, SL_THUCTE, LYDO) VALUES
('KK000001', 'SP000002', 4, 2, 'Thiếu sau đối soát thực tế')
ON CONFLICT (MAPKK, MASP) DO NOTHING;
