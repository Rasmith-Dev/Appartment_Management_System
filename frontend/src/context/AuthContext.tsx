import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { login as apiLogin, register as apiRegister } from '../services/api';
import api from '../services/api';

interface AuthContextType {
    user: User | null;
    login: (email: string, password: string) => Promise<void>;
    register: (username: string, email: string, password: string) => Promise<void>;
    logout: () => void;
    isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const validateToken = async () => {
            const token = localStorage.getItem('token');
            const storedUser = localStorage.getItem('user');
            
            if (!token || !storedUser) {
                setUser(null);
                setIsAuthenticated(false);
                return;
            }

            try {
                // Try to make a request to validate the token
                await api.get('/auth/validate');
                setUser(JSON.parse(storedUser));
                setIsAuthenticated(true);
            } catch (error) {
                console.error('Token validation failed:', error);
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                setUser(null);
                setIsAuthenticated(false);
            }
        };

        validateToken();
    }, []);

    const login = async (email: string, password: string) => {
        try {
            const response = await apiLogin(email, password);
            localStorage.setItem('token', response.token);
            const userData: User = {
                email: response.email,
                username: response.email.split('@')[0], // Using email prefix as username
                role: response.role as 'OWNER' | 'MANAGER' | 'TENANT'
            };
            localStorage.setItem('user', JSON.stringify(userData));
            setUser(userData);
            setIsAuthenticated(true);
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    };

    const register = async (username: string, email: string, password: string) => {
        try {
            console.log('Starting registration process...');
            const response = await apiRegister(username, email, password);
            console.log('Registration response:', response);
            
            if (!response.token) {
                console.error('No token in registration response');
                throw new Error('Invalid registration response');
            }
            
            localStorage.setItem('token', response.token);
            const userData: User = {
                email: response.email,
                username: username, // Use the provided username instead of email prefix
                role: response.role as 'OWNER' | 'MANAGER' | 'TENANT'
            };
            localStorage.setItem('user', JSON.stringify(userData));
            setUser(userData);
            setIsAuthenticated(true);
            console.log('Registration completed successfully');
        } catch (error: any) {
            console.error('Registration failed:', error);
            if (error.response) {
                console.error('Error response:', error.response.data);
            }
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, isAuthenticated }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}; 