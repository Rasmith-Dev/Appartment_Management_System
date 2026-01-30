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
} from '@mui/material';
import { Complaint, Tenant } from '../types';
import api from '../services/api';

const Complaints: React.FC = () => {
  const [complaints, setComplaints] = useState<Complaint[]>([]);
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [open, setOpen] = useState(false);
  const [selectedComplaint, setSelectedComplaint] = useState<Complaint | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    status: 'OPEN',
    tenantId: '',
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [complaintsResponse, tenantsResponse] = await Promise.all([
        api.get('/complaints'),
        api.get('/tenants'),
      ]);
      setComplaints(complaintsResponse.data);
      setTenants(tenantsResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const handleOpen = (complaint?: Complaint) => {
    if (complaint) {
      setSelectedComplaint(complaint);
      setFormData({
        title: complaint.title,
        description: complaint.description,
        status: complaint.status,
        tenantId: complaint.tenantId.toString(),
      });
    } else {
      setSelectedComplaint(null);
      setFormData({
        title: '',
        description: '',
        status: 'OPEN',
        tenantId: '',
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedComplaint(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const complaintData = {
        ...formData,
        tenantId: parseInt(formData.tenantId),
      };

      if (selectedComplaint) {
        await api.put(`/complaints/${selectedComplaint.id}`, complaintData);
      } else {
        await api.post('/complaints', complaintData);
      }
      fetchData();
      handleClose();
    } catch (error) {
      console.error('Error saving complaint:', error);
    }
  };

  const getTenantInfo = (tenantId: string) => {
    const tenant = tenants.find((t) => t.id === tenantId);
    return tenant ? tenant.user.username : 'N/A';
  };

  return (
    <Container>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Complaints</Typography>
        <Button variant="contained" color="primary" onClick={() => handleOpen()}>
          Add Complaint
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Title</TableCell>
              <TableCell>Description</TableCell>
              <TableCell>Tenant</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {complaints.map((complaint) => (
              <TableRow key={complaint.id}>
                <TableCell>{complaint.title}</TableCell>
                <TableCell>{complaint.description}</TableCell>
                <TableCell>{getTenantInfo(complaint.tenantId)}</TableCell>
                <TableCell>{complaint.status}</TableCell>
                <TableCell>
                  <Button
                    size="small"
                    onClick={() => handleOpen(complaint)}
                    sx={{ mr: 1 }}
                  >
                    Edit
                  </Button>
                  <Button
                    size="small"
                    color="error"
                    onClick={async () => {
                      if (window.confirm('Are you sure you want to delete this complaint?')) {
                        try {
                          await api.delete(`/complaints/${complaint.id}`);
                          fetchData();
                        } catch (error) {
                          console.error('Error deleting complaint:', error);
                        }
                      }
                    }}
                  >
                    Delete
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>
          {selectedComplaint ? 'Edit Complaint' : 'Add Complaint'}
        </DialogTitle>
        <form onSubmit={handleSubmit}>
          <DialogContent>
            <TextField
              select
              margin="dense"
              name="tenantId"
              label="Tenant"
              fullWidth
              value={formData.tenantId}
              onChange={(e) => setFormData({ ...formData, tenantId: e.target.value })}
              required
            >
              {tenants.map((tenant) => (
                <MenuItem key={tenant.id} value={tenant.id}>
                  {tenant.user.username}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              margin="dense"
              name="title"
              label="Title"
              fullWidth
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              required
            />
            <TextField
              margin="dense"
              name="description"
              label="Description"
              fullWidth
              multiline
              rows={4}
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              required
            />
            <TextField
              select
              margin="dense"
              name="status"
              label="Status"
              fullWidth
              value={formData.status}
              onChange={(e) => setFormData({ ...formData, status: e.target.value })}
              required
            >
              <MenuItem value="OPEN">Open</MenuItem>
              <MenuItem value="IN_PROGRESS">In Progress</MenuItem>
              <MenuItem value="RESOLVED">Resolved</MenuItem>
              <MenuItem value="CLOSED">Closed</MenuItem>
            </TextField>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancel</Button>
            <Button type="submit" variant="contained" color="primary">
              {selectedComplaint ? 'Update' : 'Add'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Container>
  );
};

export default Complaints; 