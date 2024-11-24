// src/components/ProfileTab.tsx

import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Button,
    TextField,
    Grid,
    MenuItem,
    Select,
    FormControl,
    InputLabel,
    SelectChangeEvent,
} from '@mui/material';
import api from '../service/api';
import * as yup from 'yup';
import { AlertColor } from '@mui/material';
import { attendeeSchema } from '../utils/validationSchemas';
import { Attendee } from '../utils/types';

interface User {
    email: string;
    role: 'admin' | 'user';
}

interface ProfileTabProps {
    user: User;
    showMessage: (message: string, severity?: AlertColor) => void;
}

const ProfileTab: React.FC<ProfileTabProps> = ({ user, showMessage }) => {
    const [profileData, setProfileData] = useState<Attendee>({
        firstName: '',
        lastName: '',
        address: '',
        type: '',
        email: user.email,
        password: '',
        mobileNo: '',
        organization: '',
        originalEmail: user.email, // Keep track of original email
    });

    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState<{ [key: string]: string }>({});

    useEffect(() => {
        // Fetch the user's profile data
        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        setLoading(true);
        try {
            const response = await api.get('/getAttendeeByEmail', { params: { email: user.email } });

            handleApiResponse(
                response,
                (data: any) => {
                    // Set profileData with fetched data
                    const fetchedProfile = data.attendee;
                    setProfileData({
                        ...fetchedProfile,
                        password: '',
                        originalEmail: fetchedProfile.email,
                    });
                    setLoading(false);
                },
                'fetching profile data'
            );
        } catch (error) {
            handleApiError(error, 'fetching profile data');
            setLoading(false);
        }
    };

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
        if (error.response && error.response.data && error.response.data.message) {
            message = `Error ${action}: ${error.response.data.message}`;
        } else {
            message = `Error ${action}: ${error.message}`;
        }
        showMessage(message, 'error');
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setProfileData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSelectChange = (e: SelectChangeEvent<string>) => {
        const { name, value } = e.target;
        setProfileData((prevData) => ({
            ...prevData,
            [name!]: value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await attendeeSchema.validate(profileData, { abortEarly: false });
            // Clear previous errors
            setErrors({});

            // Update the profile via API
            const response = await api.post('/updateAttendeeProfile', profileData);
            handleApiResponse(
                response,
                () => {
                    showMessage('Profile updated successfully', 'success');
                    // Update originalEmail if email was changed
                    setProfileData((prevData) => ({
                        ...prevData,
                        originalEmail: prevData.email,
                    }));
                },
                'updating profile'
            );
        } catch (error: any) {
            if (error.name === 'ValidationError') {
                const validationErrors: { [key: string]: string } = {};
                error.inner.forEach((err: any) => {
                    if (err.path) {
                        validationErrors[err.path] = err.message;
                    }
                });
                setErrors(validationErrors);
            } else {
                handleApiError(error, 'updating profile');
            }
        }
    };

    if (loading) {
        return <Typography>Loading...</Typography>;
    }

    return (
        <Box sx={{ mt: 3 }}>
            <form onSubmit={handleSubmit}>
                <Grid container spacing={2}>
                    {/* First Name */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            name="firstName"
                            label="First Name"
                            value={profileData.firstName}
                            onChange={(e) => {
                                // Allow only English letters
                                const value = e.target.value.replace(/[^A-Za-z]/g, '');
                                setProfileData((prevData) => ({
                                    ...prevData,
                                    firstName: value,
                                }));
                            }}
                            fullWidth
                            error={!!errors.firstName}
                            helperText={errors.firstName}
                        />
                    </Grid>
                    {/* Last Name */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            name="lastName"
                            label="Last Name"
                            value={profileData.lastName}
                            onChange={(e) => {
                                const value = e.target.value.replace(/[^A-Za-z]/g, '');
                                setProfileData((prevData) => ({
                                    ...prevData,
                                    lastName: value,
                                }));
                            }}
                            fullWidth
                            error={!!errors.lastName}
                            helperText={errors.lastName}
                        />
                    </Grid>
                    {/* Address */}
                    <Grid item xs={12}>
                        <TextField
                            name="address"
                            label="Address"
                            value={profileData.address}
                            onChange={handleInputChange}
                            fullWidth
                            error={!!errors.address}
                            helperText={errors.address}
                        />
                    </Grid>
                    {/* Attendee Type */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            name="type"
                            label="Attendee Type"
                            value={profileData.type}
                            onChange={handleInputChange}
                            fullWidth
                            error={!!errors.type}
                            helperText={errors.type}
                        />
                    </Grid>
                    {/* Email */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            name="email"
                            label="Email Address"
                            value={profileData.email}
                            onChange={handleInputChange}
                            fullWidth
                            error={!!errors.email}
                            helperText={errors.email}
                        />
                    </Grid>
                    {/* Mobile Number */}
                    <Grid item xs={12} sm={6}>
                        <TextField
                            name="mobileNo"
                            label="Mobile Number"
                            value={profileData.mobileNo}
                            onChange={(e) => {
                                // Allow only digits and limit to 8 characters
                                const value = e.target.value.replace(/\D/g, '').slice(0, 8);
                                setProfileData((prevData) => ({
                                    ...prevData,
                                    mobileNo: value,
                                }));
                            }}
                            fullWidth
                            error={!!errors.mobileNo}
                            helperText={errors.mobileNo}
                            inputProps={{ maxLength: 8 }}
                        />
                    </Grid>
                    {/* Affiliated Organization */}
                    <Grid item xs={12} sm={6}>
                        <FormControl fullWidth error={!!errors.organization}>
                            <InputLabel id="organization-label">Affiliated Organization</InputLabel>
                            <Select
                                labelId="organization-label"
                                name="organization"
                                value={profileData.organization}
                                onChange={handleSelectChange}
                                label="Affiliated Organization"
                            >
                                <MenuItem value="PolyU">PolyU</MenuItem>
                                <MenuItem value="SPEED">SPEED</MenuItem>
                                <MenuItem value="HKCC">HKCC</MenuItem>
                                <MenuItem value="Others">Others</MenuItem>
                            </Select>
                            {errors.organization && (
                                <Typography variant="caption" color="error">
                                    {errors.organization}
                                </Typography>
                            )}
                        </FormControl>
                    </Grid>
                    {/* New Password Field */}
                    <Grid item xs={12}>
                        <TextField
                            name="password"
                            label="New Password"
                            type="password"
                            value={profileData.password || ''}
                            onChange={handleInputChange}
                            fullWidth
                            error={!!errors.password}
                            helperText={errors.password || 'Leave blank if you don\'t want to change'}
                        />
                    </Grid>
                </Grid>

                {/* Display form-level error message if any */}
                {errors.form && (
                    <Typography color="error" sx={{ mt: 2 }}>
                        {errors.form}
                    </Typography>
                )}

                <Button type="submit" variant="contained" sx={{ mt: 2 }}>
                    Update Profile
                </Button>
            </form>
        </Box>
    );
};

export default ProfileTab;