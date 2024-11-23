'use client';

import React, { useState } from 'react';
import {
    Box,
    Typography,
    TextField,
    Button,
    Stack,
    Alert,
} from '@mui/material';
import api from '../utils/api';

// Function to validate email format
function validateEmail(email: string) {
    const re = /\S+@\S+\.\S+/;
    return re.test(email);
}

interface Registration {
    attendeeEmail: string;
    banquetBIN: number;
    seatNo: number;
    regTime: string; // Assuming regTime is a string; adjust if it's a Date
    drinkChoice: string;
    mealChoice: string;
    remarks: string;
}

export default function AttendeeManagement() {
    const [email, setEmail] = useState('');
    const [attendee, setAttendee] = useState<any>(null);
    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState(''); // Error message for search
    const [errors, setErrors] = useState<{ [key: string]: string }>({}); // Errors for attendee update
    const [attendeeSuccessMessage, setAttendeeSuccessMessage] = useState(''); // Success message for attendee update

    const [registrations, setRegistrations] = useState<Registration[]>([]); // Attendee's registrations
    const [registrationErrors, setRegistrationErrors] = useState<{ [key: number]: { [key: string]: string } }>({}); // Errors for registration updates
    const [registrationSuccessMessages, setRegistrationSuccessMessages] = useState<{ [key: number]: string }>({}); // Success messages for registration updates

    // Handle attendee search by email
    const handleSearch = async () => {
        if (!email) return;
        setLoading(true);
        setErrorMessage('');
        setAttendee(null); // Reset attendee info when initiating search
        setRegistrations([]); // Reset registrations
        try {
            const response = await api.get('/getAttendeeByEmail', { params: { email } });
            console.log('Attendee:', response.data);
            if (response.data.status === 'success') {
                if (response.data.attendee == null) {
                    setErrorMessage('Attendee not found.');
                    setAttendee(null);
                } else {
                    setAttendee({
                        ...response.data.attendee,
                        password: '', // Initialize password as empty
                        originalEmail: response.data.attendee.email,
                    });
                    // After fetching attendee, fetch their registrations
                    fetchRegistrations(response.data.attendee.email);
                }
            } else {
                setErrorMessage('Attendee not found.');
                setAttendee(null);
            }
        } catch (error) {
            console.error('Error fetching attendee:', error);
            setErrorMessage('Error fetching attendee.');
            setAttendee(null);
        }
        setLoading(false);
    };

    // Fetch attendee's registrations
    const fetchRegistrations = async (email: string) => {
        try {
            const response = await api.get('/getReservesByAttendeeEmail', { params: { email } });
            if (response.data.status === 'success') {
                console.log('Registrations:', response.data.registrations);
                setRegistrations(response.data.registrations);
            } else {
                setErrorMessage('Error fetching registrations.');
            }
        } catch (error) {
            console.error('Error fetching registrations:', error);
            setErrorMessage('Error fetching registrations.');
        }
    };

    // Handle attendee information update
    const handleUpdateAttendee = async () => {
        // Reset previous errors and success message
        setErrors({});
        setAttendeeSuccessMessage('');

        const validationErrors: { [key: string]: string } = {};

        // Validate required fields
        const requiredFields = [
            'firstName',
            'lastName',
            'email',
            'address',
            'type',
            'organization',
            'mobileNo',
        ];
        for (const field of requiredFields) {
            if (!attendee[field] || attendee[field].trim() === '') {
                validationErrors[field] = 'This field is required.';
            }
        }

        // Validate email format
        if (attendee.email && !validateEmail(attendee.email)) {
            validationErrors['email'] = 'Invalid email format.';
        }

        // Validate mobile number: Only 8-digit number
        if (attendee.mobileNo && !/^\d{8}$/.test(attendee.mobileNo)) {
            validationErrors['mobileNo'] = 'Mobile number must be exactly 8 digits.';
        }

        // Validate first name and last name: Only English letters
        if (attendee.firstName && !/^[A-Za-z]+$/.test(attendee.firstName.trim())) {
            validationErrors['firstName'] = 'First name must contain only English letters.';
        }

        if (attendee.lastName && !/^[A-Za-z]+$/.test(attendee.lastName.trim())) {
            validationErrors['lastName'] = 'Last name must contain only English letters.';
        }

        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        try {
            // Send the updated attendee object to the backend
            const response = await api.post('/updateAttendeeProfile', attendee);
            if (response.data.status === 'success') {
                console.log('Attendee updated successfully');
                // Optionally, update originalEmail if email was changed
                setAttendee({
                    ...attendee,
                    originalEmail: attendee.email,
                });
                // Clear any previous errors
                setErrors({});
                // Set success message
                setAttendeeSuccessMessage('Attendee updated successfully.');
            } else {
                console.error('Failed to update attendee:', response.data.message);
                setErrors({ form: response.data.message || 'Failed to update attendee.' });
            }
        } catch (error) {
            console.error('Error updating attendee:', error);
            setErrors({ form: 'Error updating attendee.' });
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
        // Reset previous errors and success message
        setRegistrationErrors((prevErrors) => {
            const newErrors = { ...prevErrors };
            delete newErrors[index];
            return newErrors;
        });
        setRegistrationSuccessMessages((prevMessages) => {
            const newMessages = { ...prevMessages };
            delete newMessages[index];
            return newMessages;
        });

        const validationErrors: { [key: string]: string } = {};
        const registration = registrations[index];

        // Validate seat number: Must be a positive integer
        if (!registration.seatNo || isNaN(Number(registration.seatNo)) || Number(registration.seatNo) <= 0) {
            validationErrors['seatNo'] = 'Seat number must be a positive integer.';
        }

        // Validate required fields
        const requiredFields = ['drinkChoice', 'mealChoice'];
        for (const field of requiredFields) {
            if (!registration[field as keyof Registration] || (registration[field as keyof Registration] as string).trim() === '') {
                validationErrors[field] = 'This field is required.';
            }
        }

        if (Object.keys(validationErrors).length > 0) {
            setRegistrationErrors((prevErrors) => ({
                ...prevErrors,
                [index]: validationErrors,
            }));
            return;
        }

        try {
            // Send the updated registration data to the backend
            const response = await api.post('/updateAttendeeRegistrationData', {
                email: registration.attendeeEmail,
                registrationData: registration,
            });
            if (response.data.status === 'success') {
                console.log('Registration updated successfully');
                // Clear any previous errors
                setRegistrationErrors((prevErrors) => {
                    const newErrors = { ...prevErrors };
                    delete newErrors[index];
                    return newErrors;
                });
                // Set success message
                setRegistrationSuccessMessages((prevMessages) => ({
                    ...prevMessages,
                    [index]: 'Registration updated successfully.',
                }));
            } else {
                console.error('Failed to update registration:', response.data.message);
                setRegistrationErrors((prevErrors) => ({
                    ...prevErrors,
                    [index]: { form: response.data.message || 'Failed to update registration.' },
                }));
            }
        } catch (error) {
            console.error('Error updating registration:', error);
            setRegistrationErrors((prevErrors) => ({
                ...prevErrors,
                [index]: { form: 'Error updating registration.' },
            }));
        }
    };

    // Handle deletion of a registration
    const handleDeleteRegistration = async (index: number) => {
        const registration = registrations[index];

        // Reset previous errors and success messages
        setRegistrationErrors((prevErrors) => {
            const newErrors = { ...prevErrors };
            delete newErrors[index];
            return newErrors;
        });
        setRegistrationSuccessMessages((prevMessages) => {
            const newMessages = { ...prevMessages };
            delete newMessages[index];
            return newMessages;
        });

        try {
            // Send delete request to backend
            const response = await api.post('/deleteReserve', {
                attendeeEmail: registration.attendeeEmail,
                banquetBIN: registration.banquetBIN,
            });

            if (response.data.status === 'success') {
                // Remove the registration from the state
                const updatedRegistrations = [...registrations];
                updatedRegistrations.splice(index, 1);
                setRegistrations(updatedRegistrations);

                // Reset errors and success messages
                setRegistrationErrors({});
                setRegistrationSuccessMessages({});

                // Set success message
                setAttendeeSuccessMessage('Registration deleted successfully.');
            } else {
                // Handle error
                setRegistrationErrors((prevErrors) => ({
                    ...prevErrors,
                    [index]: { form: response.data.message || 'Failed to delete registration.' },
                }));
            }
        } catch (error) {
            console.error('Error deleting registration:', error);
            setRegistrationErrors((prevErrors) => ({
                ...prevErrors,
                [index]: { form: 'Error deleting registration.' },
            }));
        }
    };

    return (
        <Box sx={{ mt: 2 }}>
            {/* Search attendee by email */}
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
                    onClick={handleSearch}
                    sx={{ ml: 2 }}
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

            {/* Display attendee information if found */}
            {attendee && (
                <Box sx={{ mt: 4 }}>
                    <Typography variant="h6">Attendee Information</Typography>

                    {/* First Name and Last Name */}
                    <Stack
                        direction={{ xs: 'column', sm: 'row' }}
                        spacing={2}
                        sx={{ mt: 2 }}
                    >
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
                                setAttendee({ ...attendee, firstName: value });
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
                                setAttendee({ ...attendee, lastName: value });
                            }}
                        />
                    </Stack>

                    {/* Email and Address */}
                    <Stack
                        direction={{ xs: 'column', sm: 'row' }}
                        spacing={2}
                        sx={{ mt: 2 }}
                    >
                        <TextField
                            label="Email"
                            fullWidth
                            required
                            value={attendee.email}
                            error={!!errors.email}
                            helperText={errors.email}
                            onChange={(e) =>
                                setAttendee({ ...attendee, email: e.target.value })
                            }
                        />
                        <TextField
                            label="Address"
                            fullWidth
                            required
                            value={attendee.address}
                            error={!!errors.address}
                            helperText={errors.address}
                            onChange={(e) =>
                                setAttendee({ ...attendee, address: e.target.value })
                            }
                        />
                    </Stack>

                    {/* Type and Organization */}
                    <Stack
                        direction={{ xs: 'column', sm: 'row' }}
                        spacing={2}
                        sx={{ mt: 2 }}
                    >
                        <TextField
                            label="Type"
                            fullWidth
                            required
                            value={attendee.type}
                            error={!!errors.type}
                            helperText={errors.type}
                            onChange={(e) =>
                                setAttendee({ ...attendee, type: e.target.value })
                            }
                        />
                        <TextField
                            label="Organization"
                            fullWidth
                            required
                            value={attendee.organization}
                            error={!!errors.organization}
                            helperText={errors.organization}
                            onChange={(e) =>
                                setAttendee({ ...attendee, organization: e.target.value })
                            }
                        />
                    </Stack>

                    {/* Mobile Number */}
                    <Stack
                        direction={{ xs: 'column', sm: 'row' }}
                        spacing={2}
                        sx={{ mt: 2 }}
                    >
                        <TextField
                            label="Mobile Number"
                            fullWidth
                            required
                            value={attendee.mobileNo}
                            error={!!errors.mobileNo}
                            helperText={errors.mobileNo}
                            onChange={(e) => {
                                // Allow only digits
                                const value = e.target.value.replace(/\D/g, '');
                                // Limit to 8 digits
                                if (value.length <= 8) {
                                    setAttendee({ ...attendee, mobileNo: value });
                                }
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
                            onChange={(e) =>
                                setAttendee({ ...attendee, password: e.target.value })
                            }
                        />
                    </Stack>

                    {/* Display form-level error message */}
                    {errors.form && (
                        <Typography color="error" sx={{ mt: 2 }}>
                            {errors.form}
                        </Typography>
                    )}

                    {/* Display success message */}
                    {attendeeSuccessMessage && (
                        <Typography sx={{ mt: 2, color: 'green' }}>
                            {attendeeSuccessMessage}
                        </Typography>
                    )}

                    {/* Button to update attendee information */}
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleUpdateAttendee}
                        sx={{ mt: 2 }}
                    >
                        Update Attendee
                    </Button>
                </Box>
            )}

            {/* Display attendee registrations if any */}
            {attendee && registrations && registrations.length > 0 && (
                <Box sx={{ mt: 4 }}>
                    <Typography variant="h6">Attendee Registrations</Typography>
                    {registrations.map((registration, index) => (
                        <Box key={index} sx={{ mt: 2, p: 2, border: '1px solid #ccc' }}>
                            <Typography variant="subtitle1">Banquet BIN: {registration.banquetBIN}</Typography>

                            {/* Registration Time and Seat Number */}
                            <Stack
                                direction={{ xs: 'column', sm: 'row' }}
                                spacing={2}
                                sx={{ mt: 2 }}
                            >
                                <TextField
                                    label="Registration Time"
                                    fullWidth
                                    disabled
                                    value={registration.regTime}
                                />
                                <TextField
                                    label="Seat Number"
                                    fullWidth
                                    required
                                    value={registration.seatNo}
                                    error={!!registrationErrors[index]?.seatNo}
                                    helperText={registrationErrors[index]?.seatNo}
                                    onChange={(e) => {
                                        // Allow only numbers
                                        const value = e.target.value.replace(/\D/g, '');
                                        handleRegistrationChange(index, 'seatNo', value);
                                    }}
                                />
                            </Stack>

                            {/* Drink Choice and Meal Choice */}
                            <Stack
                                direction={{ xs: 'column', sm: 'row' }}
                                spacing={2}
                                sx={{ mt: 2 }}
                            >
                                <TextField
                                    label="Drink Choice"
                                    fullWidth
                                    required
                                    value={registration.drinkChoice}
                                    error={!!registrationErrors[index]?.drinkChoice}
                                    helperText={registrationErrors[index]?.drinkChoice}
                                    onChange={(e) =>
                                        handleRegistrationChange(index, 'drinkChoice', e.target.value)
                                    }
                                />
                                <TextField
                                    label="Meal Choice"
                                    fullWidth
                                    required
                                    value={registration.mealChoice}
                                    error={!!registrationErrors[index]?.mealChoice}
                                    helperText={registrationErrors[index]?.mealChoice}
                                    onChange={(e) =>
                                        handleRegistrationChange(index, 'mealChoice', e.target.value)
                                    }
                                />
                            </Stack>

                            {/* Remarks */}
                            <Stack direction="column" spacing={2} sx={{ mt: 2 }}>
                                <TextField
                                    label="Remarks"
                                    fullWidth
                                    multiline
                                    value={registration.remarks}
                                    onChange={(e) =>
                                        handleRegistrationChange(index, 'remarks', e.target.value)
                                    }
                                />
                            </Stack>

                            {/* Display form-level error message */}
                            {registrationErrors[index]?.form && (
                                <Typography color="error" sx={{ mt: 2 }}>
                                    {registrationErrors[index]?.form}
                                </Typography>
                            )}

                            {/* Display success message */}
                            {registrationSuccessMessages[index] && (
                                <Typography sx={{ mt: 2, color: 'green' }}>
                                    {registrationSuccessMessages[index]}
                                </Typography>
                            )}

                            {/* Buttons to update and delete registration */}
                            <Stack direction="row" spacing={2} sx={{ mt: 2 }}>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={() => handleUpdateRegistration(index)}
                                >
                                    Update Registration
                                </Button>
                                <Button
                                    variant="contained"
                                    color="error"
                                    onClick={() => handleDeleteRegistration(index)}
                                >
                                    Delete Registration
                                </Button>
                            </Stack>
                        </Box>
                    ))}
                </Box>
            )}
        </Box>
    );
}