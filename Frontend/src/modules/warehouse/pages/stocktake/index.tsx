import { Button, Card, Modal, Space, Table, Tag, message } from 'antd';
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
    const [selectedStocktake, setSelectedStocktake] =
        useState<Stocktake | null>(null);
    const [isStocktakeModalOpen, setStocktakeModalOpen] = useState(false);

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

    const openStocktakeDetail = async (maPkk: string) => {
        try {
            const stocktake = await stocktakeService.getDetail(maPkk);
            setSelectedStocktake(stocktake);
            setStocktakeModalOpen(true);
        } catch (error) {
            message.error(getErrorMessage(error));
        }
    };

    const closeStocktakeModal = () => {
        setSelectedStocktake(null);
        setStocktakeModalOpen(false);
    };

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
                            title: 'Số sản phẩm',
                            render: (_: unknown, record: Stocktake) =>
                                record.items.length,
                        },
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
                                    <Button
                                        type="link"
                                        onClick={() =>
                                            void openStocktakeDetail(
                                                record.maPkk,
                                            )
                                        }
                                    >
                                        Xem chi tiết
                                    </Button>
                                    {record.status !== 'approved' ? (
                                        <Button
                                            type="primary"
                                            disabled={!isManager}
                                            onClick={async () => {
                                                try {
                                                    await stocktakeService.approve(
                                                        record.maPkk,
                                                    );
                                                    message.success(
                                                        'Duyệt phiếu kiểm kê thành công',
                                                    );
                                                    void fetchData();
                                                } catch (error) {
                                                    message.error(
                                                        getErrorMessage(error),
                                                    );
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

            <Modal
                title={`Chi tiết phiếu kiểm kê ${selectedStocktake?.maPkk ?? ''}`}
                open={isStocktakeModalOpen}
                onCancel={closeStocktakeModal}
                footer={null}
                width={900}
            >
                <p>
                    <strong>Số sản phẩm kiểm kê:</strong>{' '}
                    {selectedStocktake?.items.length ?? 0}
                </p>
                <p>
                    <strong>Tổng chênh lệch:</strong>{' '}
                    {selectedStocktake?.items.reduce(
                        (sum, item) => sum + (item.slThucTe - item.slHeThong),
                        0,
                    ) ?? 0}
                </p>
                <Table
                    rowKey="maSp"
                    dataSource={selectedStocktake?.items ?? []}
                    pagination={false}
                    columns={[
                        { title: 'Mã sản phẩm', dataIndex: 'maSp' },
                        { title: 'Tên sản phẩm', dataIndex: 'tenSp' },
                        { title: 'Số lượng hệ thống', dataIndex: 'slHeThong' },
                        { title: 'Số lượng thực tế', dataIndex: 'slThucTe' },
                        {
                            title: 'Chênh lệch',
                            render: (_, item) => item.slThucTe - item.slHeThong,
                        },
                        { title: 'Lý do', dataIndex: 'lyDo' },
                    ]}
                />
            </Modal>
        </>
    );
};

export default StocktakePage;
