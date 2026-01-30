import React, { useEffect, useState } from 'react';
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Container,
  IconButton,
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon, CheckCircle as CheckCircleIcon } from '@mui/icons-material';
import { Payment, Tenant } from '../types';
import api from '../services/api';

const Payments: React.FC = () => {
  const [payments, setPayments] = useState<Payment[]>([]);
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [open, setOpen] = useState(false);
  const [selectedPayment, setSelectedPayment] = useState<Payment | null>(null);
  const [formData, setFormData] = useState<{
    tenantId: string;
    flatId: string;
    amount: string;
    type: 'RENT' | 'DEPOSIT' | 'MAINTENANCE' | 'OTHER';
    status: 'PENDING' | 'COMPLETED' | 'OVERDUE';
    dueDate: string;
    description: string;
  }>({
    tenantId: '',
    flatId: '',
    amount: '',
    type: 'RENT',
    status: 'PENDING',
    dueDate: '',
    description: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [paymentsResponse, tenantsResponse] = await Promise.all([
        api.get('/payments'),
        api.get('/tenants'),
      ]);
      setPayments(paymentsResponse.data);
      setTenants(tenantsResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const handleOpen = (payment?: Payment) => {
    if (payment) {
      setSelectedPayment(payment);
      setFormData({
        tenantId: payment.tenantId,
        flatId: payment.flatId,
        amount: payment.amount.toString(),
        type: payment.type,
        status: payment.status,
        dueDate: payment.dueDate,
        description: payment.description || '',
      });
    } else {
      setSelectedPayment(null);
      setFormData({
        tenantId: '',
        flatId: '',
        amount: '',
        type: 'RENT',
        status: 'PENDING',
        dueDate: '',
        description: '',
      });
    }
    setOpen(true);
  };

  const handleTenantChange = (tenantId: string) => {
    const tenant = tenants.find((t) => t.id === tenantId);
    setFormData({
      ...formData,
      tenantId,
      flatId: tenant?.flat?.id || '',
    });
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedPayment(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const paymentData = {
        ...formData,
        amount: parseFloat(formData.amount),
      };

      if (selectedPayment) {
        await api.put(`/payments/${selectedPayment.id}`, paymentData);
      } else {
        await api.post('/payments', paymentData);
      }
      handleClose();
      fetchData();
    } catch (error) {
      console.error('Error saving payment:', error);
    }
  };

  const handleDelete = async (id: string) => {
    if (window.confirm('Are you sure you want to delete this payment?')) {
      try {
        await api.delete(`/payments/${id}`);
        fetchData();
      } catch (error) {
        console.error('Error deleting payment:', error);
      }
    }
  };

  const handleMarkAsPaid = async (id: string) => {
    try {
      await api.put(`/payments/${id}/mark-paid`);
      fetchData();
    } catch (error) {
      console.error('Error marking payment as paid:', error);
    }
  };

  const getTenantInfo = (tenantId: string) => {
    const tenant = tenants.find((t) => t.id === tenantId);
    return tenant ? tenant.user.username : 'N/A';
  };

  return (
    <Container>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Payments Management
        </Typography>
        <Button variant="contained" color="primary" onClick={() => handleOpen()}>
          Add Payment
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Tenant</TableCell>
              <TableCell>Amount</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Due Date</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {payments.map((payment) => (
              <TableRow key={payment.id}>
                <TableCell>{getTenantInfo(payment.tenantId)}</TableCell>
                <TableCell>₹{payment.amount}</TableCell>
                <TableCell>{payment.type}</TableCell>
                <TableCell>{payment.status}</TableCell>
                <TableCell>{new Date(payment.dueDate).toLocaleDateString()}</TableCell>
                <TableCell>
                  <IconButton onClick={() => handleOpen(payment)} color="primary">
                    <EditIcon />
                  </IconButton>
                  {payment.status === 'PENDING' && (
                    <IconButton onClick={() => handleMarkAsPaid(payment.id)} color="success">
                      <CheckCircleIcon />
                    </IconButton>
                  )}
                  <IconButton onClick={() => handleDelete(payment.id)} color="error">
                    <DeleteIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
        <DialogTitle>{selectedPayment ? 'Edit Payment' : 'Add Payment'}</DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent>
            <TextField
              select
              fullWidth
              label="Tenant"
              value={formData.tenantId}
              onChange={(e) => handleTenantChange(e.target.value)}
              margin="normal"
              required
            >
              {tenants.map((tenant) => (
                <MenuItem key={tenant.id} value={tenant.id}>
                  {tenant.user.username}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              fullWidth
              label="Amount"
              type="number"
              value={formData.amount}
              onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
              margin="normal"
              required
            />
            <TextField
              select
              fullWidth
              label="Type"
              value={formData.type}
              onChange={(e) => setFormData({ ...formData, type: e.target.value as 'RENT' | 'DEPOSIT' | 'MAINTENANCE' | 'OTHER' })}
              margin="normal"
              required
            >
              <MenuItem value="RENT">Rent</MenuItem>
              <MenuItem value="DEPOSIT">Deposit</MenuItem>
              <MenuItem value="MAINTENANCE">Maintenance</MenuItem>
              <MenuItem value="OTHER">Other</MenuItem>
            </TextField>
            <TextField
              select
              fullWidth
              label="Status"
              value={formData.status}
              onChange={(e) => setFormData({ ...formData, status: e.target.value as 'PENDING' | 'COMPLETED' | 'OVERDUE' })}
              margin="normal"
              required
            >
              <MenuItem value="PENDING">Pending</MenuItem>
              <MenuItem value="COMPLETED">Completed</MenuItem>
              <MenuItem value="OVERDUE">Overdue</MenuItem>
            </TextField>
            <TextField
              fullWidth
              label="Due Date"
              type="date"
              value={formData.dueDate}
              onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
              margin="normal"
              required
              InputLabelProps={{ shrink: true }}
            />
            <TextField
              fullWidth
              label="Description"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              margin="normal"
              multiline
              rows={3}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancel</Button>
            <Button type="submit" variant="contained" color="primary">
              {selectedPayment ? 'Update' : 'Add'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Container>
  );
};

export default Payments; 