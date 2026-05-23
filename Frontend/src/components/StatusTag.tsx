import { Tag } from 'antd';

type Props = {
    type: 'customer' | 'supplier' | 'both' | 'manager' | 'inbound' | 'outbound';
};

const StatusTag = ({ type }: Props) => {
    if (type === 'customer') return <Tag color="blue">Khách hàng</Tag>;
    if (type === 'supplier') return <Tag color="green">Nhà cung cấp</Tag>;
    if (type === 'both') return <Tag color="purple">Cả hai</Tag>;
    if (type === 'manager') return <Tag color="gold">Quản lý</Tag>;
    if (type === 'inbound') return <Tag color="cyan">NV nhập</Tag>;
    return <Tag color="magenta">NV xuất</Tag>;
};

export default StatusTag;