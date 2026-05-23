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
import { Employee, Product, Stocktake } from '../../modules/warehouse/types';
import { warehouseMockService } from '../../modules/warehouse/services/warehouse.mock';

// giữ nguyên code StocktakeForm của bạn

interface StocktakeFormProps {
    employees: Employee[];
    products: Product[];
    onSuccess?: () => void;
}

const StocktakeForm = ({ employees, products, onSuccess }: StocktakeFormProps) => {
    const [form] = Form.useForm();

    const employeeOptions = employees.map((item) => ({
        label: `${item.tenNv} - ${item.maNv}`,
        value: item.maNv,
    }));

    const productOptions = products.map((item) => ({
        label: `${item.tenSp} - ${item.maSp}`,
        value: item.maSp,
    }));

    const handleSubmit = async () => {
        const values = await form.validateFields();

        const payload: Stocktake = {
            maPkk: values.maPkk,
            ngayKk: values.ngayKk.format('YYYY-MM-DD'),
            maNv: values.maNv,
            ghiChu: values.ghiChu ?? '',
            status: 'pending',
            items: values.items.map(
                (item: { maSp: string; slThucTe: number; lyDo: string }) => {
                    const product = products.find((p) => p.maSp === item.maSp);

                    return {
                        maSp: item.maSp,
                        tenSp: product?.tenSp,
                        slHeThong: product?.soLuongTon ?? 0,
                        slThucTe: item.slThucTe,
                        lyDo: item.lyDo ?? '',
                    };
                },
            ),
        };

        await warehouseMockService.createStocktake(payload);
        message.success('Tạo phiếu kiểm kê thành công');
        form.resetFields();
        onSuccess?.();
    };

    return (
        <Card>
            <Form
                form={form}
                layout="vertical"
                initialValues={{
                    ngayKk: dayjs(),
                    items: [{ maSp: undefined, slThucTe: 0, lyDo: '' }],
                }}
            >
                <Space style={{ width: '100%' }} size={16} align="start">
                    <Form.Item name="maPkk" label="Mã phiếu kiểm kê" rules={[{ required: true }]}>
                        <Input style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="ngayKk" label="Ngày kiểm kê" rules={[{ required: true }]}>
                        <DatePicker style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="maNv" label="Nhân viên kiểm kê" rules={[{ required: true }]}>
                        <Select style={{ width: 260 }} options={employeeOptions} />
                    </Form.Item>

                    <Form.Item name="ghiChu" label="Ghi chú">
                        <Input style={{ width: 300 }} />
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
                                        name={[field.name, 'slThucTe']}
                                        label="SL thực tế"
                                        rules={[{ required: true, type: 'number', min: 0 }]}
                                    >
                                        <InputNumber min={0} style={{ width: 160 }} />
                                    </Form.Item>

                                    <Form.Item
                                        {...field}
                                        name={[field.name, 'lyDo']}
                                        label="Lý do chênh lệch"
                                    >
                                        <Input style={{ width: 300 }} />
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
                                Thêm dòng kiểm kê
                            </Button>
                        </>
                    )}
                </Form.List>

                <div style={{ marginTop: 20 }}>
                    <Button type="primary" onClick={() => void handleSubmit()}>
                        Lưu phiếu kiểm kê
                    </Button>
                </div>
            </Form>
        </Card>
    );
};

export default StocktakeForm;