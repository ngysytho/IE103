import { Button, Card, Modal, Table, message } from 'antd';
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
    const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null);
    const [isIssueModalOpen, setIssueModalOpen] = useState(false);

    const fetchData = async () => {
        try {
            const [employeeRes, partnerRes, productRes, issueRes] =
                await Promise.all([
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

    const openIssueDetail = async (maPx: string) => {
        try {
            const issue = await issueService.getDetail(maPx);
            setSelectedIssue(issue);
            setIssueModalOpen(true);
        } catch (error) {
            message.error(getErrorMessage(error));
        }
    };

    const closeIssueModal = () => {
        setSelectedIssue(null);
        setIssueModalOpen(false);
    };

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
                            title: 'Số sản phẩm',
                            render: (_: unknown, record: Issue) =>
                                record.items.length,
                        },
                        {
                            title: 'Tổng tiền',
                            render: (_, record: Issue) =>
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
                            render: (_, record: Issue) => (
                                <Button
                                    type="link"
                                    onClick={() =>
                                        void openIssueDetail(record.maPx)
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
                title={`Chi tiết phiếu xuất ${selectedIssue?.maPx ?? ''}`}
                open={isIssueModalOpen}
                onCancel={closeIssueModal}
                footer={null}
                width={800}
            >
                <p>
                    <strong>Số sản phẩm:</strong>{' '}
                    {selectedIssue?.items.length ?? 0}
                </p>
                <p>
                    <strong>Tổng số lượng:</strong>{' '}
                    {selectedIssue?.items.reduce(
                        (sum, item) => sum + item.soLuong,
                        0,
                    ) ?? 0}
                </p>
                <Table
                    rowKey="maSp"
                    dataSource={selectedIssue?.items ?? []}
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

export default OutboundPage;
