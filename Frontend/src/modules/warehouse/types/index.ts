export type PartnerType = 0 | 1 | 2;
export type EmployeeType = 0 | 1 | 2;

export interface Category {
    maLoai: string;
    tenLoai: string;
}

export interface Product {
    maSp: string;
    tenSp: string;
    dvt: string;
    giaNhap: number;
    maLoai: string;
    soLuongTon: number;
}

export interface Partner {
    maDt: string;
    tenDt: string;
    diaChi: string;
    sdt: string;
    loaiDt: PartnerType;
}

export interface Employee {
    maNv: string;
    tenNv: string;
    dchi: string;
    sdt: string;
    loaiNv: EmployeeType;
}

export interface ReceiptItem {
    maSp: string;
    tenSp?: string;
    soLuong: number;
    donGia: number;
}

export interface Receipt {
    maPn: string;
    ngayNhap: string;
    maNv: string;
    maDt: string;
    items: ReceiptItem[];
}

export interface IssueItem {
    maSp: string;
    tenSp?: string;
    soLuong: number;
    donGia: number;
}

export interface Issue {
    maPx: string;
    ngayXuat: string;
    maNv: string;
    maDt: string;
    items: IssueItem[];
}

export interface StocktakeItem {
    maSp: string;
    tenSp?: string;
    slHeThong: number;
    slThucTe: number;
    lyDo: string;
}

export interface Stocktake {
    maPkk: string;
    ngayKk: string;
    maNv: string;
    ghiChu: string;
    items: StocktakeItem[];
    status: 'pending' | 'approved';
}

export interface DashboardSummary {
    totalProducts: number;
    totalPartners: number;
    totalEmployees: number;
    totalStock: number;
    lowStockCount: number;
}