import { apiClient } from './apiClient';
import { AuthUser } from '../types';

export const authService = {
    async login(email: string, password: string): Promise<AuthUser> {
        const res = await apiClient.post('/auth/login', { email, password });
        return res.data;
    },

    async register(payload: {
        hoTen: string;
        email: string;
        password: string;
        loaiNv: number;
    }): Promise<AuthUser> {
        const res = await apiClient.post('/auth/register', payload);
        return res.data;
    },

    async me(): Promise<AuthUser> {
        const res = await apiClient.get('/auth/me');
        return res.data;
    },
};
