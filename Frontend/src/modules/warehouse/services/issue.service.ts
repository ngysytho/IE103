import { apiClient } from './apiClient';
import { Issue } from '../types';

export const issueService = {
    async getAll(): Promise<Issue[]> {
        const res = await apiClient.get('/phieuxuat');
        return res.data;
    },

    async getDetail(maPx: string): Promise<Issue> {
        const res = await apiClient.get(`/phieuxuat/${maPx}`);
        return res.data;
    },

    async create(payload: Issue) {
        const res = await apiClient.post('/phieuxuat', payload);
        return res.data;
    },
};