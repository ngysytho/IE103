import { Button, Card, Modal, Table, Tabs, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import { Issue, Receipt } from '../../types';
import { issueService } from '../../services/issue.service';
import { receiptService } from '../../services/receipt.service';
import { getErrorMessage } from '../../utils/errors';

const HistoryPage = () => {
    const [receipts, setReceipts] = useState<Receipt[]>([]);
    const [issues, setIssues] = useState<Issue[]>([]);
    const [selectedReceipt, setSelectedReceipt] = useState<Receipt | null>(
        null,
    );
    const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null);
    const [isReceiptModalOpen, setReceiptModalOpen] = useState(false);
    const [isIssueModalOpen, setIssueModalOpen] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [receiptRes, issueRes] = await Promise.all([
                    receiptService.getAll(),
                    issueService.getAll(),
                ]);
                setReceipts(receiptRes);
                setIssues(issueRes);
            } catch (error) {
                message.error(getErrorMessage(error));
            }
        };

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

    const openIssueDetail = async (maPx: string) => {
        try {
            const issue = await issueService.getDetail(maPx);
            setSelectedIssue(issue);
            setIssueModalOpen(true);
        } catch (error) {
            message.error(getErrorMessage(error));
        }
    };

    const closeReceiptModal = () => {
        setSelectedReceipt(null);
        setReceiptModalOpen(false);
    };

    const closeIssueModal = () => {
        setSelectedIssue(null);
        setIssueModalOpen(false);
    };

    return (
        <>
            <PageHeader
                title="Lịch sử nhập / xuất"
                subtitle="Tra cứu các phiếu theo thời gian"
            />

            <Card>
                <Tabs
                    items={[
                        {
                            key: 'receipt',
                            label: 'Lịch sử nhập',
                            children: (
                                <Table
                                    rowKey="maPn"
                                    dataSource={receipts}
                                    columns={[
                                        {
                                            title: 'Mã phiếu',
                                            dataIndex: 'maPn',
                                        },
                                        {
                                            title: 'Ngày nhập',
                                            dataIndex: 'ngayNhap',
                                        },
                                        {
                                            title: 'Nhân viên',
                                            dataIndex: 'maNv',
                                        },
                                        { title: 'Đối tác', dataIndex: 'maDt' },
                                        {
                                            title: 'Số sản phẩm',
                                            render: (
                                                _: unknown,
                                                record: Receipt,
                                            ) => record.items.length,
                                        },
                                        {
                                            title: 'Tổng tiền',
                                            render: (_, record: Receipt) =>
                                                record.items
                                                    .reduce(
                                                        (sum, item) =>
                                                            sum +
                                                            item.soLuong *
                                                                item.donGia,
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
                                                        void openReceiptDetail(
                                                            record.maPn,
                                                        )
                                                    }
                                                >
                                                    Xem chi tiết
                                                </Button>
                                            ),
                                        },
                                    ]}
                                />
                            ),
                        },
                        {
                            key: 'issue',
                            label: 'Lịch sử xuất',
                            children: (
                                <Table
                                    rowKey="maPx"
                                    dataSource={issues}
                                    columns={[
                                        {
                                            title: 'Mã phiếu',
                                            dataIndex: 'maPx',
                                        },
                                        {
                                            title: 'Ngày xuất',
                                            dataIndex: 'ngayXuat',
                                        },
                                        {
                                            title: 'Nhân viên',
                                            dataIndex: 'maNv',
                                        },
                                        { title: 'Đối tác', dataIndex: 'maDt' },
                                        {
                                            title: 'Số sản phẩm',
                                            render: (
                                                _: unknown,
                                                record: Issue,
                                            ) => record.items.length,
                                        },
                                        {
                                            title: 'Tổng tiền',
                                            render: (_, record: Issue) =>
                                                record.items
                                                    .reduce(
                                                        (sum, item) =>
                                                            sum +
                                                            item.soLuong *
                                                                item.donGia,
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
                                                        void openIssueDetail(
                                                            record.maPx,
                                                        )
                                                    }
                                                >
                                                    Xem chi tiết
                                                </Button>
                                            ),
                                        },
                                    ]}
                                />
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

export default HistoryPage;
