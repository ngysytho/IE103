import { Card, Col, Row, Table } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import StatCard from '../../../../components/stat-card';
import StockBadge from '../../../../components/stock-badge';
import { DashboardSummary, Product } from '../../types';
import { warehouseMockService } from '../../services/warehouse.mock';

const WarehouseDashboardPage = () => {
    const [stats, setStats] = useState<DashboardSummary | null>(null);
    const [products, setProducts] = useState<Product[]>([]);

    useEffect(() => {
        const bootstrap = async () => {
            const [statsRes, productRes] = await Promise.all([
                warehouseMockService.getDashboardStats(),
                warehouseMockService.getProducts(),
            ]);

            setStats(statsRes);
            setProducts(productRes);
        };

        void bootstrap();
    }, []);

    return (
        <>
            <PageHeader
                title="Dashboard kho"
                subtitle="Theo dõi nhanh tồn kho, sản phẩm, nhân sự và cảnh báo sắp hết hàng"
            />

            <Row gutter={[16, 16]}>
                <Col span={6}>
                    <StatCard title="Tổng sản phẩm" value={stats?.totalProducts ?? 0} />
                </Col>
                <Col span={6}>
                    <StatCard title="Tổng đối tác" value={stats?.totalPartners ?? 0} />
                </Col>
                <Col span={6}>
                    <StatCard title="Tổng nhân viên" value={stats?.totalEmployees ?? 0} />
                </Col>
                <Col span={6}>
                    <StatCard title="Tổng tồn kho" value={stats?.totalStock ?? 0} />
                </Col>
            </Row>

            <Card title="Sản phẩm tồn thấp" style={{ marginTop: 20 }}>
                <Table
                    rowKey="maSp"
                    dataSource={products.filter((item) => item.soLuongTon < 5)}
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
        </>
    );
};

export default WarehouseDashboardPage;