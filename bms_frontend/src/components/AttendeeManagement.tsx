// src/components/AttendeeManagement.tsx
'use client';

import React, { useState, useContext } from 'react';
import { Box } from '@mui/material';
import { Attendee, Registration } from '../utils/types';
import AttendeeSearch from './AttendeeSearch';
import AttendeeForm from './AttendeeForm';
import RegistrationList from './RegistrationList';
import { attendeeSchema, registrationSchemaForAdmin } from '../utils/validationSchemas';
import * as Yup from 'yup';
import { SnackbarContext } from '../context/SnackbarContext'; 
import api from '../service/api';


export default function AttendeeManagement() {
    // Get showMessage function from SnackbarContext
    const { showMessage } = useContext(SnackbarContext);

    // State for attendee information
    const [attendee, setAttendee] = useState<Attendee | null>(null);
    const [registrations, setRegistrations] = useState<Registration[]>([]);
    const [loading, setLoading] = useState(false);

    // State for form validation errors
    const [errors, setErrors] = useState<{ [key: string]: string }>({});

    // State for registration validation errors
    const [registrationErrors, setRegistrationErrors] = useState<{
        [key: number]: { [key: string]: string };
    }>({});

    const [registrationSuccessMessages, setRegistrationSuccessMessages] = useState<{
        [key: number]: string;
    }>({});

    const [searchError, setSearchError] = useState<string>('');

    const handleApiResponse = (
        response: any,
        successCallback: (data: any) => void,
        action: string
    ) => {
        const data = response.data;
        if (data.status === 'success') {
            successCallback(data);
        } else {
            const message = `Failed to ${action}: ${data.message || 'Unknown error'}`;
            showMessage(message, 'error');
        }
    };

    const handleApiError = (error: any, action: string) => {
        let message = '';
        if (error.response?.data?.message) {
            message = `Error ${action}: ${error.response.data.message}`;
        } else {
            message = `Error ${action}: ${error.message}`;
        }
        showMessage(message, 'error');
    };

    // Handle attendee search by email
    const handleSearch = async (email: string) => {
        setLoading(true);
        setSearchError('');
        setAttendee(null);
        setRegistrations([]);
        try {
            const response = await api.get('/getAttendeeByEmail', { params: { email } });
            handleApiResponse(
                response,
                (data) => {
                    const fetchedAttendee = data.attendee;
                    setAttendee({
                        ...fetchedAttendee,
                        password: '',
                        originalEmail: fetchedAttendee.email,
                    });
                    fetchRegistrations(fetchedAttendee.email);
                },
                'fetching attendee'
            );
        } catch (error) {
            handleApiError(error, 'fetching attendee');
        }
        setLoading(false);
    };

    // Fetch attendee's registrations
    const fetchRegistrations = async (email: string) => {
        try {
            const response = await api.get('/getReservesByAttendeeEmail', { params: { email } });
            handleApiResponse(
                response,
                (data) => {
                    setRegistrations(data.registrations);
                },
                'fetching registrations'
            );
        } catch (error) {
            handleApiError(error, 'fetching registrations');
        }
    };

    // Handle attendee information update
    const handleUpdateAttendee = async () => {
        setErrors({});
        if (attendee) {
            try {
                await attendeeSchema.validate(attendee, { abortEarly: false });
                const response = await api.post('/updateAttendeeProfile', attendee);
                handleApiResponse(
                    response,
                    () => {
                        setAttendee({ ...attendee, originalEmail: attendee.email });
                        setErrors({});
                        showMessage('Attendee updated successfully.', 'success');
                    },
                    'updating attendee'
                );
            } catch (err) {
                if (err instanceof Yup.ValidationError) {
                    const validationErrors: { [key: string]: string } = {};
                    err.inner.forEach((error) => {
                        if (error.path) {
                            validationErrors[error.path] = error.message;
                        }
                    });
                    setErrors(validationErrors);
                    return;
                }
                handleApiError(err, 'updating attendee');
            }
        }
    };

    // Handle attendee field change
    const handleAttendeeChange = (field: string, value: any) => {
        if (attendee) {
            setAttendee({ ...attendee, [field]: value });
        }
    };

    // Handle registration data change
    const handleRegistrationChange = (index: number, field: string, value: any) => {
        const updatedRegistrations = [...registrations];
        updatedRegistrations[index] = { ...updatedRegistrations[index], [field]: value };
        setRegistrations(updatedRegistrations);
    };

    // Validate and update registration data
    const handleUpdateRegistration = async (index: number) => {
        // Reset previous errors
        setRegistrationErrors((prev) => {
            const newErrors = { ...prev };
            delete newErrors[index];
            return newErrors;
        });

        const registration = registrations[index];

        // Validate registration data
        try {
            await registrationSchemaForAdmin.validate(registration, { abortEarly: false });
        } catch (err) {
            if (err instanceof Yup.ValidationError) {
                const validationErrors: { [key: string]: string } = {};
                err.inner.forEach((error) => {
                    if (error.path) {
                        validationErrors[error.path] = error.message;
                    }
                });
                setRegistrationErrors((prev) => ({
                    ...prev,
                    [index]: validationErrors,
                }));
                return;
            }
        }

        try {
            const response = await api.post('/updateAttendeeRegistrationData', {
                registrationData: registrations[index],
            });
            handleApiResponse(
                response,
                () => {
                    setRegistrationSuccessMessages(prev => ({
                        ...prev,
                        [index]: 'Registration updated successfully.'
                    }));
                    showMessage('Registration updated successfully.', 'success');
                },
                'updating registration'
            );
        } catch (error) {
            handleApiError(error, 'updating registration');
        }
    };

    // Handle deletion of a registration
    const handleDeleteRegistration = async (index: number) => {
        const registration = registrations[index];
        if (!registration) return;

        setRegistrationErrors((prev) => {
            const newErrors = { ...prev };
            delete newErrors[index];
            return newErrors;
        });

        try {
            const response = await api.post('/deleteReserve', {
                attendeeEmail: registration.attendeeEmail,
                banquetBIN: registration.banquetBIN,
            });
            handleApiResponse(
                response,
                () => {
                    const updatedRegistrations = [...registrations];
                    updatedRegistrations.splice(index, 1);
                    setRegistrations(updatedRegistrations);
                    showMessage('Registration deleted successfully.', 'success');
                },
                'deleting registration'
            );
        } catch (error) {
            handleApiError(error, 'deleting registration');
        }
    };

    return (
        <Box sx={{ mt: 2 }}>
            {/* Search attendee by email */}
            <AttendeeSearch
                onSearch={handleSearch}
                loading={loading}
                errorMessage={searchError}
            />

            {/* Display attendee information if found */}
            {attendee && (
                <>
                    <AttendeeForm
                        attendee={attendee}
                        errors={errors}
                        onUpdate={handleUpdateAttendee}
                        onChange={handleAttendeeChange}
                        successMessage=""
                    />

                    {/* Display attendee registrations if any */}
                    {registrations && registrations.length > 0 && (
                        <RegistrationList
                            registrations={registrations}
                            registrationErrors={registrationErrors}
                            registrationSuccessMessages={registrationSuccessMessages}
                            onRegistrationChange={handleRegistrationChange}
                            onUpdateRegistration={handleUpdateRegistration}
                            onDeleteRegistration={handleDeleteRegistration}
                        />
                    )}
                </>
            )}
        </Box>
    );
}