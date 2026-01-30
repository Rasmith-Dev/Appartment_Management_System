import React, { useState, useEffect } from 'react';
import {
    Container,
    Typography,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    MenuItem,
    Box,
    IconButton,
    Alert,
    Snackbar,
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';
import { Tenant, Flat } from '../types';
import api from '../services/api';

const Tenants: React.FC = () => {
    const [tenants, setTenants] = useState<Tenant[]>([]);
    const [flats, setFlats] = useState<Flat[]>([]);
    const [users, setUsers] = useState<{ id: string; username: string; email: string }[]>([]);
    const [selectedTenant, setSelectedTenant] = useState<Tenant | null>(null);
    const [open, setOpen] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [formData, setFormData] = useState<{
        userId: string;
        flatId: string;
        leaseStart: string;
        leaseEnd: string;
        phone: string;
    }>({
        userId: '',
        flatId: '',
        leaseStart: '',
        leaseEnd: '',
        phone: ''
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            console.log('Fetching tenants and flats data...');
            const [tenantsResponse, flatsResponse, usersResponse] = await Promise.all([
                api.get('/tenants'),
                api.get('/flats'),
                api.get('/users')
            ]);
            console.log('Tenants response:', tenantsResponse.data);
            console.log('Flats response:', flatsResponse.data);
            setTenants(tenantsResponse.data);
            setFlats(flatsResponse.data);
            setUsers(usersResponse.data);
        } catch (error: any) {
            console.error('Error fetching data:', error);
            if (error.response) {
                console.error('Error response:', error.response.data);
                console.error('Error status:', error.response.status);
            }
            setError('Failed to fetch tenant data. Please try again.');
        }
    };

    const handleOpen = (tenant?: Tenant) => {
        if (tenant) {
            setSelectedTenant(tenant);
            setFormData({
                userId: tenant.user.id,
                flatId: tenant.flat.id,
                leaseStart: tenant.leaseStart,
                leaseEnd: tenant.leaseEnd,
                phone: tenant.phone
            });
        } else {
            setSelectedTenant(null);
            setFormData({
                userId: '',
                flatId: '',
                leaseStart: '',
                leaseEnd: '',
                phone: ''
            });
        }
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setSelectedTenant(null);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const tenantData = {
                userId: formData.userId,
                flatId: formData.flatId,
                leaseStart: formData.leaseStart,
                leaseEnd: formData.leaseEnd,
                phone: formData.phone
            };

            if (selectedTenant) {
                await api.put(`/tenants/${selectedTenant.id}`, tenantData);
            } else {
                await api.post('/tenants', tenantData);
            }
            handleClose();
            fetchData();
        } catch (error: any) {
            console.error('Error saving tenant:', error);
            const message = error?.response?.data || 'Failed to save tenant. Please try again.';
            setError(message);
        }
    };

    const handleDelete = async (id: string) => {
        if (window.confirm('Are you sure you want to delete this tenant?')) {
            try {
                await api.delete(`/tenants/${id}`);
                fetchData();
            } catch (error) {
                console.error('Error deleting tenant:', error);
                setError('Failed to delete tenant. Please try again.');
            }
        }
    };

    return (
        <Container>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" component="h1">
                    Tenants Management
                </Typography>
                <Button variant="contained" color="primary" onClick={() => handleOpen()}>
                    Add Tenant
                </Button>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>User</TableCell>
                            <TableCell>Flat</TableCell>
                            <TableCell>Lease Start</TableCell>
                            <TableCell>Lease End</TableCell>
                            <TableCell>Phone</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tenants.map((tenant) => (
                            <TableRow key={tenant.id}>
                                <TableCell>{tenant.user.username}</TableCell>
                                <TableCell>{tenant.flat.flatNumber}</TableCell>
                                <TableCell>{new Date(tenant.leaseStart).toLocaleDateString()}</TableCell>
                                <TableCell>{new Date(tenant.leaseEnd).toLocaleDateString()}</TableCell>
                                <TableCell>{tenant.phone}</TableCell>
                                <TableCell>
                                    <IconButton onClick={() => handleOpen(tenant)} color="primary">
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton onClick={() => handleDelete(tenant.id)} color="error">
                                        <DeleteIcon />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
                <DialogTitle>{selectedTenant ? 'Edit Tenant' : 'Add Tenant'}</DialogTitle>
                <form onSubmit={handleSubmit}>
                    <DialogContent>
                        <TextField
                            select
                            fullWidth
                            label="User"
                            value={formData.userId}
                            onChange={(e) => setFormData({ ...formData, userId: e.target.value })}
                            margin="normal"
                            required
                        >
                            {users.map((user) => (
                                <MenuItem key={user.id} value={user.id}>
                                    {user.username}
                                </MenuItem>
                            ))}
                        </TextField>
                        <TextField
                            select
                            fullWidth
                            label="Flat"
                            value={formData.flatId}
                            onChange={(e) => setFormData({ ...formData, flatId: e.target.value })}
                            margin="normal"
                            required
                        >
                            {flats.map((flat) => (
                                <MenuItem key={flat.id} value={flat.id}>
                                    {flat.flatNumber}
                                </MenuItem>
                            ))}
                        </TextField>
                        <TextField
                            fullWidth
                            label="Lease Start"
                            type="date"
                            value={formData.leaseStart}
                            onChange={(e) => setFormData({ ...formData, leaseStart: e.target.value })}
                            margin="normal"
                            required
                            InputLabelProps={{ shrink: true }}
                        />
                        <TextField
                            fullWidth
                            label="Lease End"
                            type="date"
                            value={formData.leaseEnd}
                            onChange={(e) => setFormData({ ...formData, leaseEnd: e.target.value })}
                            margin="normal"
                            required
                            InputLabelProps={{ shrink: true }}
                        />
                        <TextField
                            fullWidth
                            label="Phone"
                            value={formData.phone}
                            onChange={(e) => {
                                const digitsOnly = e.target.value.replace(/\D/g, '').slice(0, 10);
                                setFormData({ ...formData, phone: digitsOnly });
                            }}
                            margin="normal"
                            required
                            error={formData.phone.length > 0 && formData.phone.length < 10}
                            helperText={
                                formData.phone.length > 0 && formData.phone.length < 10
                                    ? 'Phone number should be 10 digits long'
                                    : 'Enter a 10-digit phone number'
                            }
                            inputProps={{ inputMode: 'numeric', maxLength: 10 }}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose}>Cancel</Button>
                        <Button type="submit" variant="contained" color="primary">
                            {selectedTenant ? 'Update' : 'Add'}
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>

            <Snackbar
                open={!!error}
                autoHideDuration={6000}
                onClose={() => setError(null)}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert onClose={() => setError(null)} severity="error" sx={{ width: '100%' }}>
                    {error}
                </Alert>
            </Snackbar>
        </Container>
    );
};

export default Tenants; 