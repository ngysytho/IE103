import {
    Button,
    Card,
    Form,
    Input,
    InputNumber,
    Modal,
    Select,
    Space,
    Table,
    message,
} from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import StockBadge from '../../../../components/stock-badge';
import { Category, Product } from '../../types';
import { categoryService } from '../../services/category.service';
import { productService } from '../../services/product.service';
import { getErrorMessage } from '../../utils/errors';

const ProductsPage = () => {
    const [items, setItems] = useState<Product[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [open, setOpen] = useState(false);
    const [form] = Form.useForm<Product>();

    const fetchData = async () => {
        try {
            const [products, categories] = await Promise.all([
                productService.getAll(),
                categoryService.getAll(),
            ]);

            setItems(products);
            setCategories(categories);
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
                title="Sản phẩm"
                subtitle="Quản lý SANPHAM và theo dõi tồn kho"
                extra={
                    <Button type="primary" onClick={() => setOpen(true)}>
                        Thêm sản phẩm
                    </Button>
                }
            />

            <Card>
                <Table
                    rowKey="maSp"
                    dataSource={items}
                    columns={[
                        { title: 'Mã SP', dataIndex: 'maSp' },
                        { title: 'Tên sản phẩm', dataIndex: 'tenSp' },
                        { title: 'ĐVT', dataIndex: 'dvt' },
                        {
                            title: 'Giá nhập',
                            dataIndex: 'giaNhap',
                            render: (value: number) => value.toLocaleString('vi-VN'),
                        },
                        { title: 'Mã loại', dataIndex: 'maLoai' },
                        { title: 'Số lượng tồn', dataIndex: 'soLuongTon' },
                        {
                            title: 'Trạng thái',
                            render: (_, record: Product) => <StockBadge quantity={record.soLuongTon} />,
                        },
                    ]}
                />
            </Card>

            <Modal
                open={open}
                title="Thêm sản phẩm"
                onCancel={() => setOpen(false)}
                onOk={async () => {
                    try {
                        const values = await form.validateFields();
                        await productService.create(values);
                        await fetchData();
                        form.resetFields();
                        setOpen(false);
                        message.success('Thêm sản phẩm thành công');
                    } catch (error) {
                        message.error(getErrorMessage(error));
                    }
                }}
            >
                <Form form={form} layout="vertical" initialValues={{ soLuongTon: 0 }}>
                    <Form.Item name="maSp" label="Mã sản phẩm" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>

                    <Form.Item name="tenSp" label="Tên sản phẩm" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>

                    <Form.Item name="dvt" label="Đơn vị tính" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>

                    <Form.Item
                        name="giaNhap"
                        label="Giá nhập"
                        rules={[{ required: true, type: 'number', min: 1 }]}
                    >
                        <InputNumber style={{ width: '100%' }} min={1} />
                    </Form.Item>

                    <Form.Item name="maLoai" label="Loại sản phẩm" rules={[{ required: true }]}>
                        <Select
                            options={categories.map((item) => ({
                                label: item.tenLoai,
                                value: item.maLoai,
                            }))}
                        />
                    </Form.Item>

                    <Form.Item
                        name="soLuongTon"
                        label="Số lượng tồn ban đầu"
                        rules={[{ required: true, type: 'number', min: 0 }]}
                    >
                        <InputNumber style={{ width: '100%' }} min={0} />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
};

export default ProductsPage;
