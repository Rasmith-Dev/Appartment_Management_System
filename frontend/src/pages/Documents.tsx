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
import { Document, Tenant } from '../types';
import api from '../services/api';

const Documents: React.FC = () => {
  const [documents, setDocuments] = useState<Document[]>([]);
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [open, setOpen] = useState(false);
  const [selectedDocument, setSelectedDocument] = useState<Document | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    type: '',
    tenantId: '',
    file: null as File | null,
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [documentsResponse, tenantsResponse] = await Promise.all([
        api.get('/documents'),
        api.get('/tenants'),
      ]);
      setDocuments(documentsResponse.data);
      setTenants(tenantsResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const handleOpen = (document?: Document) => {
    if (document) {
      setSelectedDocument(document);
      setFormData({
        title: document.title,
        description: document.description,
        type: document.type,
        tenantId: document.tenantId.toString(),
        file: null,
      });
    } else {
      setSelectedDocument(null);
      setFormData({
        title: '',
        description: '',
        type: '',
        tenantId: '',
        file: null,
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedDocument(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const formDataToSend = new FormData();
      formDataToSend.append('title', formData.title);
      formDataToSend.append('description', formData.description);
      formDataToSend.append('type', formData.type);
      formDataToSend.append('tenantId', formData.tenantId);
      if (formData.file) {
        formDataToSend.append('file', formData.file);
      }

      if (selectedDocument) {
        await api.put(`/documents/${selectedDocument.id}`, formDataToSend, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
      } else {
        await api.post('/documents', formDataToSend, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
      }
      fetchData();
      handleClose();
    } catch (error) {
      console.error('Error saving document:', error);
    }
  };

  const getTenantInfo = (tenantId: string) => {
    const tenant = tenants.find((t) => t.id === tenantId);
    return tenant ? tenant.user.username : 'N/A';
  };

  const handleDownload = async (id: string) => {
    try {
      const response = await api.get(`/documents/${id}/download`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'document.pdf');
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error downloading document:', error);
    }
  };

  return (
    <Container>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">Documents</Typography>
        <Button variant="contained" color="primary" onClick={() => handleOpen()}>
          Add Document
        </Button>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Title</TableCell>
              <TableCell>Description</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Tenant</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {documents.map((document) => (
              <TableRow key={document.id}>
                <TableCell>{document.title}</TableCell>
                <TableCell>{document.description}</TableCell>
                <TableCell>{document.type}</TableCell>
                <TableCell>{getTenantInfo(document.tenantId)}</TableCell>
                <TableCell>
                  <Button
                    size="small"
                    onClick={() => handleOpen(document)}
                    sx={{ mr: 1 }}
                  >
                    Edit
                  </Button>
                  <Button
                    size="small"
                    onClick={() => handleDownload(document.id)}
                    sx={{ mr: 1 }}
                  >
                    Download
                  </Button>
                  <Button
                    size="small"
                    color="error"
                    onClick={async () => {
                      if (window.confirm('Are you sure you want to delete this document?')) {
                        try {
                          await api.delete(`/documents/${document.id}`);
                          fetchData();
                        } catch (error) {
                          console.error('Error deleting document:', error);
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
          {selectedDocument ? 'Edit Document' : 'Add Document'}
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
              name="type"
              label="Type"
              fullWidth
              value={formData.type}
              onChange={(e) => setFormData({ ...formData, type: e.target.value })}
              required
            >
              <MenuItem value="LEASE">Lease Agreement</MenuItem>
              <MenuItem value="ID_PROOF">ID Proof</MenuItem>
              <MenuItem value="INVOICE">Invoice</MenuItem>
              <MenuItem value="OTHER">Other</MenuItem>
            </TextField>
            <TextField
              margin="dense"
              name="file"
              label="File"
              type="file"
              fullWidth
              onChange={(e) => {
                const file = (e.target as HTMLInputElement).files?.[0] || null;
                setFormData({ ...formData, file });
              }}
              required={!selectedDocument}
              InputLabelProps={{ shrink: true }}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleClose}>Cancel</Button>
            <Button type="submit" variant="contained" color="primary">
              {selectedDocument ? 'Update' : 'Add'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </Container>
  );
};

export default Documents; 