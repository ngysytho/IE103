import { Card, Table, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import OutboundForm from '../../../../components/outbound-form';
import { Employee, Issue, Partner, Product } from '../../types';
import { employeeService } from '../../services/employee.service';
import { issueService } from '../../services/issue.service';
import { partnerService } from '../../services/partner.service';
import { productService } from '../../services/product.service';
import { getErrorMessage } from '../../utils/errors';

const OutboundPage = () => {
    const [employees, setEmployees] = useState<Employee[]>([]);
    const [partners, setPartners] = useState<Partner[]>([]);
    const [products, setProducts] = useState<Product[]>([]);
    const [issues, setIssues] = useState<Issue[]>([]);

    const fetchData = async () => {
        try {
            const [employeeRes, partnerRes, productRes, issueRes] = await Promise.all([
                employeeService.getAll(),
                partnerService.getAll(),
                productService.getAll(),
                issueService.getAll(),
            ]);

            setEmployees(employeeRes);
            setPartners(partnerRes);
            setProducts(productRes);
            setIssues(issueRes);
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
