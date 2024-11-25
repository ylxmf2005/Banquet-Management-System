'use client';

import React, { useState, useContext } from 'react';
import { Box, Button, Typography, CircularProgress } from '@mui/material';
import { SnackbarContext } from '../../context/SnackbarContext';
import api from '../../service/api';

export default function ReportManagement() {
    const { showMessage } = useContext(SnackbarContext);
    const [loading, setLoading] = useState(false);

    const handleDownloadReport = async () => {
        setLoading(true);
        try {
            const response = await api.get('/generateReport', {
                responseType: 'blob',
                headers: {
                    'Accept': 'application/octet-stream'
                }
            });

            if (!(response.data instanceof Blob)) {
                throw new Error('Invalid response format');
            }

            const url = window.URL.createObjectURL(response.data);
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'banquet_report.pdf');
            document.body.appendChild(link);
            link.click();
            
            window.URL.revokeObjectURL(url);
            link.remove();

            showMessage('Report downloaded successfully', 'success');
        } catch (error: any) {
            console.error('Error downloading report:', error);
            showMessage('Failed to download report: ' + (error.message || 'Unknown error'), 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ mt: 1, p: 2 }}>
            <Button
                variant="contained"
                color="primary"
                onClick={handleDownloadReport}
                disabled={loading}
                startIcon={loading && <CircularProgress size={20} color="inherit" />}
            >
                {loading ? 'Generating Report...' : 'Download Report'}
            </Button>
        </Box>
    );
} 