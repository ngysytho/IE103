import { Button, Card, Form, Input, Modal, Select, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import { PARTNER_TYPE_OPTIONS } from '../../constants/options';
import { Partner } from '../../types';
import { warehouseMockService } from '../../services/warehouse.mock';

const renderPartnerType = (value: number) => {
    if (value === 0) return <Tag color="blue">Khách hàng</Tag>;
    if (value === 1) return <Tag color="green">Nhà cung cấp</Tag>;
    return <Tag color="purple">Cả hai</Tag>;
};

const PartnersPage = () => {
    const [items, setItems] = useState<Partner[]>([]);
    const [open, setOpen] = useState(false);
    const [form] = Form.useForm<Partner>();

    const fetchData = async () => {
        const res = await warehouseMockService.getPartners();
        setItems(res);
    };

    useEffect(() => {
        void fetchData();
    }, []);

    return (
        <>
            <PageHeader
                title="Đối tác"
                subtitle="Quản lý khách hàng và nhà cung cấp"
                extra={
                    <Button type="primary" onClick={() => setOpen(true)}>
                        Thêm đối tác
                    </Button>
                }
            />

            <Card>
                <Table
                    rowKey="maDt"
                    dataSource={items}
                    columns={[
                        { title: 'Mã đối tác', dataIndex: 'maDt' },
                        { title: 'Tên đối tác', dataIndex: 'tenDt' },
                        { title: 'Địa chỉ', dataIndex: 'dchi' },
                        { title: 'Số điện thoại', dataIndex: 'sdt' },
                        {
                            title: 'Loại đối tác',
                            dataIndex: 'loaiDt',
                            render: renderPartnerType,
                        },
                    ]}
                />
            </Card>

            <Modal
                open={open}
                title="Thêm đối tác"
                onCancel={() => setOpen(false)}
                onOk={async () => {
                    const values = await form.validateFields();
                    setItems((prev) => [values, ...prev]);
                    message.success('Thêm đối tác thành công');
                    form.resetFields();
                    setOpen(false);
                }}
            >
                <Form form={form} layout="vertical">
                    <Form.Item name="maDt" label="Mã đối tác" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="tenDt" label="Tên đối tác" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="dchi" label="Địa chỉ" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="sdt" label="Số điện thoại" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="loaiDt" label="Loại đối tác" rules={[{ required: true }]}>
                        <Select options={PARTNER_TYPE_OPTIONS} />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
};

export default PartnersPage;