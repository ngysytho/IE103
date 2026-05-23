import { Card, Table, Tabs, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import { Issue, Receipt } from '../../types';
import { issueService } from '../../services/issue.service';
import { receiptService } from '../../services/receipt.service';
import { getErrorMessage } from '../../utils/errors';

const HistoryPage = () => {
    const [receipts, setReceipts] = useState<Receipt[]>([]);
    const [issues, setIssues] = useState<Issue[]>([]);

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
                                        { title: 'Mã phiếu', dataIndex: 'maPn' },
                                        { title: 'Ngày nhập', dataIndex: 'ngayNhap' },
                                        { title: 'Nhân viên', dataIndex: 'maNv' },
                                        { title: 'Đối tác', dataIndex: 'maDt' },
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
                                        { title: 'Mã phiếu', dataIndex: 'maPx' },
                                        { title: 'Ngày xuất', dataIndex: 'ngayXuat' },
                                        { title: 'Nhân viên', dataIndex: 'maNv' },
                                        { title: 'Đối tác', dataIndex: 'maDt' },
                                    ]}
                                />
                            ),
                        },
                    ]}
                />
            </Card>
        </>
    );
};

export default HistoryPage;
