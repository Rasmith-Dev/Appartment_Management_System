import React, { useState, useEffect } from 'react';
import {
  Box,
  Container,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tabs,
  Tab,
  CircularProgress,
  Alert,
} from '@mui/material';
import axios from 'axios';

interface TableData {
  [key: string]: any[];
}

const AdminDashboard: React.FC = () => {
  const [selectedTab, setSelectedTab] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tableData, setTableData] = useState<TableData>({});

  const tables = [
    'users',
    'apartments',
    'flats',
    'tenants',
    'complaints',
    'payments',
    'documents'
  ];

  useEffect(() => {
    const fetchTableData = async () => {
      try {
        setLoading(true);
        setError(null);
        const token = localStorage.getItem('token');
        
        if (!token) {
          setError('Not authenticated');
          return;
        }

        const response = await axios.get('http://localhost:8080/api/admin/tables', {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        setTableData(response.data);
      } catch (err) {
        setError('Failed to fetch table data');
        console.error('Error fetching table data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchTableData();
  }, []);

  const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
    setSelectedTab(newValue);
  };

  const renderTable = (tableName: string) => {
    const data = tableData[tableName] || [];
    
    if (data.length === 0) {
      return <Typography>No data available</Typography>;
    }

    const columns = Object.keys(data[0]);

    return (
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell key={column}>{column}</TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {data.map((row, index) => (
              <TableRow key={index}>
                {columns.map((column) => (
                  <TableCell key={`${index}-${column}`}>
                    {typeof row[column] === 'object' 
                      ? JSON.stringify(row[column])
                      : String(row[column])}
                  </TableCell>
                ))}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    );
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="100vh">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Container>
        <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>
      </Container>
    );
  }

  return (
    <Container>
      <Box sx={{ mt: 4, mb: 4 }}>
        <Typography variant="h4" gutterBottom>
          Admin Dashboard
        </Typography>
        
        <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 2 }}>
          <Tabs value={selectedTab} onChange={handleTabChange}>
            {tables.map((table, index) => (
              <Tab key={table} label={table} />
            ))}
          </Tabs>
        </Box>

        {renderTable(tables[selectedTab])}
      </Box>
    </Container>
  );
};

export default AdminDashboard; 