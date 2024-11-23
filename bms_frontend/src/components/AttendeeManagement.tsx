// src/components/AttendeeManagement.tsx
'use client';

import React, { useState, useContext } from 'react';
import { Box } from '@mui/material';
import { Attendee, Registration } from '../utils/types';
import AttendeeSearch from './AttendeeSearch';
import AttendeeForm from './AttendeeForm';
import RegistrationList from './RegistrationList';
import Service from '../service/Service';
import { attendeeSchema, registrationSchema } from '../utils/validationSchemas';
import * as Yup from 'yup';
import { SnackbarContext } from '../context/SnackbarContext'; 


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

    // Handle attendee search by email
    const handleSearch = async (email: string) => {
        setLoading(true);
        setSearchError('');
        // Reset attendee and registrations
        setAttendee(null);
        setRegistrations([]);
        try {
            const fetchedAttendee = await Service.getAttendeeByEmail(email);
            if (fetchedAttendee) {
                setAttendee({
                    ...fetchedAttendee,
                    password: '',
                    originalEmail: fetchedAttendee.email,
                });
                fetchRegistrations(fetchedAttendee.email);
            } else {
                // Display error using Snackbar
                showMessage('Attendee not found.', 'error');
            }
        } catch (error) {
            console.error('Error fetching attendee:', error);
            showMessage('Error fetching attendee.', 'error');
        }
        setLoading(false);
    };

    // Fetch attendee's registrations
    const fetchRegistrations = async (email: string) => {
        try {
            const fetchedRegistrations = await Service.getRegistrationsByEmail(email);
            setRegistrations(fetchedRegistrations);
        } catch (error) {
            console.error('Error fetching registrations:', error);
            showMessage('Error fetching registrations.', 'error');
        }
    };

    // Handle attendee information update
    const handleUpdateAttendee = async () => {
        setErrors({});
        if (attendee) {
            // Validate attendee data
            try {
                await attendeeSchema.validate(attendee, { abortEarly: false });
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
            }

            try {
                const response = await Service.updateAttendeeProfile(attendee);
                if (response.status === 'success') {
                    // Update originalEmail if email was changed
                    setAttendee({ ...attendee, originalEmail: attendee.email });
                    setErrors({});
                    // Display success message using Snackbar
                    showMessage('Attendee updated successfully.', 'success');
                } else {
                    // Display error message using Snackbar
                    showMessage(response.message || 'Failed to update attendee.', 'error');
                }
            } catch (error) {
                console.error('Error updating attendee:', error);
                showMessage('Error updating attendee.', 'error');
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
            await registrationSchema.validate(registration, { abortEarly: false });
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
            const response = await Service.updateRegistrationData(registration);
            if (response.status === 'success') {
                setRegistrationSuccessMessages(prev => ({
                    ...prev,
                    [index]: 'Registration updated successfully.'
                }));
                showMessage('Registration updated successfully.', 'success');
            } else {
                showMessage(response.message || 'Failed to update registration.', 'error');
            }
        } catch (error) {
            console.error('Error updating registration:', error);
            showMessage('Error updating registration.', 'error');
        }
    };

    // Handle deletion of a registration
    const handleDeleteRegistration = async (index: number) => {
        const registration = registrations[index];
        if (!registration) return;

        // Reset previous errors
        setRegistrationErrors((prev) => {
            const newErrors = { ...prev };
            delete newErrors[index];
            return newErrors;
        });

        try {
            const response = await Service.deleteRegistration(
                registration.attendeeEmail,
                registration.banquetBIN
            );
            if (response.status === 'success') {
                // Remove the registration from the state
                const updatedRegistrations = [...registrations];
                updatedRegistrations.splice(index, 1);
                setRegistrations(updatedRegistrations);

                // Display success message using Snackbar
                showMessage('Registration deleted successfully.', 'success');
            } else {
                // Display error message using Snackbar
                showMessage(response.message || 'Failed to delete registration.', 'error');
            }
        } catch (error) {
            console.error('Error deleting registration:', error);
            showMessage('Error deleting registration.', 'error');
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