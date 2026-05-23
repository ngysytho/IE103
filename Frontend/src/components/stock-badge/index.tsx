import { Tag } from 'antd';

interface StockBadgeProps {
    quantity: number;
}

const StockBadge = ({ quantity }: StockBadgeProps) => {
    if (quantity === 0) return <Tag color="red">Hết hàng</Tag>;
    if (quantity < 5) return <Tag color="orange">Sắp hết</Tag>;
    return <Tag color="green">Ổn định</Tag>;
};

export default StockBadge;