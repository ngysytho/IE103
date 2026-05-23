import { Button, Card, Space, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import StocktakeForm from '../../../../components/stocktake-form';
import { Employee, Product, Stocktake } from '../../types';
import { employeeService } from '../../services/employee.service';
import { productService } from '../../services/product.service';
import { stocktakeService } from '../../services/stocktake.service';
import { getErrorMessage } from '../../utils/errors';
import { useAuth } from '../../auth/AuthContext';

const StocktakePage = () => {
    const { isManager } = useAuth();
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [products, setProducts] = useState<Product[]>([]);
    const [stocktakes, setStocktakes] = useState<Stocktake[]>([]);

    const fetchData = async () => {
        try {
            const [employeeRes, productRes, stocktakeRes] = await Promise.all([
                employeeService.getAll(),
                productService.getAll(),
                stocktakeService.getAll(),
            ]);

            setEmployees(employeeRes);
            setProducts(productRes);
            setStocktakes(stocktakeRes);
        } catch (error) {
            message.error(getErrorMessage(error));
        }
    };

    useEffect(() => {
        void fetchData();
    }, []);

    return (
        <>
            <PageHeader
                title="Kiểm kê kho"
                subtitle="Đối soát số liệu thực tế với số lượng hệ thống"
            />

            <StocktakeForm
                employees={employees}
                products={products}
                onSuccess={() => void fetchData()}
            />

            <Card title="Danh sách phiếu kiểm kê" style={{ marginTop: 20 }}>
                <Table
                    rowKey="maPkk"
                    dataSource={stocktakes}
                    columns={[
                        { title: 'Mã phiếu', dataIndex: 'maPkk' },
                        { title: 'Ngày kiểm kê', dataIndex: 'ngayKk' },
                        { title: 'Nhân viên', dataIndex: 'maNv' },
                        { title: 'Ghi chú', dataIndex: 'ghiChu' },
                        {
                            title: 'Trạng thái',
                            dataIndex: 'status',
                            render: (value: string) =>
                                value === 'approved' ? (
                                    <Tag color="green">Đã duyệt</Tag>
                                ) : (
                                    <Tag color="orange">Chờ duyệt</Tag>
                                ),
                        },
                        {
                            title: 'Hành động',
                            render: (_, record: Stocktake) => (
                                <Space>
                                    {record.status !== 'approved' ? (
                                        <Button
                                            type="primary"
                                            disabled={!isManager}
                                            onClick={async () => {
                                                try {
                                                    await stocktakeService.approve(record.maPkk);
                                                    message.success('Duyệt phiếu kiểm kê thành công');
                                                    void fetchData();
                                                } catch (error) {
                                                    message.error(getErrorMessage(error));
                                                }
                                            }}
                                        >
                                            Duyệt cập nhật
                                        </Button>
                                    ) : null}
                                </Space>
                            ),
                        },
                    ]}
                />
            </Card>
        </>
    );
};

export default StocktakePage;
