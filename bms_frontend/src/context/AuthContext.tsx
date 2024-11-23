'use client';
import React, { createContext, useState, useEffect } from 'react';
import api from '../service/api';

interface User {
    email: string;
    role: 'admin' | 'user';
}

interface AuthContextType {
    user: User | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
    }, []);

    const login = async (email: string, password: string) => {
        try {
            const response = await api.post('/authenticateAccount', { email, password });
            console.log('AuthProvider login response.data: ', response.data);
            const loggedInUser = response.data.user;
            setUser(loggedInUser);
            localStorage.setItem('user', JSON.stringify(loggedInUser));
        } catch (error) {
            console.error('Login failed', error);
            throw error;
        }
    };

    const logout = () => {
        setUser(null);
        localStorage.removeItem('user');
        // localStorage.removeItem('token');
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};