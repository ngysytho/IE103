ALTER TABLE PHIEUKIEMKE
ADD COLUMN IF NOT EXISTS TRANGTHAI VARCHAR(20) DEFAULT 'pending';

UPDATE PHIEUKIEMKE
SET TRANGTHAI = 'pending'
WHERE TRANGTHAI IS NULL;

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

CREATE OR REPLACE PROCEDURE sp_lapphieunhap(
    p_mapn VARCHAR,
    p_ngaynhap VARCHAR,
    p_manv VARCHAR,
    p_madt VARCHAR,
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
    VALUES (CAST(p_mapn AS CHAR(8)), CAST(p_ngaynhap AS DATE), CAST(p_manv AS CHAR(8)), CAST(p_madt AS CHAR(8)));

    FOR v_item IN SELECT value FROM jsonb_array_elements(p_items)
    LOOP
        INSERT INTO CT_PHIEUNHAP (MAPN, MASP, SOLUONG, DONGIA)
        VALUES (
            CAST(p_mapn AS CHAR(8)),
            CAST(v_item ->> 'maSp' AS CHAR(8)),
            (v_item ->> 'soLuong')::DOUBLE PRECISION,
            (v_item ->> 'donGia')::DOUBLE PRECISION
        );
    END LOOP;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_lapphieunhap(
    p_mapn VARCHAR,
    p_ngaynhap DATE,
    p_manv VARCHAR,
    p_madt VARCHAR,
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
    VALUES (CAST(p_mapn AS CHAR(8)), p_ngaynhap, CAST(p_manv AS CHAR(8)), CAST(p_madt AS CHAR(8)));

    FOR v_item IN SELECT value FROM jsonb_array_elements(p_items)
    LOOP
        INSERT INTO CT_PHIEUNHAP (MAPN, MASP, SOLUONG, DONGIA)
        VALUES (
            CAST(p_mapn AS CHAR(8)),
            CAST(v_item ->> 'maSp' AS CHAR(8)),
            (v_item ->> 'soLuong')::DOUBLE PRECISION,
            (v_item ->> 'donGia')::DOUBLE PRECISION
        );
    END LOOP;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_lapphieuxuat(
    p_mapx VARCHAR,
    p_ngayxuat VARCHAR,
    p_manv VARCHAR,
    p_madt VARCHAR,
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
    VALUES (CAST(p_mapx AS CHAR(8)), CAST(p_ngayxuat AS DATE), CAST(p_manv AS CHAR(8)), CAST(p_madt AS CHAR(8)));

    FOR v_item IN SELECT value FROM jsonb_array_elements(p_items)
    LOOP
        INSERT INTO CT_PHIEUXUAT (MAPX, MASP, SOLUONG, DONGIA)
        VALUES (
            CAST(p_mapx AS CHAR(8)),
            CAST(v_item ->> 'maSp' AS CHAR(8)),
            (v_item ->> 'soLuong')::DOUBLE PRECISION,
            (v_item ->> 'donGia')::DOUBLE PRECISION
        );
    END LOOP;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_lapphieuxuat(
    p_mapx VARCHAR,
    p_ngayxuat DATE,
    p_manv VARCHAR,
    p_madt VARCHAR,
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
    VALUES (CAST(p_mapx AS CHAR(8)), p_ngayxuat, CAST(p_manv AS CHAR(8)), CAST(p_madt AS CHAR(8)));

    FOR v_item IN SELECT value FROM jsonb_array_elements(p_items)
    LOOP
        INSERT INTO CT_PHIEUXUAT (MAPX, MASP, SOLUONG, DONGIA)
        VALUES (
            CAST(p_mapx AS CHAR(8)),
            CAST(v_item ->> 'maSp' AS CHAR(8)),
            (v_item ->> 'soLuong')::DOUBLE PRECISION,
            (v_item ->> 'donGia')::DOUBLE PRECISION
        );
    END LOOP;
END;
$$;
