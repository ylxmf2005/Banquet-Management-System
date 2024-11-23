// src/context/SnackbarContext.tsx
'use client';
import React, { createContext, useState } from 'react';
import { Snackbar, Alert, AlertColor } from '@mui/material';

interface SnackbarContextValue {
    showMessage: (message: string, severity?: AlertColor) => void;
}

export const SnackbarContext = createContext<SnackbarContextValue>({
    showMessage: () => { },
});

export const SnackbarProvider = ({ children }: { children: React.ReactNode }) => {
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarSeverity, setSnackbarSeverity] = useState<AlertColor>('success');

    const showMessage = (message: string, severity: AlertColor = 'success') => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    };

    const handleSnackbarClose = (
        event?: React.SyntheticEvent | Event,
        reason?: string
    ) => {
        if (reason === 'clickaway') {
            return;
        }
        setSnackbarOpen(false);
    };

    return (
        <SnackbarContext.Provider value={{ showMessage }}>
            {children}
            <Snackbar
                open={snackbarOpen}
                autoHideDuration={2000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
            >
                <Alert
                    onClose={handleSnackbarClose}
                    severity={snackbarSeverity}
                    sx={{ width: '100%' }}
                >
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </SnackbarContext.Provider>
    );
};