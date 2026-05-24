import { createBrowserRouter } from 'react-router-dom';
import WarehouseLayout from '../layouts/WarehouseLayout';
import WarehouseDashboardPage from '../modules/warehouse/pages/dashboard';
import CategoriesPage from '../modules/warehouse/pages/categories';
import ProductsPage from '../modules/warehouse/pages/products';
import PartnersPage from '../modules/warehouse/pages/partners';
import EmployeesPage from '../modules/warehouse/pages/employees';
import InboundPage from '../modules/warehouse/pages/inbound';
import OutboundPage from '../modules/warehouse/pages/outbound';
import StocktakePage from '../modules/warehouse/pages/stocktake';
import HistoryPage from '../modules/warehouse/pages/history';
import LoginPage from '../modules/warehouse/pages/login';
import RequireRole from '../modules/warehouse/auth/RequireRole';

import { Navigate } from 'react-router-dom';

export const router = createBrowserRouter([
    {
        path: '/',
        element: <Navigate to="/warehouse/dashboard" />,
    },
    {
        path: '/login',
        element: <LoginPage />,
    },
    {
        path: '/warehouse',
        element: <WarehouseLayout />,
        children: [
            { path: 'dashboard', element: <WarehouseDashboardPage /> },
            { path: 'categories', element: <CategoriesPage /> },
            { path: 'products', element: <ProductsPage /> },
            { path: 'partners', element: <PartnersPage /> },
            {
                path: 'employees',
                element: (
                    <RequireRole roles={[0]}>
                        <EmployeesPage />
                    </RequireRole>
                ),
            },
            {
                path: 'inbound',
                element: (
                    <RequireRole roles={[0, 1]}>
                        <InboundPage />
                    </RequireRole>
                ),
            },
            {
                path: 'outbound',
                element: (
                    <RequireRole roles={[0, 2]}>
                        <OutboundPage />
                    </RequireRole>
                ),
            },
            { path: 'stocktake', element: <StocktakePage /> },
            { path: 'history', element: <HistoryPage /> },
        ],
    },
]);
