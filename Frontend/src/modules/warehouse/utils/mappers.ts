export const mapCategoryDto = (row: any) => ({
    maLoai: row.MALOAI ?? row.maLoai,
    tenLoai: row.TENLOAI ?? row.tenLoai,
});

export const mapProductDto = (row: any) => ({
    maSp: row.MASP ?? row.maSp,
    tenSp: row.TENSP ?? row.tenSp,
    dvt: row.DVT ?? row.dvt,
    giaNhap: Number(row.GIANHAP ?? row.giaNhap ?? 0),
    maLoai: row.MALOAI ?? row.maLoai,
    soLuongTon: Number(row.SOLUONG_TON ?? row.soLuongTon ?? 0),
});

export const mapPartnerDto = (row: any) => ({
    maDt: row.MADT ?? row.maDt,
    tenDt: row.TENDT ?? row.tenDt,
    dchi: row.DCHI ?? row.dchi ?? row.diaChi,
    sdt: row.SDT ?? row.sdt,
    loaiDt: Number(row.LOAIDT ?? row.loaiDt ?? 0),
});

export const mapEmployeeDto = (row: any) => ({
    maNv: row.MANV ?? row.maNv,
    tenNv: row.TENNV ?? row.tenNv,
    dchi: row.DCHI ?? row.dchi,
    sdt: row.SDT ?? row.sdt,
    loaiNv: Number(row.LOAINV ?? row.loaiNv ?? 0),
});
