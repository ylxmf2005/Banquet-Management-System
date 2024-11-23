// src/components/AttendeeSearch.tsx
'use client';

import React, { useState } from 'react';
import { Box, TextField, Button, Typography } from '@mui/material';

interface AttendeeSearchProps {
    onSearch: (email: string) => void;
    loading: boolean;
    errorMessage: string;
}

const AttendeeSearch: React.FC<AttendeeSearchProps> = ({ onSearch, loading, errorMessage }) => {
    const [email, setEmail] = useState('');

    const handleSearchClick = () => {
        onSearch(email);
    };

    return (
        <Box sx={{ mt: 2 }}>
            <Typography variant="h6">Search Attendee By Email</Typography>
            <Box sx={{ display: 'flex', mt: 2 }}>
                <TextField
                    label="Email Address"
                    variant="outlined"
                    fullWidth
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSearchClick}
                    sx={{ ml: 2 }}
                    disabled={loading || !email}
                >
                    Search
                </Button>
            </Box>
            {/* Display loading indicator */}
            {loading && <Typography sx={{ mt: 2 }}>Loading...</Typography>}
            {/* Display error message if attendee not found */}
            {errorMessage && (
                <Typography color="error" sx={{ mt: 2 }}>
                    {errorMessage}
                </Typography>
            )}
        </Box>
    );
};

export default AttendeeSearch;