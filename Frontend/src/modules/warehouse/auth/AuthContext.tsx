import { createContext, ReactNode, useContext, useMemo, useState } from 'react';
import { message } from 'antd';
import { AuthUser } from '../types';
import { authService } from '../services/auth.service';
import { getErrorMessage } from '../utils/errors';

interface AuthContextValue {
    user: AuthUser | null;
    authError: string | null;
    login: (email: string, password: string) => Promise<boolean>;
    register: (payload: {
        hoTen: string;
        email: string;
        password: string;
        loaiNv: number;
    }) => Promise<boolean>;
    logout: () => void;
    isManager: boolean;
}

const AUTH_STORAGE_KEY = 'ie103_auth_user';
const TOKEN_STORAGE_KEY = 'ie103_auth_token';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const getStoredUser = () => {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY);

    if (!raw) {
        return null;
    }

    try {
        return JSON.parse(raw) as AuthUser;
    } catch {
        localStorage.removeItem(AUTH_STORAGE_KEY);
        localStorage.removeItem(TOKEN_STORAGE_KEY);
        return null;
    }
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<AuthUser | null>(() => getStoredUser());
    const [authError, setAuthError] = useState<string | null>(null);

    const saveAuthUser = (authUser: AuthUser) => {
        localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(authUser));
        localStorage.setItem(TOKEN_STORAGE_KEY, authUser.token);
        setUser(authUser);
    };

    const login = async (email: string, password: string) => {
        setAuthError(null);
        try {
            const authUser = await authService.login(email, password);
            saveAuthUser(authUser);
            return true;
        } catch (error) {
            const errorMessage = getErrorMessage(error);
            setAuthError(errorMessage);
            message.error(errorMessage);
            return false;
        }
    };

    const register = async (payload: {
        hoTen: string;
        email: string;
        password: string;
        loaiNv: number;
    }) => {
        setAuthError(null);
        try {
            const authUser = await authService.register(payload);
            saveAuthUser(authUser);
            return true;
        } catch (error) {
            const errorMessage = getErrorMessage(error);
            setAuthError(errorMessage);
            message.error(errorMessage);
            return false;
        }
    };

    const logout = () => {
        localStorage.removeItem(AUTH_STORAGE_KEY);
        localStorage.removeItem(TOKEN_STORAGE_KEY);
        setUser(null);
    };

    const value = useMemo<AuthContextValue>(
        () => ({
            user,
            authError,
            login,
            register,
            logout,
            isManager: user?.loaiNv === 0,
        }),
        [user, authError],
    );

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error('useAuth must be used inside AuthProvider');
    }

    return context;
};
