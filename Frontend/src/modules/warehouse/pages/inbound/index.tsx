import { Card, Table } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import InboundForm from '../../../../components/inbound-form';
import { Employee, Partner, Product, Receipt } from '../../types';
import { warehouseMockService } from '../../services/warehouse.mock';

const InboundPage = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [partners, setPartners] = useState<Partner[]>([]);
    const [products, setProducts] = useState<Product[]>([]);
    const [receipts, setReceipts] = useState<Receipt[]>([]);

    const fetchData = async () => {
        const [employeeRes, partnerRes, productRes, receiptRes] = await Promise.all([
            warehouseMockService.getEmployees(),
            warehouseMockService.getPartners(),
            warehouseMockService.getProducts(),
            warehouseMockService.getReceipts(),
        ]);

        setEmployees(employeeRes);
        setPartners(partnerRes);
        setProducts(productRes);
        setReceipts(receiptRes);
    };

    useEffect(() => {
        void fetchData();
    }, []);

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
                            title: 'Tổng tiền',
                            render: (_, record: Receipt) =>
                                record.items
                                    .reduce((sum, item) => sum + item.soLuong * item.donGia, 0)
                                    .toLocaleString('vi-VN'),
                        },
                    ]}
                />
            </Card>
        </>
    );
};

export default InboundPage;