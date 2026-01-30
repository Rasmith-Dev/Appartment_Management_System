import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
    Box,
    Drawer,
    AppBar,
    Toolbar,
    List,
    Typography,
    Divider,
    IconButton,
    ListItem,
    ListItemIcon,
    ListItemText,
    Button,
    Container,
} from '@mui/material';
import {
    Menu as MenuIcon,
    Dashboard as DashboardIcon,
    Home as HomeIcon,
    People as PeopleIcon,
    Payment as PaymentIcon,
    ReportProblem as ReportProblemIcon,
    Description as DescriptionIcon,
    Logout as LogoutIcon,
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';

const drawerWidth = 240;

const Dashboard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [mobileOpen, setMobileOpen] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();
    const { logout } = useAuth();

    console.log('Dashboard render - Current path:', location.pathname);
    console.log('Dashboard render - Children:', children);

    const menuItems = [
        { text: 'Dashboard', icon: <DashboardIcon />, path: '/' },
        { text: 'Flats', icon: <HomeIcon />, path: '/flats' },
        { text: 'Tenants', icon: <PeopleIcon />, path: '/tenants' },
        { text: 'Payments', icon: <PaymentIcon />, path: '/payments' },
        { text: 'Complaints', icon: <ReportProblemIcon />, path: '/complaints' },
        { text: 'Documents', icon: <DescriptionIcon />, path: '/documents' },
    ];

    const handleDrawerToggle = () => {
        setMobileOpen(!mobileOpen);
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const drawer = (
        <div>
            <Toolbar>
                <Typography variant="h6" noWrap component="div">
                    Apartment Management
                </Typography>
            </Toolbar>
            <Divider />
            <List>
                {menuItems.map((item) => (
                    <ListItem
                        key={item.text}
                        onClick={() => {
                            navigate(item.path);
                            setMobileOpen(false);
                        }}
                        sx={{
                            cursor: 'pointer',
                            backgroundColor: location.pathname === item.path ? 'rgba(0, 0, 0, 0.04)' : 'transparent',
                        }}
                    >
                        <ListItemIcon>{item.icon}</ListItemIcon>
                        <ListItemText primary={item.text} />
                    </ListItem>
                ))}
            </List>
            <Divider />
            <List>
                <ListItem onClick={handleLogout} sx={{ cursor: 'pointer' }}>
                    <ListItemIcon>
                        <LogoutIcon />
                    </ListItemIcon>
                    <ListItemText primary="Logout" />
                </ListItem>
            </List>
        </div>
    );

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh' }}>
            <AppBar
                position="fixed"
                sx={{
                    width: { sm: `calc(100% - ${drawerWidth}px)` },
                    ml: { sm: `${drawerWidth}px` },
                    zIndex: (theme) => theme.zIndex.drawer + 1,
                }}
            >
                <Toolbar>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        edge="start"
                        onClick={handleDrawerToggle}
                        sx={{ mr: 2, display: { sm: 'none' } }}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
                        {menuItems.find((item) => item.path === location.pathname)?.text || 'Dashboard'}
                    </Typography>
                </Toolbar>
            </AppBar>
            <Box
                component="nav"
                sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
            >
                <Drawer
                    variant="temporary"
                    open={mobileOpen}
                    onClose={handleDrawerToggle}
                    ModalProps={{
                        keepMounted: true,
                    }}
                    sx={{
                        display: { xs: 'block', sm: 'none' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                    }}
                >
                    {drawer}
                </Drawer>
                <Drawer
                    variant="permanent"
                    sx={{
                        display: { xs: 'none', sm: 'block' },
                        '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
                    }}
                    open
                >
                    {drawer}
                </Drawer>
            </Box>
            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    p: 3,
                    width: { sm: `calc(100% - ${drawerWidth}px)` },
                    mt: '64px',
                    backgroundColor: '#f5f5f5',
                    minHeight: 'calc(100vh - 64px)',
                }}
            >
                <Container maxWidth="lg">
                    {location.pathname === '/' ? (
                        <Box sx={{ mb: 4 }}>
                            <Typography variant="h4" component="h1" gutterBottom>
                                Welcome to Apartment Management System
                            </Typography>
                            <Typography variant="body1" paragraph>
                                Manage your flats, tenants, payments, and more from this dashboard.
                            </Typography>
                            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                                <Button
                                    variant="contained"
                                    startIcon={<HomeIcon />}
                                    onClick={() => navigate('/flats')}
                                >
                                    Add Flat
                                </Button>
                                <Button
                                    variant="contained"
                                    startIcon={<PeopleIcon />}
                                    onClick={() => navigate('/tenants')}
                                >
                                    Add Tenant
                                </Button>
                                <Button
                                    variant="contained"
                                    startIcon={<PaymentIcon />}
                                    onClick={() => navigate('/payments')}
                                >
                                    Record Payment
                                </Button>
                            </Box>
                        </Box>
                    ) : (
                        <Box sx={{ backgroundColor: 'white', p: 3, borderRadius: 1, boxShadow: 1 }}>
                            {children}
                        </Box>
                    )}
                </Container>
            </Box>
        </Box>
    );
};

export default Dashboard; 