import { Layout, Menu } from 'antd';
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
} from '@ant-design/icons';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';

const { Sider, Content } = Layout;

const items = [
    { key: '/warehouse/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
    { key: '/warehouse/categories', icon: <AppstoreOutlined />, label: 'Loại sản phẩm' },
    { key: '/warehouse/products', icon: <BarcodeOutlined />, label: 'Sản phẩm' },
    { key: '/warehouse/partners', icon: <TeamOutlined />, label: 'Đối tác' },
    { key: '/warehouse/employees', icon: <UserOutlined />, label: 'Nhân viên' },
    { key: '/warehouse/inbound', icon: <InboxOutlined />, label: 'Nhập kho' },
    { key: '/warehouse/outbound', icon: <ExportOutlined />, label: 'Xuất kho' },
    { key: '/warehouse/stocktake', icon: <AuditOutlined />, label: 'Kiểm kê' },
    { key: '/warehouse/history', icon: <HistoryOutlined />, label: 'Lịch sử' },
];

const WarehouseLayout = () => {
    const navigate = useNavigate();
    const location = useLocation();

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
                    items={items}
                    onClick={({ key }) => navigate(key)}
                    style={{ borderRight: 0 }}
                />
            </Sider>

            <Layout>
                <Content style={{ padding: 24, background: '#f5f7fb' }}>
                    <Outlet />
                </Content>
            </Layout>
        </Layout>
    );
};

export default WarehouseLayout;