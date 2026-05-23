import { apiClient } from './apiClient';
import {
    ImportByPartnerReport,
    LowStockReport,
    StocktakeDifferenceReport,
} from '../types';

export const reportService = {
    async getLowStock(): Promise<LowStockReport[]> {
        const res = await apiClient.get('/reports/low-stock');
        return res.data;
    },

    async getImportByPartner(): Promise<ImportByPartnerReport[]> {
        const res = await apiClient.get('/reports/import-by-partner');
        return res.data;
    },

    async getStocktakeDifferences(): Promise<StocktakeDifferenceReport[]> {
        const res = await apiClient.get('/reports/stocktake-differences');
        return res.data;
    },
};
