import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet, useLocation } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { AuthProvider } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './components/Dashboard';
import Flats from './pages/Flats';
import Tenants from './pages/Tenants';
import Payments from './pages/Payments';
import Complaints from './pages/Complaints';
import Documents from './pages/Documents';
import PrivateRoute from './components/PrivateRoute';

const theme = createTheme({
    palette: {
        mode: 'light',
        primary: {
            main: '#1976d2',
        },
        secondary: {
            main: '#dc004e',
        },
    },
});

const DashboardLayout = () => {
    const location = useLocation();
    console.log('DashboardLayout render - Current path:', location.pathname);
    
    return (
        <Dashboard>
            <Outlet />
        </Dashboard>
    );
};

function App() {
    console.log('App render');
    
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <AuthProvider>
                <Router>
                    <Routes>
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route element={<PrivateRoute><DashboardLayout /></PrivateRoute>}>
                            <Route index element={<Navigate to="/flats" replace />} />
                            <Route path="flats" element={<Flats />} />
                            <Route path="tenants" element={<Tenants />} />
                            <Route path="payments" element={<Payments />} />
                            <Route path="complaints" element={<Complaints />} />
                            <Route path="documents" element={<Documents />} />
                        </Route>
                    </Routes>
                </Router>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;
