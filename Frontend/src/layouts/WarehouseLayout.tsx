import { Button, Layout, Menu, Space, Tag, Typography } from 'antd';
import {
    AppstoreOutlined,
    BarcodeOutlined,
    TeamOutlined,
    UserOutlined,
    InboxOutlined,
    ExportOutlined,
    AuditOutlined,
    HistoryOutlined,
    DashboardOutlined,
    LogoutOutlined,
} from '@ant-design/icons';
import { Navigate, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { ReactNode } from 'react';
import { useAuth } from '../modules/warehouse/auth/AuthContext';
import { EmployeeType } from '../modules/warehouse/types';

const { Header, Sider, Content } = Layout;

const allItems: Array<{
    key: string;
    icon: ReactNode;
    label: string;
    roles?: EmployeeType[];
}> = [
    { key: '/warehouse/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
    { key: '/warehouse/categories', icon: <AppstoreOutlined />, label: 'Loại sản phẩm' },
    { key: '/warehouse/products', icon: <BarcodeOutlined />, label: 'Sản phẩm' },
    { key: '/warehouse/partners', icon: <TeamOutlined />, label: 'Đối tác' },
    { key: '/warehouse/employees', icon: <UserOutlined />, label: 'Nhân viên', roles: [0] },
    { key: '/warehouse/inbound', icon: <InboxOutlined />, label: 'Nhập kho', roles: [0, 1] },
    { key: '/warehouse/outbound', icon: <ExportOutlined />, label: 'Xuất kho', roles: [0, 2] },
    { key: '/warehouse/stocktake', icon: <AuditOutlined />, label: 'Kiểm kê' },
    { key: '/warehouse/history', icon: <HistoryOutlined />, label: 'Lịch sử' },
];

const roleLabel = (loaiNv: EmployeeType) => {
    if (loaiNv === 0) return <Tag color="gold">Quản lý</Tag>;
    if (loaiNv === 1) return <Tag color="cyan">NV nhập kho</Tag>;
    return <Tag color="magenta">NV xuất kho</Tag>;
};

const WarehouseLayout = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { user, logout } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    const visibleItems = allItems.filter((item) => !item.roles || item.roles.includes(user.loaiNv));

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider width={250} theme="light">
                <div
                    style={{
                        padding: 20,
                        fontWeight: 700,
                        fontSize: 18,
                        borderBottom: '1px solid #f0f0f0',
                    }}
                >
                    Quản lý kho
                </div>
                <Menu
                    mode="inline"
                    selectedKeys={[location.pathname]}
                    items={visibleItems}
                    onClick={({ key }) => navigate(key)}
                    style={{ borderRight: 0 }}
                />
            </Sider>

            <Layout>
                <Header
                    style={{
                        height: 64,
                        background: '#fff',
                        display: 'flex',
                        justifyContent: 'flex-end',
                        alignItems: 'center',
                        padding: '0 24px',
                        borderBottom: '1px solid #f0f0f0',
                    }}
                >
                    <Space size={12}>
                        <Typography.Text strong>{user.tenNv}</Typography.Text>
                        {roleLabel(user.loaiNv)}
                        <Button
                            icon={<LogoutOutlined />}
                            onClick={() => {
                                logout();
                                navigate('/login', { replace: true });
                            }}
                        >
                            Đăng xuất
                        </Button>
                    </Space>
                </Header>

                <Content style={{ padding: 24, background: '#f5f7fb' }}>
                    <Outlet />
                </Content>
            </Layout>
        </Layout>
    );
};

export default WarehouseLayout;
