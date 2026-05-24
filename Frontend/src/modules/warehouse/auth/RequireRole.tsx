import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { EmployeeType } from '../types';
import { useAuth } from './AuthContext';

interface RequireRoleProps {
    roles: EmployeeType[];
    children: ReactNode;
}

const RequireRole = ({ roles, children }: RequireRoleProps) => {
    const { user } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    if (!roles.includes(user.loaiNv)) {
        return <Navigate to="/warehouse/dashboard" replace />;
    }

    return <>{children}</>;
};

export default RequireRole;
