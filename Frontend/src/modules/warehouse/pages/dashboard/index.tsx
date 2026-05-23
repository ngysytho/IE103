import { Button, Card, Col, Row, Space, Table, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import StatCard from '../../../../components/stat-card';
import StockBadge from '../../../../components/stock-badge';
import {
    DashboardSummary,
    ImportByPartnerReport,
    LowStockReport,
    Product,
    StocktakeDifferenceReport,
} from '../../types';
import { dashboardService } from '../../services/dashboard.service';
import { productService } from '../../services/product.service';
import { reportService } from '../../services/report.service';
import { useAuth } from '../../auth/AuthContext';
import { getErrorMessage } from '../../utils/errors';
import { formatCurrency } from '../../utils/format';

const WarehouseDashboardPage = () => {
    const { isManager } = useAuth();
    const [stats, setStats] = useState<DashboardSummary | null>(null);
    const [products, setProducts] = useState<Product[]>([]);
    const [lowStock, setLowStock] = useState<LowStockReport[]>([]);
    const [importByPartner, setImportByPartner] = useState<ImportByPartnerReport[]>([]);
    const [differences, setDifferences] = useState<StocktakeDifferenceReport[]>([]);
    const [loadingReport, setLoadingReport] = useState<string | null>(null);

    useEffect(() => {
        const bootstrap = async () => {
            try {
                const [statsRes, productRes] = await Promise.all([
                    dashboardService.getSummary(),
                    productService.getAll(),
                ]);

                setStats(statsRes);
                setProducts(productRes);
            } catch (error) {
                message.error(getErrorMessage(error));
            }
        };

        void bootstrap();
    }, []);

    const runLowStockCursor = async () => {
        setLoadingReport('low-stock');
        try {
            setLowStock(await reportService.getLowStock());
            message.success('Đã chạy sp_Cursor_CanhBaoTonKho');
        } catch (error) {
            message.error(getErrorMessage(error));
        } finally {
            setLoadingReport(null);
        }
    };

    const runImportCursor = async () => {
        setLoadingReport('import');
        try {
            setImportByPartner(await reportService.getImportByPartner());
            message.success('Đã chạy sp_Cursor_ThongKeNhapHang');
        } catch (error) {
            message.error(getErrorMessage(error));
        } finally {
            setLoadingReport(null);
        }
    };

    const runDifferenceCursor = async () => {
        setLoadingReport('difference');
        try {
            setDifferences(await reportService.getStocktakeDifferences());
            message.success('Đã chạy sp_Cursor_KiemTraChenhLech');
        } catch (error) {
            message.error(getErrorMessage(error));
        } finally {
            setLoadingReport(null);
        }
    };

    return (
        <>
            <PageHeader
                title="Dashboard kho"
                subtitle="Theo dõi nhanh tồn kho, sản phẩm, nhân sự và cảnh báo sắp hết hàng"
            />

            <Row gutter={[16, 16]}>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard title="Tổng sản phẩm" value={stats?.totalProducts ?? 0} />
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard title="Tổng đối tác" value={stats?.totalPartners ?? 0} />
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard title="Tổng nhân viên" value={stats?.totalEmployees ?? 0} />
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard title="Tổng tồn kho" value={stats?.totalStock ?? 0} />
                </Col>
            </Row>

            <Card title="Sản phẩm tồn thấp" style={{ marginTop: 20 }}>
                <Table
                    rowKey="maSp"
                    dataSource={products.filter((item) => item.soLuongTon < 10)}
                    pagination={false}
                    columns={[
                        { title: 'Mã SP', dataIndex: 'maSp' },
                        { title: 'Tên sản phẩm', dataIndex: 'tenSp' },
                        { title: 'ĐVT', dataIndex: 'dvt' },
                        { title: 'Tồn kho', dataIndex: 'soLuongTon' },
                        {
                            title: 'Trạng thái',
                            render: (_, record: Product) => (
                                <StockBadge quantity={record.soLuongTon} />
                            ),
                        },
                    ]}
                />
            </Card>

            {isManager ? (
                <Card
                    title="Cursor báo cáo"
                    style={{ marginTop: 20 }}
                    extra={
                        <Space wrap>
                            <Button
                                onClick={() => void runLowStockCursor()}
                                loading={loadingReport === 'low-stock'}
                            >
                                Cảnh báo tồn kho
                            </Button>
                            <Button
                                onClick={() => void runImportCursor()}
                                loading={loadingReport === 'import'}
                            >
                                Thống kê nhập hàng
                            </Button>
                            <Button
                                onClick={() => void runDifferenceCursor()}
                                loading={loadingReport === 'difference'}
                            >
                                Kiểm kê chênh lệch
                            </Button>
                        </Space>
                    }
                >
                    <Row gutter={[16, 16]}>
                        <Col xs={24} xl={8}>
                            <Table
                                size="small"
                                rowKey="maSp"
                                dataSource={lowStock}
                                pagination={false}
                                columns={[
                                    { title: 'Mã SP', dataIndex: 'maSp' },
                                    { title: 'Tên SP', dataIndex: 'tenSp' },
                                    { title: 'Tồn', dataIndex: 'soLuongTon' },
                                ]}
                            />
                        </Col>
                        <Col xs={24} xl={8}>
                            <Table
                                size="small"
                                rowKey="maDt"
                                dataSource={importByPartner}
                                pagination={false}
                                columns={[
                                    { title: 'Đối tác', dataIndex: 'tenDt' },
                                    { title: 'Số phiếu', dataIndex: 'tongSoPhieu' },
                                    {
                                        title: 'Tổng nhập',
                                        dataIndex: 'tongGiaTri',
                                        render: (value: number) => formatCurrency(value),
                                    },
                                ]}
                            />
                        </Col>
                        <Col xs={24} xl={8}>
                            <Table
                                size="small"
                                rowKey={(record) => `${record.maPkk}-${record.maSp}`}
                                dataSource={differences}
                                pagination={false}
                                columns={[
                                    { title: 'Phiếu', dataIndex: 'maPkk' },
                                    { title: 'Mã SP', dataIndex: 'maSp' },
                                    { title: 'Chênh', dataIndex: 'chenhLech' },
                                ]}
                            />
                        </Col>
                    </Row>
                </Card>
            ) : null}
        </>
    );
};

export default WarehouseDashboardPage;
