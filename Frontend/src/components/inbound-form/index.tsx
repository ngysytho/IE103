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
import { Employee, Partner, Product, Receipt } from '../../modules/warehouse/types';
import { receiptService } from '../../modules/warehouse/services/receipt.service';
import { useAuth } from '../../modules/warehouse/auth/AuthContext';
import { getErrorMessage } from '../../modules/warehouse/utils/errors';
import { nextDocumentCode } from '../../modules/warehouse/utils/ids';

interface InboundFormProps {
    employees: Employee[];
    partners: Partner[];
    products: Product[];
    onSuccess?: () => void;
}

const InboundForm = ({ employees, partners, products, onSuccess }: InboundFormProps) => {
    const { user } = useAuth();
    const [form] = Form.useForm();
    const [lastError, setLastError] = useState<string | null>(null);

    useEffect(() => {
        if (user) {
            form.setFieldsValue({ maNv: user.maNv });
        }
    }, [form, user]);

    useEffect(() => {
        const supplier = partners.find((item) => item.loaiDt === 1 || item.loaiDt === 2);
        const product = products[0];
        const currentItems = form.getFieldValue('items');

        form.setFieldsValue({
            maPn: form.getFieldValue('maPn') || nextDocumentCode('PN'),
            maDt: form.getFieldValue('maDt') || supplier?.maDt,
            items:
                currentItems?.[0]?.maSp || !product
                    ? currentItems
                    : [{ maSp: product.maSp, soLuong: 1, donGia: product.giaNhap || 1 }],
        });
    }, [form, partners, products]);

    const partnerTypeText = (value: number) => {
        if (value === 0) return 'Khách hàng';
        if (value === 1) return 'Nhà cung cấp';
        return 'Cả hai';
    };

    const partnerOptions = partners.map((item) => ({
        label: `${item.tenDt} - ${item.maDt} (${partnerTypeText(item.loaiDt)})`,
        value: item.maDt,
    }));

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
                throw new Error('Mỗi sản phẩm chỉ được chọn một dòng trong phiếu nhập');
            }

            const payload: Receipt = {
                maPn: values.maPn,
                ngayNhap: values.ngayNhap.format('YYYY-MM-DD'),
                maNv: user?.maNv ?? values.maNv,
                maDt: values.maDt,
                items: values.items,
            };

            await receiptService.create(payload);
            message.success('Lập phiếu nhập thành công bằng sp_LapPhieuNhap');
            form.resetFields();
            const product = products[0];
            form.setFieldsValue({
                maPn: nextDocumentCode('PN'),
                maNv: user?.maNv,
                ngayNhap: dayjs(),
                maDt: partners.find((item) => item.loaiDt === 1 || item.loaiDt === 2)?.maDt,
                items: product
                    ? [{ maSp: product.maSp, soLuong: 1, donGia: product.giaNhap || 1 }]
                    : [{ maSp: undefined, soLuong: 1, donGia: 1 }],
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
                    maPn: nextDocumentCode('PN'),
                    ngayNhap: dayjs(),
                    items: [{ maSp: undefined, soLuong: 1, donGia: 1 }],
                }}
            >
                {lastError ? (
                    <Alert type="error" message={lastError} showIcon style={{ marginBottom: 16 }} />
                ) : null}

                <Space style={{ width: '100%' }} size={16} align="start" wrap>
                    <Form.Item name="maPn" label="Mã phiếu nhập" rules={[{ required: true }]}>
                        <Input readOnly style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="ngayNhap" label="Ngày nhập" rules={[{ required: true }]}>
                        <DatePicker style={{ width: 220 }} />
                    </Form.Item>

                    <Form.Item name="maNv" label="Nhân viên nhập" rules={[{ required: true }]}>
                        <Select style={{ width: 260 }} options={employeeOptions} disabled />
                    </Form.Item>

                    <Form.Item name="maDt" label="Nhà cung cấp" rules={[{ required: true }]}>
                        <Select style={{ width: 300 }} options={partnerOptions} />
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
