'use client';
import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useRouter } from 'next/navigation';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';

export default function Navbar() {
    const auth = useContext(AuthContext);
    const router = useRouter();

    const handleLogout = () => {
        auth?.logout();
        router.push('/login');
    };

    const handleLogin = () => {
        router.push('/login');
    };

    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    BMS
                </Typography>
                {auth?.user ? (
                    <>
                        <Typography variant="body1" sx={{ marginRight: 2 }}>
                            Welcome, {auth.user.email}
                        </Typography>
                        <Button color="inherit" onClick={handleLogout}>
                            Logout
                        </Button>
                    </>
                ) : (
                    <Button color="inherit" onClick={handleLogin}>
                        Login
                    </Button>
                )}
            </Toolbar>
        </AppBar>
    );
}
