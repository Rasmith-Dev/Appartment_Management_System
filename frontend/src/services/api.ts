import axios from 'axios';
import { AuthResponse } from '../types';
import { User, Apartment, Tenant, Payment, Complaint, Document } from '../types';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add request interceptor to add auth token
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        console.error('Request error:', error);
        return Promise.reject(error);
    }
);

// Add response interceptor to handle errors
api.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error('API Error:', error.response?.data || error.message);
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

// Auth services
export const login = async (email: string, password: string): Promise<AuthResponse> => {
    try {
        console.log('Attempting login for:', email);
        const response = await api.post<AuthResponse>('/auth/signin', { email, password });
        console.log('Login successful:', response.data);
        return response.data;
    } catch (error) {
        console.error('Login failed:', error);
        throw error;
    }
};

export const register = async (username: string, email: string, password: string): Promise<AuthResponse> => {
    try {
        console.log('Attempting registration for:', email);
        const response = await api.post<AuthResponse>('/auth/register', { username, email, password });
        console.log('Registration successful:', response.data);
        return response.data;
    } catch (error) {
        console.error('Registration failed:', error);
        throw error;
    }
};

// Apartment services
export const getApartments = async (): Promise<Apartment[]> => {
    const response = await api.get<Apartment[]>('/apartments');
    return response.data;
};

export const getApartment = async (id: number): Promise<Apartment> => {
    const response = await api.get<Apartment>(`/apartments/${id}`);
    return response.data;
};

// Tenant services
export const getTenants = async (): Promise<Tenant[]> => {
    const response = await api.get<Tenant[]>('/tenants');
    return response.data;
};

export const getTenant = async (id: number): Promise<Tenant> => {
    const response = await api.get<Tenant>(`/tenants/${id}`);
    return response.data;
};

// Payment services
export const getPayments = async (): Promise<Payment[]> => {
    const response = await api.get<Payment[]>('/payments');
    return response.data;
};

export const createPayment = async (payment: Partial<Payment>): Promise<Payment> => {
    const response = await api.post<Payment>('/payments', payment);
    return response.data;
};

// Complaint services
export const getComplaints = async (): Promise<Complaint[]> => {
    const response = await api.get<Complaint[]>('/complaints');
    return response.data;
};

export const createComplaint = async (complaint: Partial<Complaint>): Promise<Complaint> => {
    const response = await api.post<Complaint>('/complaints', complaint);
    return response.data;
};

// Document services
export const getDocuments = async (): Promise<Document[]> => {
    const response = await api.get<Document[]>('/documents');
    return response.data;
};

export const uploadDocument = async (file: File, tenantId: number): Promise<Document> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('tenantId', tenantId.toString());
    const response = await api.post<Document>('/documents/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
    return response.data;
};

export default api; 