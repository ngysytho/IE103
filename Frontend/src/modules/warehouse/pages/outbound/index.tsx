import { Card, Table } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import OutboundForm from '../../../../components/outbound-form';
import { Employee, Issue, Partner, Product } from '../../types';
import { warehouseMockService } from '../../services/warehouse.mock';

const OutboundPage = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [partners, setPartners] = useState<Partner[]>([]);
    const [products, setProducts] = useState<Product[]>([]);
    const [issues, setIssues] = useState<Issue[]>([]);

    const fetchData = async () => {
        const [employeeRes, partnerRes, productRes, issueRes] = await Promise.all([
            warehouseMockService.getEmployees(),
            warehouseMockService.getPartners(),
            warehouseMockService.getProducts(),
            warehouseMockService.getIssues(),
        ]);

        setEmployees(employeeRes);
        setPartners(partnerRes);
        setProducts(productRes);
        setIssues(issueRes);
    };

    useEffect(() => {
        void fetchData();
    }, []);

    return (
        <>
            <PageHeader
                title="Xuất kho"
                subtitle="Kiểm tra tồn kho, lập phiếu xuất và tự động trừ tồn"
            />

            <OutboundForm
                employees={employees}
                partners={partners}
                products={products}
                onSuccess={() => void fetchData()}
            />

            <Card title="Danh sách phiếu xuất" style={{ marginTop: 20 }}>
                <Table
                    rowKey="maPx"
                    dataSource={issues}
                    columns={[
                        { title: 'Mã phiếu', dataIndex: 'maPx' },
                        { title: 'Ngày xuất', dataIndex: 'ngayXuat' },
                        { title: 'Mã nhân viên', dataIndex: 'maNv' },
                        { title: 'Mã đối tác', dataIndex: 'maDt' },
                        {
                            title: 'Tổng tiền',
                            render: (_, record: Issue) =>
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

export default OutboundPage;