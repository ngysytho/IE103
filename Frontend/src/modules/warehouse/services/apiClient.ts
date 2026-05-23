import axios from 'axios';

export const apiClient = axios.create({
    baseURL:
        process.env.REACT_APP_API_URL ||
        `${process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080'}/api`,
    timeout: 10000,
});

apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('ie103_auth_token');

    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});
