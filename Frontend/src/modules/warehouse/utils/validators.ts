import { Employee, Partner, Product } from '../types';

export const isSupplier = (partner?: Partner | null) =>
    !!partner && (partner.loaiDt === 1 || partner.loaiDt === 2);

export const isCustomer = (partner?: Partner | null) =>
    !!partner && (partner.loaiDt === 0 || partner.loaiDt === 2);

export const canCreateOutbound = (employee?: Employee | null) =>
    !!employee && (employee.loaiNv === 0 || employee.loaiNv === 2);

export const canCreateInbound = (employee?: Employee | null) =>
    !!employee && (employee.loaiNv === 0 || employee.loaiNv === 1);

export const isPositiveNumber = (value: number) => value > 0;

export const hasEnoughStock = (
    product: Product | undefined,
    requiredQty: number,
) => {
    if (!product) return false;
    return product.soLuongTon >= requiredQty;
};