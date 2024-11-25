'use client';

import React, { useState } from 'react';
import { Box, TextField, Button, Typography } from '@mui/material';

interface BanquetSearchProps {
    onSearch: (BIN: number) => void;
    loading: boolean;
    errorMessage: string;
}

const BanquetSearch: React.FC<BanquetSearchProps> = ({ onSearch, loading, errorMessage }) => {
    const [BIN, setBIN] = useState('');

    const handleSearchClick = () => {
        const binNumber = parseInt(BIN);
        if (!isNaN(binNumber)) {
            onSearch(binNumber);
        }
    };

    return (
        <Box sx={{ mt: 2 }}>
            <Typography variant="h6">Search Registrations By BIN</Typography>
            <Box sx={{ display: 'flex', mt: 2 }}>
                <TextField
                    label="Banquet BIN"
                    variant="outlined"
                    fullWidth
                    value={BIN}
                    onChange={(e) => {
                        // Only allow numbers
                        const value = e.target.value.replace(/\D/g, '');
                        setBIN(value);
                    }}
                />
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSearchClick}
                    sx={{ ml: 2 }}
                    disabled={loading || !BIN}
                >
                    Search
                </Button>
            </Box>
            {loading && <Typography sx={{ mt: 2 }}>Loading...</Typography>}
            {errorMessage && (
                <Typography color="error" sx={{ mt: 2 }}>
                    {errorMessage}
                </Typography>
            )}
        </Box>
    );
};

export default BanquetSearch;