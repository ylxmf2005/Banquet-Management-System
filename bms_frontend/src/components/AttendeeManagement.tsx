'use client';

import React, { useState } from 'react';
import {
    Box,
    Typography,
    TextField,
    Button,
    Stack,
    Select,
    MenuItem,
    InputLabel,
    FormControl,
} from '@mui/material';
import api from '../utils/api';

export default function AttendeeManagement() {
    const [email, setEmail] = useState('');
    const [attendee, setAttendee] = useState<any>(null);
    const [loading, setLoading] = useState(false);

    // Handle attendee search by email
    const handleSearch = async () => {
        if (!email) return;
        setLoading(true);
        try {
            const response = await api.get('/getAttendeeByEmail', { params: { email } });
            console.log('Attendee:', response.data);
            if (response.data.status === 'success') {
                // Set the attendee state with the data from the backend
                // Include originalEmail to handle email changes
                setAttendee({
                    ...response.data.attendee,
                    originalEmail: response.data.attendee.email,
                });
            } else {
                console.error('Attendee not found');
                setAttendee(null);
            }
        } catch (error) {
            console.error('Error fetching attendee:', error);
            setAttendee(null);
        }
        setLoading(false);
    };

    // Handle attendee information update
    const handleUpdateAttendee = async () => {
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
            } else {
                console.error('Failed to update attendee:', response.data.message);
            }
        } catch (error) {
            console.error('Error updating attendee:', error);
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
                            value={attendee.firstName}
                            onChange={(e) =>
                                setAttendee({ ...attendee, firstName: e.target.value })
                            }
                        />
                        <TextField
                            label="Last Name"
                            fullWidth
                            value={attendee.lastName}
                            onChange={(e) =>
                                setAttendee({ ...attendee, lastName: e.target.value })
                            }
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
                            value={attendee.email}
                            onChange={(e) =>
                                setAttendee({ ...attendee, email: e.target.value })
                            }
                        />
                        <TextField
                            label="Address"
                            fullWidth
                            value={attendee.address}
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
                            value={attendee.type}
                            onChange={(e) =>
                                setAttendee({ ...attendee, type: e.target.value })
                            }
                        />
                        <TextField
                            label="Organization"
                            fullWidth
                            value={attendee.organization}
                            onChange={(e) =>
                                setAttendee({ ...attendee, organization: e.target.value })
                            }
                        />
                    </Stack>

                    {/* Mobile Number and Role */}
                    <Stack
                        direction={{ xs: 'column', sm: 'row' }}
                        spacing={2}
                        sx={{ mt: 2 }}
                    >
                        <TextField
                            label="Mobile Number"
                            fullWidth
                            value={attendee.mobileNo}
                            onChange={(e) =>
                                setAttendee({ ...attendee, mobileNo: e.target.value })
                            }
                        />
                        {/* Role Selection */}
                        <FormControl fullWidth>
                            <InputLabel id="role-select-label">Role</InputLabel>
                            <Select
                                labelId="role-select-label"
                                id="role-select"
                                value={attendee.role}
                                label="Role"
                                onChange={(e) =>
                                    setAttendee({ ...attendee, role: e.target.value })
                                }
                            >
                                <MenuItem value="user">User</MenuItem>
                                <MenuItem value="admin">Admin</MenuItem>
                            </Select>
                        </FormControl>
                    </Stack>

                    {/* Password (if necessary) */}
                    <Stack
                        direction="column"
                        spacing={2}
                        sx={{ mt: 2 }}
                    >
                        <TextField
                            label="Password"
                            type="password"
                            fullWidth
                            value={attendee.password}
                            onChange={(e) =>
                                setAttendee({ ...attendee, password: e.target.value })
                            }
                        />
                    </Stack>

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
        </Box>
    );
}