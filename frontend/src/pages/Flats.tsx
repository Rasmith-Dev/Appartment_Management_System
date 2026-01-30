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
import { Flat } from '../types';
import api from '../services/api';

const Flats: React.FC = () => {
    const [flats, setFlats] = useState<Flat[]>([]);
    const [selectedFlat, setSelectedFlat] = useState<Flat | null>(null);
    const [open, setOpen] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [formData, setFormData] = useState<{
        flatNumber: string;
        floor: string;
        area: string;
        rent: string;
        type: 'ONE_BHK' | 'TWO_BHK' | 'THREE_BHK' | 'FOUR_BHK';
        status: 'VACANT' | 'OCCUPIED' | 'MAINTENANCE';
    }>({
        flatNumber: '',
        floor: '',
        area: '',
        rent: '',
        type: 'ONE_BHK',
        status: 'VACANT'
    });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        try {
            const response = await api.get<Flat[]>('/flats');
            setFlats(response.data);
        } catch (error) {
            console.error('Error fetching data:', error);
            setError('Failed to fetch data. Please try again later.');
        }
    };

    const handleOpen = (flat?: Flat) => {
        if (flat) {
            setSelectedFlat(flat);
            setFormData({
                flatNumber: flat.flatNumber,
                floor: flat.floor.toString(),
                area: flat.area.toString(),
                rent: flat.rent.toString(),
                type: flat.type as 'ONE_BHK' | 'TWO_BHK' | 'THREE_BHK' | 'FOUR_BHK',
                status: flat.status
            });
        } else {
            setSelectedFlat(null);
            setFormData({
                flatNumber: '',
                floor: '',
                area: '',
                rent: '',
                type: 'ONE_BHK',
                status: 'VACANT'
            });
        }
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setSelectedFlat(null);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const flatData = {
                ...formData,
                floor: parseInt(formData.floor),
                area: parseFloat(formData.area),
                rent: parseFloat(formData.rent)
            };

            if (selectedFlat) {
                await api.put<Flat>(`/flats/${selectedFlat.id}`, flatData);
            } else {
                await api.post<Flat>('/flats', flatData);
            }
            handleClose();
            fetchData();
        } catch (error) {
            console.error('Error saving flat:', error);
            setError('Failed to save flat. Please try again.');
        }
    };

    const handleDelete = async (id: string) => {
        if (window.confirm('Are you sure you want to delete this flat?')) {
            try {
                await api.delete(`/flats/${id}`);
                fetchData();
            } catch (error: any) {
                console.error('Error deleting flat:', error);
                if (error.response?.status === 409) {
                    setError('Cannot delete flat because it has related records.');
                } else {
                    setError('Failed to delete flat. Please try again.');
                }
            }
        }
    };

    return (
        <Container>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" component="h1">
                    Flats Management
                </Typography>
                <Button variant="contained" color="primary" onClick={() => handleOpen()}>
                    Add Flat
                </Button>
            </Box>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Flat Number</TableCell>
                            <TableCell>Floor</TableCell>
                            <TableCell>Area (sq ft)</TableCell>
                            <TableCell>Rent</TableCell>
                            <TableCell>Type</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {flats.map((flat) => (
                            <TableRow key={flat.id}>
                                <TableCell>{flat.flatNumber}</TableCell>
                                <TableCell>{flat.floor}</TableCell>
                                <TableCell>{flat.area}</TableCell>
                                <TableCell>₹{flat.rent}</TableCell>
                                <TableCell>{flat.type}</TableCell>
                                <TableCell>{flat.status}</TableCell>
                                <TableCell>
                                    <IconButton onClick={() => handleOpen(flat)} color="primary">
                                        <EditIcon />
                                    </IconButton>
                                    <IconButton onClick={() => handleDelete(flat.id)} color="error">
                                        <DeleteIcon />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
                <DialogTitle>{selectedFlat ? 'Edit Flat' : 'Add Flat'}</DialogTitle>
                <form onSubmit={handleSubmit}>
                    <DialogContent>
                        <TextField
                            fullWidth
                            label="Flat Number"
                            value={formData.flatNumber}
                            onChange={(e) => setFormData({ ...formData, flatNumber: e.target.value })}
                            margin="normal"
                            required
                            disabled={!!selectedFlat}
                        />
                        <TextField
                            fullWidth
                            label="Floor"
                            type="number"
                            value={formData.floor}
                            onChange={(e) => setFormData({ ...formData, floor: e.target.value })}
                            margin="normal"
                            required
                            disabled={!!selectedFlat}
                        />
                        <TextField
                            fullWidth
                            label="Area (sq ft)"
                            type="number"
                            value={formData.area}
                            onChange={(e) => setFormData({ ...formData, area: e.target.value })}
                            margin="normal"
                            required
                            disabled={!!selectedFlat}
                        />
                        <TextField
                            fullWidth
                            label="Rent"
                            type="number"
                            value={formData.rent}
                            onChange={(e) => setFormData({ ...formData, rent: e.target.value })}
                            margin="normal"
                            required
                        />
                        <TextField
                            select
                            fullWidth
                            label="Type"
                            value={formData.type}
                            onChange={(e) => setFormData({ ...formData, type: e.target.value as 'ONE_BHK' | 'TWO_BHK' | 'THREE_BHK' | 'FOUR_BHK' })}
                            margin="normal"
                            required
                            disabled={!!selectedFlat}
                        >
                            <MenuItem value="ONE_BHK">1 BHK</MenuItem>
                            <MenuItem value="TWO_BHK">2 BHK</MenuItem>
                            <MenuItem value="THREE_BHK">3 BHK</MenuItem>
                            <MenuItem value="FOUR_BHK">4 BHK</MenuItem>
                        </TextField>
                        <TextField
                            select
                            fullWidth
                            label="Status"
                            value={formData.status}
                            onChange={(e) => setFormData({ ...formData, status: e.target.value as 'VACANT' | 'OCCUPIED' | 'MAINTENANCE' })}
                            margin="normal"
                            required
                        >
                            <MenuItem value="VACANT">Vacant</MenuItem>
                            <MenuItem value="OCCUPIED">Occupied</MenuItem>
                            <MenuItem value="MAINTENANCE">Maintenance</MenuItem>
                        </TextField>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={handleClose}>Cancel</Button>
                        <Button type="submit" variant="contained" color="primary">
                            {selectedFlat ? 'Update' : 'Add'}
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

export default Flats; 