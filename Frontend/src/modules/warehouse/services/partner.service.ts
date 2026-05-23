import { apiClient } from './apiClient';
import { Partner } from '../types';

export const partnerService = {
    async getAll(): Promise<Partner[]> {
        const res = await apiClient.get('/doitac');
        return res.data;
    },

    async getSuppliers(): Promise<Partner[]> {
        const res = await apiClient.get('/doitac', { params: { loaiDtIn: '1,2' } });
        return res.data;
    },

    async getCustomers(): Promise<Partner[]> {
        const res = await apiClient.get('/doitac', { params: { loaiDtIn: '0,2' } });
        return res.data;
    },

    async create(payload: Partner) {
        const res = await apiClient.post('/doitac', payload);
        return res.data;
    },

    async update(maDt: string, payload: Partial<Partner>) {
        const res = await apiClient.put(`/doitac/${maDt}`, payload);
        return res.data;
    },

    async remove(maDt: string) {
        const res = await apiClient.delete(`/doitac/${maDt}`);
        return res.data;
    },
};