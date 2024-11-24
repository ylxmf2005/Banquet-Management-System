// src/admin/page.tsx
'use client';

import React, { useEffect, useContext, useState } from 'react';
import { AuthContext } from '../../context/AuthContext';
import { useRouter } from 'next/navigation';
import {
    AppBar,
    Tabs,
    Tab,
    Container,
} from '@mui/material';
import BanquetManagement from '../../components/BanquetManagement';
import AttendeeManagement from '../../components/AttendeeManagement';

export default function AdminPage() {
    const auth = useContext(AuthContext);
    const router = useRouter();

    // Redirect non-admin users
    useEffect(() => {
        if (auth?.user?.role !== 'admin') {
            router.push('/login');
        }
    }, [auth, router]);

    const [tabIndex, setTabIndex] = useState(0);

    // Handle tab change
    const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
        setTabIndex(newValue);
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            {/* AppBar with Tabs for navigation */}
            <AppBar position="static" color="default">
                <Tabs
                    value={tabIndex}
                    onChange={handleTabChange}
                    variant="fullWidth"
                    indicatorColor="primary"
                    textColor="primary"
                >
                    <Tab label="Banquet Management" />
                    <Tab label="Attendee Management" />
                </Tabs>
            </AppBar>
            {/* Render components based on the selected tab */}
            {tabIndex === 0 && <BanquetManagement />}
            {tabIndex === 1 && <AttendeeManagement />}
        </Container>
    );
}