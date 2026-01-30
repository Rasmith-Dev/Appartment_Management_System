import React, { useEffect, useState } from 'react';
import {
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
} from '@mui/material';
import {
  Home as HomeIcon,
  People as PeopleIcon,
  Payment as PaymentIcon,
  Report as ReportIcon,
} from '@mui/icons-material';
import Layout from '../components/Layout';
import { getApartments, getTenants, getPayments, getComplaints } from '../services/api';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState({
    apartments: 0,
    tenants: 0,
    payments: 0,
    complaints: 0,
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [apartments, tenants, payments, complaints] = await Promise.all([
          getApartments(),
          getTenants(),
          getPayments(),
          getComplaints(),
        ]);

        setStats({
          apartments: apartments.length,
          tenants: tenants.length,
          payments: payments.length,
          complaints: complaints.length,
        });
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      }
    };

    fetchData();
  }, []);

  const StatCard: React.FC<{
    title: string;
    value: number;
    icon: React.ReactNode;
  }> = ({ title, value, icon }) => (
    <Card sx={{ width: '100%' }}>
      <CardContent>
        <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
          {icon}
          <Typography variant="h6" component="div" sx={{ ml: 1 }}>
            {title}
          </Typography>
        </Box>
        <Typography variant="h4" component="div">
          {value}
        </Typography>
      </CardContent>
    </Card>
  );

  return (
    <Layout>
      <Box sx={{ flexGrow: 1 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          Dashboard
        </Typography>
        <Grid container spacing={3}>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Apartments"
              value={stats.apartments}
              icon={<HomeIcon color="primary" />}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Tenants"
              value={stats.tenants}
              icon={<PeopleIcon color="primary" />}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Payments"
              value={stats.payments}
              icon={<PaymentIcon color="primary" />}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              title="Complaints"
              value={stats.complaints}
              icon={<ReportIcon color="primary" />}
            />
          </Grid>
        </Grid>
      </Box>
    </Layout>
  );
};

export default Dashboard; 