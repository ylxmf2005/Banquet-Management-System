'use client';
import React, { createContext, useState, useEffect } from 'react';
import api from '../utils/api';

interface User {
    email: string;
    role: 'admin' | 'user';
    // Add other user fields
}

interface AuthContextType {
    user: User | null;
    login: (email: string, password: string) => Promise<void>;
    logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);

    const login = async (email: string, password: string) => {
        try {
            const response = await api.post('/authenticateAccount', { email, password });
            console.log('AuthProvider login response.data: ', response.data);
            setUser(response.data.user); // SetState is async. But it doesn't return a Promise. Use useEffect to detect changes in user state
            
            // Save token if provided
        } catch (error) {
            console.error('Login failed', error);
            throw error;
        }
    };

    const logout = () => {
        setUser(null);
        // Remove token if stored
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
