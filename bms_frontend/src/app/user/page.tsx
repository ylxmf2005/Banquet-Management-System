// src/app/user/page.tsx

'use client';

import React, { useContext, useEffect, useState } from 'react';
import { AuthContext } from '../../context/AuthContext';
import { SnackbarContext } from '../../context/SnackbarContext';
import { useRouter } from 'next/navigation';
import {
    AppBar,
    Tabs,
    Tab,
    Container,
} from '@mui/material';
import ProfileTab from '../../components/ProfileTab';
import AvailableBanquetsTab from '../../components/AvailableBanquetsTab';
import MyRegistrationsTab from '../../components/MyRegistrationsTab';

const UserPage: React.FC = () => {
    const { user } = useContext(AuthContext)!;
    const { showMessage } = useContext(SnackbarContext);
    const router = useRouter();
    const [tabIndex, setTabIndex] = useState(0);

    useEffect(() => {
        if (!user) {
            router.push('/login'); // Redirect to login if not authenticated
        }
    }, [user, router]);

    const handleTabChange = (event: React.SyntheticEvent, newValue: number) => {
        setTabIndex(newValue);
    };

    if (!user) {
        return null; // Or a loading indicator
    }

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
                    <Tab label="Profile" />
                    <Tab label="Available Banquets" />
                    <Tab label="My Registrations" />
                </Tabs>
            </AppBar>
            {/* Render components based on the selected tab */}
            {tabIndex === 0 && <ProfileTab user={user} showMessage={showMessage} />}
            {tabIndex === 1 && <AvailableBanquetsTab user={user} showMessage={showMessage} />}
            {tabIndex === 2 && <MyRegistrationsTab user={user} showMessage={showMessage} />}
        </Container>
    );
};

export default UserPage;