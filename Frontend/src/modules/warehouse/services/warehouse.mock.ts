import {
    Category,
    DashboardSummary,
    Employee,
    Issue,
    Partner,
    Product,
    Receipt,
    Stocktake,
} from '../types';

const categories: Category[] = [
    { maLoai: 'LSP001', tenLoai: 'Điện tử' },
    { maLoai: 'LSP002', tenLoai: 'Gia dụng' },
];

const products: Product[] = [
    {
        maSp: 'SP001',
        tenSp: 'Máy in HP',
        dvt: 'Cái',
        giaNhap: 2500000,
        maLoai: 'LSP001',
        soLuongTon: 12,
    },
    {
        maSp: 'SP002',
        tenSp: 'Ấm siêu tốc',
        dvt: 'Cái',
        giaNhap: 450000,
        maLoai: 'LSP002',
        soLuongTon: 4,
    },
];

const partners: Partner[] = [
    {
        maDt: 'DT001',
        tenDt: 'Công ty ABC',
        dchi: 'Q1, TP.HCM',
        sdt: '0909123456',
        loaiDt: 1,
    },
    {
        maDt: 'DT002',
        tenDt: 'Khách hàng Minh',
        dchi: 'Q10, TP.HCM',
        sdt: '0911222333',
        loaiDt: 0,
    },
];

const employees: Employee[] = [
    {
        maNv: 'NV001',
        tenNv: 'Nguyễn Văn A',
        dchi: 'TP.HCM',
        sdt: '0988111222',
        loaiNv: 0,
    },
    {
        maNv: 'NV002',
        tenNv: 'Trần Thị B',
        dchi: 'TP.HCM',
        sdt: '0977333444',
        loaiNv: 1,
    },
    {
        maNv: 'NV003',
        tenNv: 'Lê Văn C',
        dchi: 'TP.HCM',
        sdt: '0966555777',
        loaiNv: 2,
    },
];

const receipts: Receipt[] = [];
const issues: Issue[] = [];
const stocktakes: Stocktake[] = [];

const delay = async () => new Promise((resolve) => setTimeout(resolve, 200));

export const warehouseMockService = {
    async getDashboardStats(): Promise<DashboardSummary> {
        await delay();
        return {
            totalProducts: products.length,
            totalPartners: partners.length,
            totalEmployees: employees.length,
            totalStock: products.reduce((sum, item) => sum + item.soLuongTon, 0),
            lowStockCount: products.filter((item) => item.soLuongTon < 5).length,
        };
    },

    async getCategories(): Promise<Category[]> {
        await delay();
        return categories;
    },

    async getProducts(): Promise<Product[]> {
        await delay();
        return products;
    },

    async getPartners(): Promise<Partner[]> {
        await delay();
        return partners;
    },

    async getEmployees(): Promise<Employee[]> {
        await delay();
        return employees;
    },

    async getReceipts(): Promise<Receipt[]> {
        await delay();
        return receipts;
    },

    async getIssues(): Promise<Issue[]> {
        await delay();
        return issues;
    },

    async getStocktakes(): Promise<Stocktake[]> {
        await delay();
        return stocktakes;
    },

    async createReceipt(payload: Receipt): Promise<void> {
        await delay();
        receipts.unshift(payload);

        payload.items.forEach((item) => {
            const product = products.find((p) => p.maSp === item.maSp);
            if (product) {
                product.soLuongTon += item.soLuong;
            }
        });
    },

    async createIssue(payload: Issue): Promise<void> {
        await delay();

        payload.items.forEach((item) => {
            const product = products.find((p) => p.maSp === item.maSp);

            if (!product || product.soLuongTon < item.soLuong) {
                throw new Error(`Sản phẩm ${item.maSp} không đủ tồn kho`);
            }
        });

        issues.unshift(payload);

        payload.items.forEach((item) => {
            const product = products.find((p) => p.maSp === item.maSp);
            if (product) {
                product.soLuongTon -= item.soLuong;
            }
        });
    },

    async createStocktake(payload: Stocktake): Promise<void> {
        await delay();
        stocktakes.unshift(payload);
    },

    async approveStocktake(maPkk: string): Promise<void> {
        await delay();

        const stocktake = stocktakes.find((item) => item.maPkk === maPkk);
        if (!stocktake) {
            throw new Error('Không tìm thấy phiếu kiểm kê');
        }

        stocktake.status = 'approved';

        stocktake.items.forEach((item) => {
            const product = products.find((p) => p.maSp === item.maSp);
            if (product) {
                product.soLuongTon = item.slThucTe;
            }
        });
    },
};
