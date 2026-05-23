import { apiClient } from './apiClient';
import { Employee } from '../types';

export const employeeService = {
    async getAll(): Promise<Employee[]> {
        const res = await apiClient.get('/nhanvien');
        return res.data;
    },

    async getInboundStaff(): Promise<Employee[]> {
        const res = await apiClient.get('/nhanvien', { params: { loaiNvIn: '0,1' } });
        return res.data;
    },

    async getOutboundStaff(): Promise<Employee[]> {
        const res = await apiClient.get('/nhanvien', { params: { loaiNvIn: '0,2' } });
        return res.data;
    },

    async create(payload: Employee) {
        const res = await apiClient.post('/nhanvien', payload);
        return res.data;
    },

    async update(maNv: string, payload: Partial<Employee>) {
        const res = await apiClient.put(`/nhanvien/${maNv}`, payload);
        return res.data;
    },

    async remove(maNv: string) {
        const res = await apiClient.delete(`/nhanvien/${maNv}`);
        return res.data;
    },
};