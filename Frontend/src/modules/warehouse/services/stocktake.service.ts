import { apiClient } from './apiClient';
import { Stocktake } from '../types';

export const stocktakeService = {
    async getAll(): Promise<Stocktake[]> {
        const res = await apiClient.get('/phieukiemke');
        return res.data;
    },

    async getDetail(maPkk: string): Promise<Stocktake> {
        const res = await apiClient.get(`/phieukiemke/${maPkk}`);
        return res.data;
    },

    async create(payload: Stocktake) {
        const res = await apiClient.post('/phieukiemke', payload);
        return res.data;
    },

    async approve(maPkk: string) {
        const res = await apiClient.patch(`/phieukiemke/${maPkk}/approve`);
        return res.data;
    },
};