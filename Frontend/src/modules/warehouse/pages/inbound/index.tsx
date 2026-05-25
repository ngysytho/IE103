import { Button, Card, Modal, Table, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import InboundForm from '../../../../components/inbound-form';
import { Employee, Partner, Product, Receipt } from '../../types';
import { employeeService } from '../../services/employee.service';
import { partnerService } from '../../services/partner.service';
import { productService } from '../../services/product.service';
import { receiptService } from '../../services/receipt.service';
import { getErrorMessage } from '../../utils/errors';

const InboundPage = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [partners, setPartners] = useState<Partner[]>([]);
    const [products, setProducts] = useState<Product[]>([]);
    const [receipts, setReceipts] = useState<Receipt[]>([]);
    const [selectedReceipt, setSelectedReceipt] = useState<Receipt | null>(
        null,
    );
    const [isReceiptModalOpen, setReceiptModalOpen] = useState(false);

    const fetchData = async () => {
        try {
            const [employeeRes, partnerRes, productRes, receiptRes] =
                await Promise.all([
                    employeeService.getAll(),
                    partnerService.getAll(),
                    productService.getAll(),
                    receiptService.getAll(),
                ]);

            setEmployees(employeeRes);
            setPartners(partnerRes);
            setProducts(productRes);
            setReceipts(receiptRes);
        } catch (error) {
            message.error(getErrorMessage(error));
        }
    };

    useEffect(() => {
        void fetchData();
    }, []);

    const openReceiptDetail = async (maPn: string) => {
        try {
            const receipt = await receiptService.getDetail(maPn);
            setSelectedReceipt(receipt);
            setReceiptModalOpen(true);
        } catch (error) {
            message.error(getErrorMessage(error));
        }
    };

    const closeReceiptModal = () => {
        setSelectedReceipt(null);
        setReceiptModalOpen(false);
    };

    return (
        <>
            <PageHeader
                title="Nhập kho"
                subtitle="Kiểm tra hàng hóa, lập phiếu nhập và cộng tồn kho"
            />

            <InboundForm
                employees={employees}
                partners={partners}
                products={products}
                onSuccess={() => void fetchData()}
            />

            <Card title="Danh sách phiếu nhập" style={{ marginTop: 20 }}>
                <Table
                    rowKey="maPn"
                    dataSource={receipts}
                    columns={[
                        { title: 'Mã phiếu', dataIndex: 'maPn' },
                        { title: 'Ngày nhập', dataIndex: 'ngayNhap' },
                        { title: 'Mã nhân viên', dataIndex: 'maNv' },
                        { title: 'Mã đối tác', dataIndex: 'maDt' },
                        {
                            title: 'Số sản phẩm',
                            render: (_: unknown, record: Receipt) =>
                                record.items.length,
                        },
                        {
                            title: 'Tổng tiền',
                            render: (_, record: Receipt) =>
                                record.items
                                    .reduce(
                                        (sum, item) =>
                                            sum + item.soLuong * item.donGia,
                                        0,
                                    )
                                    .toLocaleString('vi-VN'),
                        },
                        {
                            title: 'Hành động',
                            render: (_, record: Receipt) => (
                                <Button
                                    type="link"
                                    onClick={() =>
                                        void openReceiptDetail(record.maPn)
                                    }
                                >
                                    Xem chi tiết
                                </Button>
                            ),
                        },
                    ]}
                />
            </Card>

            <Modal
                title={`Chi tiết phiếu nhập ${selectedReceipt?.maPn ?? ''}`}
                open={isReceiptModalOpen}
                onCancel={closeReceiptModal}
                footer={null}
                width={800}
            >
                <p>
                    <strong>Số sản phẩm:</strong>{' '}
                    {selectedReceipt?.items.length ?? 0}
                </p>
                <p>
                    <strong>Tổng số lượng:</strong>{' '}
                    {selectedReceipt?.items.reduce(
                        (sum, item) => sum + item.soLuong,
                        0,
                    ) ?? 0}
                </p>
                <Table
                    rowKey="maSp"
                    dataSource={selectedReceipt?.items ?? []}
                    pagination={false}
                    columns={[
                        { title: 'Mã sản phẩm', dataIndex: 'maSp' },
                        { title: 'Tên sản phẩm', dataIndex: 'tenSp' },
                        { title: 'Số lượng', dataIndex: 'soLuong' },
                        {
                            title: 'Đơn giá',
                            dataIndex: 'donGia',
                            render: (value: number) =>
                                value.toLocaleString('vi-VN'),
                        },
                        {
                            title: 'Thành tiền',
                            render: (_, item) =>
                                (item.soLuong * item.donGia).toLocaleString(
                                    'vi-VN',
                                ),
                        },
                    ]}
                />
            </Modal>
        </>
    );
};

export default InboundPage;
