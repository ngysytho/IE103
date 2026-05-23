import { apiClient } from './apiClient';
import { Product } from '../types';

export const productService = {
    async getAll(): Promise<Product[]> {
        const res = await apiClient.get('/sanpham');
        return res.data;
    },

    async getById(maSp: string): Promise<Product> {
        const res = await apiClient.get(`/sanpham/${maSp}`);
        return res.data;
    },

    async create(payload: Product) {
        const res = await apiClient.post('/sanpham', payload);
        return res.data;
    },

    async update(maSp: string, payload: Partial<Product>) {
        const res = await apiClient.put(`/sanpham/${maSp}`, payload);
        return res.data;
    },

    async remove(maSp: string) {
        const res = await apiClient.delete(`/sanpham/${maSp}`);
        return res.data;
    },
};