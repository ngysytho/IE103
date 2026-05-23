import { apiClient } from './apiClient';
import { DashboardSummary } from '../types';

export const dashboardService = {
    async getSummary(): Promise<DashboardSummary> {
        const res = await apiClient.get('/warehouse/dashboard');
        return res.data;
    },
};