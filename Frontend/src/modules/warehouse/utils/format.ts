export const formatCurrency = (value: number) =>
    new Intl.NumberFormat('vi-VN').format(value);

export const formatDate = (value?: string) => {
    if (!value) return '';
    return new Date(value).toLocaleDateString('vi-VN');
};