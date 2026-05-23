import { Button, Card, Form, Input, Modal, Space, Table, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import { Category } from '../../types';
import { warehouseMockService } from '../../services/warehouse.mock';

const CategoriesPage = () => {
    const [items, setItems] = useState<Category[]>([]);
    const [open, setOpen] = useState(false);
    const [form] = Form.useForm<Category>();

    const fetchData = async () => {
        const res = await warehouseMockService.getCategories();
        setItems(res);
    };

    useEffect(() => {
        void fetchData();
    }, []);

    return (
        <>
            <PageHeader
                title="Danh mục loại sản phẩm"
                subtitle="Khởi tạo dữ liệu nền cho LOAISP"
                extra={
                    <Button type="primary" onClick={() => setOpen(true)}>
                        Thêm loại sản phẩm
                    </Button>
                }
            />

            <Card>
                <Table
                    rowKey="maLoai"
                    dataSource={items}
                    columns={[
                        { title: 'Mã loại', dataIndex: 'maLoai' },
                        { title: 'Tên loại', dataIndex: 'tenLoai' },
                    ]}
                />
            </Card>

            <Modal
                open={open}
                title="Thêm loại sản phẩm"
                onCancel={() => setOpen(false)}
                onOk={async () => {
                    const values = await form.validateFields();
                    setItems((prev) => [values, ...prev]);
                    message.success('Thêm loại sản phẩm thành công');
                    form.resetFields();
                    setOpen(false);
                }}
            >
                <Form form={form} layout="vertical">
                    <Form.Item name="maLoai" label="Mã loại" rules={[{ required: true }]}>
                        <Input placeholder="VD: LSP003" />
                    </Form.Item>
                    <Form.Item name="tenLoai" label="Tên loại" rules={[{ required: true }]}>
                        <Input placeholder="VD: Văn phòng phẩm" />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
};

export default CategoriesPage;