// src/components/AttendeeForm.tsx
'use client';

import React from 'react';
import { Box, Typography, TextField, Button, Stack } from '@mui/material';
import { Attendee } from '../../utils/types';

interface AttendeeFormProps {
    attendee: Attendee;
    errors: { [key: string]: string };
    successMessage: string;
    onUpdate: () => void;
    onChange: (field: string, value: any) => void;
}

const AttendeeForm: React.FC<AttendeeFormProps> = ({
    attendee,
    errors,
    successMessage,
    onUpdate,
    onChange,
}) => {
    return (
        <Box sx={{ mt: 4 }}>
            <Typography variant="h6">Attendee Information</Typography>

            {/* First Name and Last Name */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="First Name"
                    fullWidth
                    required
                    value={attendee.firstName}
                    error={!!errors.firstName}
                    helperText={errors.firstName}
                    onChange={(e) => {
                        // Allow only English letters
                        const value = e.target.value.replace(/[^A-Za-z]/g, '');
                        onChange('firstName', value);
                    }}
                />
                <TextField
                    label="Last Name"
                    fullWidth
                    required
                    value={attendee.lastName}
                    error={!!errors.lastName}
                    helperText={errors.lastName}
                    onChange={(e) => {
                        // Allow only English letters
                        const value = e.target.value.replace(/[^A-Za-z]/g, '');
                        onChange('lastName', value);
                    }}
                />
            </Stack>

            {/* Email and Address */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="Email"
                    fullWidth
                    required
                    value={attendee.email}
                    error={!!errors.email}
                    helperText={errors.email}
                    onChange={(e) => onChange('email', e.target.value)}
                />
                <TextField
                    label="Address"
                    fullWidth
                    required
                    value={attendee.address}
                    error={!!errors.address}
                    helperText={errors.address}
                    onChange={(e) => onChange('address', e.target.value)}
                />
            </Stack>

            {/* Type and Organization */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="Type"
                    fullWidth
                    required
                    value={attendee.type}
                    error={!!errors.type}
                    helperText={errors.type}
                    onChange={(e) => onChange('type', e.target.value)}
                />
                <TextField
                    label="Organization"
                    fullWidth
                    required
                    value={attendee.organization}
                    error={!!errors.organization}
                    helperText={errors.organization}
                    onChange={(e) => onChange('organization', e.target.value)}
                />
            </Stack>

            {/* Mobile Number */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="Mobile Number"
                    fullWidth
                    required
                    value={attendee.mobileNo}
                    error={!!errors.mobileNo}
                    helperText={errors.mobileNo}
                    onChange={(e) => {
                        // Allow only digits and limit to 8 characters
                        const value = e.target.value.replace(/\D/g, '').slice(0, 8);
                        onChange('mobileNo', value);
                    }}
                    inputProps={{ maxLength: 8 }}
                />
            </Stack>

            {/* Password (optional) */}
            <Stack direction="column" spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="Password"
                    type="password"
                    fullWidth
                    value={attendee.password || ''}
                    error={!!errors.password}
                    helperText={errors.password}
                    onChange={(e) => onChange('password', e.target.value)}
                />
            </Stack>

            {/* Display form-level error message */}
            {errors.form && (
                <Typography color="error" sx={{ mt: 2 }}>
                    {errors.form}
                </Typography>
            )}

            {/* Display success message */}
            {successMessage && (
                <Typography sx={{ mt: 2, color: 'green' }}>{successMessage}</Typography>
            )}

            {/* Button to update attendee information */}
            <Button
                variant="contained"
                color="primary"
                onClick={onUpdate}
                sx={{ mt: 2 }}
            >
                Update Attendee
            </Button>
        </Box>
    );
};

export default AttendeeForm;