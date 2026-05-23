import { apiClient } from './apiClient';
import { Category } from '../types';

export const categoryService = {
    async getAll(): Promise<Category[]> {
        const res = await apiClient.get('/loaisp');
        return res.data;
    },

    async create(payload: Category) {
        const res = await apiClient.post('/loaisp', payload);
        return res.data;
    },

    async update(maLoai: string, payload: Partial<Category>) {
        const res = await apiClient.put(`/loaisp/${maLoai}`, payload);
        return res.data;
    },

    async remove(maLoai: string) {
        const res = await apiClient.delete(`/loaisp/${maLoai}`);
        return res.data;
    },
};