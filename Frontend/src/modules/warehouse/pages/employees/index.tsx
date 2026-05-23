import { Button, Card, Form, Input, Modal, Select, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import PageHeader from '../../../../components/page-header';
import { EMPLOYEE_TYPE_OPTIONS } from '../../constants/options';
import { Employee } from '../../types';
import { employeeService } from '../../services/employee.service';
import { getErrorMessage } from '../../utils/errors';

const renderEmployeeType = (value: number) => {
    if (value === 0) return <Tag color="gold">Quản lý</Tag>;
    if (value === 1) return <Tag color="cyan">NV nhập kho</Tag>;
    return <Tag color="magenta">NV xuất kho</Tag>;
};

const EmployeesPage = () => {
    const [items, setItems] = useState<Employee[]>([]);
    const [open, setOpen] = useState(false);
    const [form] = Form.useForm<Employee>();

    useEffect(() => {
        const fetchData = async () => {
            try {
                const res = await employeeService.getAll();
                setItems(res);
            } catch (error) {
                message.error(getErrorMessage(error));
            }
        };

        void fetchData();
    }, []);

    return (
        <>
            <PageHeader
                title="Nhân viên"
                subtitle="Thiết lập hồ sơ nhân sự và phân quyền"
                extra={
                    <Button type="primary" onClick={() => setOpen(true)}>
                        Thêm nhân viên
                    </Button>
                }
            />

            <Card>
                <Table
                    rowKey="maNv"
                    dataSource={items}
                    columns={[
                        { title: 'Mã NV', dataIndex: 'maNv' },
                        { title: 'Tên NV', dataIndex: 'tenNv' },
                        { title: 'Địa chỉ', dataIndex: 'dchi' },
                        { title: 'SĐT', dataIndex: 'sdt' },
                        { title: 'Vai trò', dataIndex: 'loaiNv', render: renderEmployeeType },
                    ]}
                />
            </Card>

            <Modal
                open={open}
                title="Thêm nhân viên"
                onCancel={() => setOpen(false)}
                onOk={async () => {
                    try {
                        const values = await form.validateFields();
                        await employeeService.create(values);
                        const res = await employeeService.getAll();
                        setItems(res);
                        message.success('Thêm nhân viên thành công');
                        form.resetFields();
                        setOpen(false);
                    } catch (error) {
                        message.error(getErrorMessage(error));
                    }
                }}
            >
                <Form form={form} layout="vertical">
                    <Form.Item name="maNv" label="Mã nhân viên" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="tenNv" label="Tên nhân viên" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="dchi" label="Địa chỉ" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="sdt" label="Số điện thoại" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="loaiNv" label="Loại nhân viên" rules={[{ required: true }]}>
                        <Select options={EMPLOYEE_TYPE_OPTIONS} />
                    </Form.Item>
                </Form>
            </Modal>
        </>
    );
};

export default EmployeesPage;
