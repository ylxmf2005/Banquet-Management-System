// src/components/RegistrationList.tsx
'use client';

import React from 'react';
import { Box, Typography } from '@mui/material';
import { Registration } from '../utils/types';
import RegistrationItem from './RegistrationItem';

interface RegistrationListProps {
    registrations: Registration[];
    registrationErrors: { [key: number]: { [key: string]: string } };
    registrationSuccessMessages: { [key: number]: string };
    onRegistrationChange: (index: number, field: string, value: any) => void;
    onUpdateRegistration: (index: number) => void;
    onDeleteRegistration: (index: number) => void;
}

const RegistrationList: React.FC<RegistrationListProps> = ({
    registrations,
    registrationErrors,
    registrationSuccessMessages,
    onRegistrationChange,
    onUpdateRegistration,
    onDeleteRegistration,
}) => {
    return (
        <Box sx={{ mt: 4 }}>
            <Typography variant="h6">Attendee Registrations</Typography>
            {registrations.map((registration, index) => (
                <RegistrationItem
                    key={index}
                    registration={registration}
                    errors={registrationErrors[index] || {}}
                    successMessage={registrationSuccessMessages[index] || ''}
                    onChange={(field, value) => onRegistrationChange(index, field, value)}
                    onUpdate={() => onUpdateRegistration(index)}
                    onDelete={() => onDeleteRegistration(index)}
                />
            ))}
        </Box>
    );
};

export default RegistrationList;