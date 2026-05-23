import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import {
    Alert,
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
import { useEffect, useState } from 'react';
import { Employee, Product, Stocktake } from '../../modules/warehouse/types';
import { stocktakeService } from '../../modules/warehouse/services/stocktake.service';
import { useAuth } from '../../modules/warehouse/auth/AuthContext';
import { getErrorMessage } from '../../modules/warehouse/utils/errors';
import { nextDocumentCode } from '../../modules/warehouse/utils/ids';

interface StocktakeFormProps {
    employees: Employee[];
    products: Product[];
    onSuccess?: () => void;
}

const StocktakeForm = ({ employees, products, onSuccess }: StocktakeFormProps) => {
    const { user } = useAuth();
    const [form] = Form.useForm();
    const [lastError, setLastError] = useState<string | null>(null);

    useEffect(() => {
        if (user) {
            form.setFieldsValue({ maNv: user.maNv });
        }
    }, [form, user]);

    useEffect(() => {
        const product = products[0];
        const currentItems = form.getFieldValue('items');

        form.setFieldsValue({
            maPkk: form.getFieldValue('maPkk') || nextDocumentCode('KK'),
            items:
                currentItems?.[0]?.maSp || !product
                    ? currentItems
                    : [{ maSp: product.maSp, slThucTe: product.soLuongTon, lyDo: '' }],
        });
    }, [form, products]);

    const employeeOptions = employees.map((item) => ({
        label: `${item.tenNv} - ${item.maNv}`,
        value: item.maNv,
    }));

    const productOptions = products.map((item) => ({
        label: `${item.tenSp} - ${item.maSp}`,
        value: item.maSp,
    }));

    const handleSubmit = async () => {
        try {
            setLastError(null);
            const values = await form.validateFields();
            const selectedProducts = values.items.map((item: { maSp: string }) => item.maSp);
            const hasDuplicateProduct = new Set(selectedProducts).size !== selectedProducts.length;

            if (hasDuplicateProduct) {
                throw new Error('Mỗi sản phẩm chỉ được chọn một dòng trong phiếu kiểm kê');
            }

            const payload: Stocktake = {
                maPkk: values.maPkk,
                ngayKk: values.ngayKk.format('YYYY-MM-DD'),
                maNv: user?.maNv ?? values.maNv,
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

            await stocktakeService.create(payload);
            message.success('Tạo phiếu kiểm kê thành công');
            form.resetFields();
            const product = products[0];
            form.setFieldsValue({
                maPkk: nextDocumentCode('KK'),
                maNv: user?.maNv,
                ngayKk: dayjs(),
                items: product
                    ? [{ maSp: product.maSp, slThucTe: product.soLuongTon, lyDo: '' }]
                    : [{ maSp: undefined, slThucTe: 0, lyDo: '' }],
            });
            onSuccess?.();
        } catch (error) {
            const errorMessage = getErrorMessage(error);
            setLastError(errorMessage);
            message.error(errorMessage);
        }
    };

    return (
        <Card>
            <Form
                form={form}
                layout="vertical"
                initialValues={{
                    maPkk: nextDocumentCode('KK'),
                    ngayKk: dayjs(),
                    items: [{ maSp: undefined, slThucTe: 0, lyDo: '' }],
                }}
            >
                {lastError ? (
                    <Alert type="error" message={lastError} showIcon style={{ marginBottom: 16 }} />
                ) : null}

                <Space style={{ width: '100%' }} size={16} align="start" wrap>
                    <Form.Item name="maPkk" label="Mã phiếu kiểm kê" rules={[{ required: true }]}>
                        <Input readOnly style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="ngayKk" label="Ngày kiểm kê" rules={[{ required: true }]}>
                        <DatePicker style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="maNv" label="Nhân viên kiểm kê" rules={[{ required: true }]}>
                        <Select style={{ width: 260 }} options={employeeOptions} disabled />
                    </Form.Item>

                    <Form.Item name="ghiChu" label="Ghi chú">
                        <Input style={{ width: 300 }} />
                    </Form.Item>
                </Space>

                <Form.List name="items">
                    {(fields, { add, remove }) => (
                        <>
                            {fields.map((field) => (
                                <Space key={field.key} style={{ display: 'flex', marginBottom: 12 }} align="start" wrap>
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
