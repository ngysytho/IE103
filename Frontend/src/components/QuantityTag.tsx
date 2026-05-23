import { Tag } from 'antd';

type Props = { quantity: number };

const QuantityTag = ({ quantity }: Props) => {
    if (quantity <= 0) return <Tag color="red">Hết hàng</Tag>;
    if (quantity < 10) return <Tag color="orange">Sắp hết</Tag>;
    return <Tag color="green">Còn hàng</Tag>;
};

export default QuantityTag;