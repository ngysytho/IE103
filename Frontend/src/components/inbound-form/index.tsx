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
import { Employee, Partner, Product, Receipt } from '../../modules/warehouse/types';
import { warehouseMockService } from '../../modules/warehouse/services/warehouse.mock';

interface InboundFormProps {
    employees: Employee[];
    partners: Partner[];
    products: Product[];
    onSuccess?: () => void;
}

const InboundForm = ({ employees, partners, products, onSuccess }: InboundFormProps) => {
    const [form] = Form.useForm();

    const supplierOptions = partners
        .filter((item) => item.loaiDt === 1 || item.loaiDt === 2)
        .map((item) => ({
            label: `${item.tenDt} - ${item.maDt}`,
            value: item.maDt,
        }));

    const employeeOptions = employees
        .filter((item) => item.loaiNv === 0 || item.loaiNv === 1)
        .map((item) => ({
            label: `${item.tenNv} - ${item.maNv}`,
            value: item.maNv,
        }));

    const productOptions = products.map((item) => ({
        label: `${item.tenSp} - ${item.maSp}`,
        value: item.maSp,
    }));

    const handleSubmit = async () => {
        const values = await form.validateFields();

        const payload: Receipt = {
            maPn: values.maPn,
            ngayNhap: values.ngayNhap.format('YYYY-MM-DD'),
            maNv: values.maNv,
            maDt: values.maDt,
            items: values.items,
        };

        await warehouseMockService.createReceipt(payload);
        message.success('Lập phiếu nhập thành công');
        form.resetFields();
        onSuccess?.();
    };

    return (
        <Card>
            <Form
                form={form}
                layout="vertical"
                initialValues={{
                    ngayNhap: dayjs(),
                    items: [{ maSp: undefined, soLuong: 1, donGia: 1 }],
                }}
            >
                <Space style={{ width: '100%' }} size={16} align="start">
                    <Form.Item name="maPn" label="Mã phiếu nhập" rules={[{ required: true }]}>
                        <Input style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="ngayNhap" label="Ngày nhập" rules={[{ required: true }]}>
                        <DatePicker style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="maNv" label="Nhân viên nhập" rules={[{ required: true }]}>
                        <Select style={{ width: 260 }} options={employeeOptions} />
                    </Form.Item>

                    <Form.Item name="maDt" label="Nhà cung cấp" rules={[{ required: true }]}>
                        <Select style={{ width: 260 }} options={supplierOptions} />
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
                                        <Select style={{ width: 320 }} options={productOptions} />
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
                                        label="Đơn giá"
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
                        Lưu phiếu nhập
                    </Button>
                </div>
            </Form>
        </Card>
    );
};

export default InboundForm;