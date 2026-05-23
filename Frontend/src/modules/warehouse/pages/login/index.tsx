import { Alert, Button, Card, Form, Input, Radio, Space, Tabs, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';

const roleOptions = [
    { label: 'Quản lý', value: 0 },
    { label: 'NV Nhập kho', value: 1 },
    { label: 'NV Xuất kho', value: 2 },
];

const LoginPage = () => {
    const { authError, login, register, user } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [loginForm] = Form.useForm();
    const [registerForm] = Form.useForm();

    useEffect(() => {
        loginForm.setFieldsValue({ email: 'quanly@gmail.com', password: '123456' });
        registerForm.setFieldsValue({ loaiNv: 0 });
    }, [loginForm, registerForm]);

    if (user) {
        return <Navigate to="/warehouse/dashboard" replace />;
    }

    const handleSubmit = async () => {
        const values = await loginForm.validateFields();
        setLoading(true);

        try {
            const ok = await login(values.email, values.password);
            if (ok) {
                navigate('/warehouse/dashboard', { replace: true });
            }
        } finally {
            setLoading(false);
        }
    };

    const handleRegister = async () => {
        const values = await registerForm.validateFields();
        setLoading(true);

        try {
            const ok = await register({
                hoTen: values.hoTen,
                email: values.email,
                password: values.password,
                loaiNv: values.loaiNv,
            });
            if (ok) {
                navigate('/warehouse/dashboard', { replace: true });
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div
            style={{
                minHeight: '100vh',
                display: 'grid',
                placeItems: 'center',
                background: '#eef2f7',
                padding: 24,
            }}
        >
            <Card style={{ width: 460, borderRadius: 8 }}>
                <Space direction="vertical" size={20} style={{ width: '100%' }}>
                    <div>
                        <Typography.Title level={3} style={{ margin: 0 }}>
                            Quản lý kho IE103
                        </Typography.Title>
                        <Typography.Text type="secondary">Đăng nhập hoặc tạo tài khoản</Typography.Text>
                    </div>

                    {authError ? <Alert type="error" message={authError} showIcon /> : null}

                    <Tabs
                        items={[
                            {
                                key: 'login',
                                label: 'Đăng nhập',
                                children: (
                                    <Form form={loginForm} layout="vertical">
                                        <Form.Item
                                            name="email"
                                            label="Gmail / Email"
                                            rules={[{ required: true, type: 'email' }]}
                                        >
                                            <Input placeholder="quanly@ie103.local" />
                                        </Form.Item>

                                        <Form.Item
                                            name="password"
                                            label="Mật khẩu"
                                            rules={[{ required: true }]}
                                        >
                                            <Input.Password />
                                        </Form.Item>

                                        <Button
                                            type="primary"
                                            block
                                            loading={loading}
                                            onClick={() => void handleSubmit()}
                                        >
                                            Đăng nhập
                                        </Button>
                                    </Form>
                                ),
                            },
                            {
                                key: 'register',
                                label: 'Tạo tài khoản',
                                children: (
                                    <Form form={registerForm} layout="vertical">
                                        <Form.Item
                                            name="hoTen"
                                            label="Tên"
                                            rules={[{ required: true }]}
                                        >
                                            <Input placeholder="Nguyễn Văn A" />
                                        </Form.Item>

                                        <Form.Item
                                            name="email"
                                            label="Gmail / Email"
                                            rules={[{ required: true, type: 'email' }]}
                                        >
                                            <Input placeholder="ten@gmail.com" />
                                        </Form.Item>

                                        <Form.Item
                                            name="password"
                                            label="Mật khẩu"
                                            rules={[{ required: true, min: 4 }]}
                                        >
                                            <Input.Password />
                                        </Form.Item>

                                        <Form.Item
                                            name="loaiNv"
                                            label="Loại tài khoản"
                                            rules={[{ required: true }]}
                                        >
                                            <Radio.Group
                                                optionType="button"
                                                buttonStyle="solid"
                                                options={roleOptions}
                                                style={{
                                                    display: 'grid',
                                                    gridTemplateColumns: 'repeat(3, 1fr)',
                                                }}
                                            />
                                        </Form.Item>

                                        <Button
                                            type="primary"
                                            block
                                            loading={loading}
                                            onClick={() => void handleRegister()}
                                        >
                                            Tạo và đăng nhập
                                        </Button>
                                    </Form>
                                ),
                            },
                        ]}
                    />
                </Space>
            </Card>
        </div>
    );
};

export default LoginPage;
