import { apiClient } from './apiClient';
import { Receipt } from '../types';

export const receiptService = {
    async getAll(): Promise<Receipt[]> {
        const res = await apiClient.get('/phieunhap');
        return res.data;
    },

    async getDetail(maPn: string): Promise<Receipt> {
        const res = await apiClient.get(`/phieunhap/${maPn}`);
        return res.data;
    },

    async create(payload: Receipt) {
        const res = await apiClient.post('/phieunhap', payload);
        return res.data;
    },
};