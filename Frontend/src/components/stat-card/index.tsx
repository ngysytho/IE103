import { Card, Statistic } from 'antd';
import { ReactNode } from 'react';

interface StatCardProps {
    title: string;
    value: number;
    prefix?: ReactNode;
}

const StatCard = ({ title, value, prefix }: StatCardProps) => {
    return (
        <Card bordered={false}>
            <Statistic title={title} value={value} prefix={prefix} />
        </Card>
    );
};

export default StatCard;