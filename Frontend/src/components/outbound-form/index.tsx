import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import {
    Button,
    Card,
    DatePicker,
    Form,
    Input,
    InputNumber,
    Select,
    Space,
    message,
} from 'antd';
import dayjs from 'dayjs';
import { Employee, Partner, Product, Issue } from '../../modules/warehouse/types';
import { warehouseMockService } from '../../modules/warehouse/services/warehouse.mock';

// giữ nguyên code OutboundForm của bạn

interface OutboundFormProps {
    employees: Employee[];
    partners: Partner[];
    products: Product[];
    onSuccess?: () => void;
}

const OutboundForm = ({ employees, partners, products, onSuccess }: OutboundFormProps) => {
    const [form] = Form.useForm();

    const customerOptions = partners
        .filter((item) => item.loaiDt === 0 || item.loaiDt === 2)
        .map((item) => ({
            label: `${item.tenDt} - ${item.maDt}`,
            value: item.maDt,
        }));

    const employeeOptions = employees
        .filter((item) => item.loaiNv === 0 || item.loaiNv === 2)
        .map((item) => ({
            label: `${item.tenNv} - ${item.maNv}`,
            value: item.maNv,
        }));

    const productOptions = products.map((item) => ({
        label: `${item.tenSp} - ${item.maSp} (Tồn: ${item.soLuongTon})`,
        value: item.maSp,
    }));

    const handleSubmit = async () => {
        const values = await form.validateFields();

        values.items.forEach((item: { maSp: string; soLuong: number }) => {
            const product = products.find((p) => p.maSp === item.maSp);

            if (!product) {
                throw new Error(`Không tìm thấy sản phẩm ${item.maSp}`);
            }

            if (item.soLuong > product.soLuongTon) {
                throw new Error(
                    `Sản phẩm ${product.tenSp} chỉ còn ${product.soLuongTon}, không đủ để xuất ${item.soLuong}`,
                );
            }
        });

        const payload: Issue = {
            maPx: values.maPx,
            ngayXuat: values.ngayXuat.format('YYYY-MM-DD'),
            maNv: values.maNv,
            maDt: values.maDt,
            items: values.items,
        };

        await warehouseMockService.createIssue(payload);
        message.success('Lập phiếu xuất thành công');
        form.resetFields();
        onSuccess?.();
    };

    return (
        <Card>
            <Form
                form={form}
                layout="vertical"
                initialValues={{
                    ngayXuat: dayjs(),
                    items: [{ maSp: undefined, soLuong: 1, donGia: 1 }],
                }}
            >
                <Space style={{ width: '100%' }} size={16} align="start">
                    <Form.Item name="maPx" label="Mã phiếu xuất" rules={[{ required: true }]}>
                        <Input style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="ngayXuat" label="Ngày xuất" rules={[{ required: true }]}>
                        <DatePicker style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="maNv" label="Nhân viên xuất" rules={[{ required: true }]}>
                        <Select style={{ width: 260 }} options={employeeOptions} />
                    </Form.Item>

                    <Form.Item name="maDt" label="Khách hàng" rules={[{ required: true }]}>
                        <Select style={{ width: 260 }} options={customerOptions} />
                    </Form.Item>
                </Space>

                <Form.List name="items">
                    {(fields, { add, remove }) => (
                        <>
                            {fields.map((field) => (
                                <Space key={field.key} style={{ display: 'flex', marginBottom: 12 }} align="start">
                                    <Form.Item
                                        {...field}
                                        name={[field.name, 'maSp']}
                                        label="Sản phẩm"
                                        rules={[{ required: true }]}
                                    >
                                        <Select style={{ width: 340 }} options={productOptions} />
                                    </Form.Item>

                                    <Form.Item
                                        {...field}
                                        name={[field.name, 'soLuong']}
                                        label="Số lượng"
                                        rules={[{ required: true, type: 'number', min: 1 }]}
                                    >
                                        <InputNumber min={1} style={{ width: 160 }} />
                                    </Form.Item>

                                    <Form.Item
                                        {...field}
                                        name={[field.name, 'donGia']}
                                        label="Đơn giá xuất"
                                        rules={[{ required: true, type: 'number', min: 1 }]}
                                    >
                                        <InputNumber min={1} style={{ width: 180 }} />
                                    </Form.Item>

                                    <Button
                                        danger
                                        icon={<DeleteOutlined />}
                                        style={{ marginTop: 30 }}
                                        onClick={() => remove(field.name)}
                                    />
                                </Space>
                            ))}

                            <Button type="dashed" icon={<PlusOutlined />} onClick={() => add()}>
                                Thêm dòng sản phẩm
                            </Button>
                        </>
                    )}
                </Form.List>

                <div style={{ marginTop: 20 }}>
                    <Button type="primary" onClick={() => void handleSubmit()}>
                        Lưu phiếu xuất
                    </Button>
                </div>
            </Form>
        </Card>
    );
};

export default OutboundForm;