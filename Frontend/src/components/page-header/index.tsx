import { Button, Flex, Typography } from 'antd';
import { ReactNode } from 'react';

interface PageHeaderProps {
    title: string;
    subtitle?: string;
    extra?: ReactNode;
}

const PageHeader = ({ title, subtitle, extra }: PageHeaderProps) => {
    return (
        <Flex justify="space-between" align="center" style={{ marginBottom: 20 }}>
            <div>
                <Typography.Title level={3} style={{ margin: 0 }}>
                    {title}
                </Typography.Title>
                {subtitle ? (
                    <Typography.Text type="secondary">{subtitle}</Typography.Text>
                ) : null}
            </div>
            <div>{extra}</div>
        </Flex>
    );
};

export default PageHeader;