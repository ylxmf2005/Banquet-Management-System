// src/components/MyRegistrationsTab.tsx

import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Button,
    Grid,
    Card,
    CardContent,
    CardActions,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    SelectChangeEvent,
    DialogContentText
} from '@mui/material';
import * as yup from 'yup';
import { AlertColor } from '@mui/material';
import api from '../../service/api';
import { Meal, Banquet, User } from '../../utils/types';

interface MyRegistrationsTabProps {
    showMessage: (message: string, severity?: AlertColor) => void;
    user: User;
}

interface Registration {
    banquetBIN: number;
    banquetName?: string;
    dateTime: string;
    regTime: string;
    seatNo: string;
    drinkChoice: string;
    mealChoice: string;
    remarks: string;
}

interface RegistrationWithBanquet extends Registration {
    banquetDateTime?: string;
    location?: string;
    address?: string;
    contactFirstName?: string;
    contactLastName?: string;
    meals?: Meal[];
}

const MyRegistrationsTab: React.FC<MyRegistrationsTabProps> = ({ showMessage, user }) => {
    const [searchCriteria, setSearchCriteria] = useState({
        banquetName: '',
        startDate: '',
        endDate: ''
    });
    const [registrations, setRegistrations] = useState<RegistrationWithBanquet[]>([]);
    const [loading, setLoading] = useState(false);

    const [selectedRegistration, setSelectedRegistration] = useState<RegistrationWithBanquet | null>(null);
    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);
    const [updateData, setUpdateData] = useState({
        newDrinkChoice: '',
        newMealChoice: '',
        newRemarks: '',
    });
    const [errors, setErrors] = useState<{ [key: string]: string }>({});
    const [openUnregisterDialog, setOpenUnregisterDialog] = useState(false);
    const [registrationToUnregister, setRegistrationToUnregister] = useState<Registration | null>(null);

    const handleSearchCriteriaChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setSearchCriteria((prevCriteria) => ({
            ...prevCriteria,
            [name]: value,
        }));
    };

    const fetchBanquetDetails = async (registration: Registration): Promise<RegistrationWithBanquet> => {
        try {
            const response = await api.get('/getBanquetByBIN', {
                params: { banquetBIN: registration.banquetBIN }
            });
            
            if (response.data.status === 'success') {
                const banquet = response.data.banquet;
                return {
                    ...registration,
                    banquetName: banquet.name,
                    banquetDateTime: banquet.dateTime,
                    location: banquet.location,
                    address: banquet.address,
                    contactFirstName: banquet.contactFirstName,
                    contactLastName: banquet.contactLastName,
                    meals: banquet.meals
                };
            }
            throw new Error(response.data.message || 'Failed to fetch banquet details');
        } catch (error) {
            console.error('Error fetching banquet details:', error);
            return registration;
        }
    };

    const handleSearch = async () => {
        setLoading(true);
        try {
            const criteria = {
                banquetName: searchCriteria.banquetName || null,
                startDate: searchCriteria.startDate ? new Date(searchCriteria.startDate).toISOString() : null,
                endDate: searchCriteria.endDate ? new Date(searchCriteria.endDate).toISOString() : null
            };

            const response = await api.post('/searchRegistrations', {
                attendeeEmail: user.email,
                criteria: criteria
            });

            if (response.data.status === 'success') {
                const registrations = response.data.registrations as Registration[];
                
                // Fetch banquet details for each registration
                const registrationsWithBanquets = await Promise.all(
                    registrations.map(fetchBanquetDetails)
                );
                
                setRegistrations(registrationsWithBanquets);
                if (registrationsWithBanquets.length === 0) {
                    showMessage('No registrations found', 'info');
                }
            } else {
                showMessage(response.data.message || 'Failed to search registrations', 'error');
            }
        } catch (error: any) {
            handleApiError(error, 'searching registrations');
        } finally {
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
            console.error(message);
            showMessage(message, 'error');
        }
    };

    const handleApiError = (error: any, action: string) => {
        let message = '';
        if (error.response && error.response.data && error.response.data.message) {
            console.error(`Error ${action}:`, error.response.data.message);
            message = `Error ${action}: ${error.response.data.message}`;
        } else {
            console.error(`Error ${action}:`, error.message);
            message = `Error ${action}: ${error.message}`;
        }
        showMessage(message, 'error');
    };

    const handleUpdateClick = async (registration: Registration) => {
        try {
            const response = await api.get('/getBanquetByBIN', {
                params: { banquetBIN: registration.banquetBIN }
            });
            
            if (response.data.status === 'success') {
                const banquet = response.data.banquet;
                setSelectedRegistration({
                    ...registration,
                    meals: banquet.meals
                });
                setUpdateData({
                    newDrinkChoice: registration.drinkChoice || '',
                    newMealChoice: registration.mealChoice || '',
                    newRemarks: registration.remarks || '',
                });
                setErrors({});
                setOpenUpdateDialog(true);
            } else {
                showMessage('Failed to fetch banquet details', 'error');
            }
        } catch (error: any) {
            handleApiError(error, 'fetching banquet details');
        }
    };

    const handleUnregisterClick = (registration: Registration) => {
        setRegistrationToUnregister(registration);
        setOpenUnregisterDialog(true);
    };

    const handleUnregister = async () => {
        try {
            const response = await api.post('/deleteReserve', {
                attendeeEmail: user.email,
                banquetBIN: registrationToUnregister!.banquetBIN
            });

            handleApiResponse(
                response,
                () => {
                    showMessage('Successfully unregistered from the banquet', 'success');
                    setOpenUnregisterDialog(false);
                    handleSearch();
                },
                'unregistering from banquet'
            );
        } catch (error: any) {
            handleApiError(error, 'unregistering from banquet');
        }
    };

    const handleTextFieldChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = event.target;
        setUpdateData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSelectChange = (event: SelectChangeEvent<string>) => {
        const { name, value } = event.target;
        setUpdateData((prevData) => ({
            ...prevData,
            [name!]: value,
        }));
    };

    const updateRegistrationSchema = yup.object().shape({
        newDrinkChoice: yup.string().required('Drink choice is required'),
        newMealChoice: yup.string().required('Meal choice is required'),
        newRemarks: yup.string().max(500, 'Remarks cannot exceed 500 characters'),
    });

    const handleUpdateSubmit = async () => {
        setErrors({});
        try {
            await updateRegistrationSchema.validate(updateData, { abortEarly: false });
            
            const registrationData = {
                banquetBIN: selectedRegistration!.banquetBIN,
                attendeeEmail: user.email,
                drinkChoice: updateData.newDrinkChoice,
                mealChoice: updateData.newMealChoice,
                remarks: updateData.newRemarks,
                seatNo: selectedRegistration!.seatNo,
                regTime: selectedRegistration!.regTime,
                dateTime: selectedRegistration!.dateTime
            };

            const response = await api.post('/updateAttendeeRegistrationData', {
                registrationData: registrationData
            });

            handleApiResponse(
                response,
                () => {
                    showMessage('Registration updated successfully', 'success');
                    setOpenUpdateDialog(false);
                    handleSearch(); 
                },
                'updating registration'
            );
        } catch (error: any) {
            if (error instanceof yup.ValidationError) {
                const validationErrors: { [key: string]: string } = {};
                error.inner.forEach((err: any) => {
                    if (err.path) {
                        validationErrors[err.path] = err.message;
                    }
                });
                setErrors(validationErrors);
            } else {
                handleApiError(error, 'updating registration');
            }
        }
    };

    useEffect(() => {
        handleSearch();
    }, []);

    return (
        <Box sx={{ mt: 3 }}>
            <Box sx={{ mb: 2 }}>
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={4}>
                        <TextField
                            name="startDate"
                            label="Start Date & Time"
                            type="datetime-local"
                            value={searchCriteria.startDate}
                            onChange={handleSearchCriteriaChange}
                            fullWidth
                            InputLabelProps={{
                                shrink: true,
                            }}
                            inputProps={{
                                step: 60
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} sm={4}>
                        <TextField
                            name="endDate"
                            label="End Date & Time"
                            type="datetime-local"
                            value={searchCriteria.endDate}
                            onChange={handleSearchCriteriaChange}
                            fullWidth
                            InputLabelProps={{
                                shrink: true,
                            }}
                            inputProps={{
                                step: 60
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} sm={4}>
                        <TextField
                            name="banquetName"
                            label="Banquet Name"
                            value={searchCriteria.banquetName}
                            onChange={handleSearchCriteriaChange}
                            fullWidth
                        />
                    </Grid>
                </Grid>
                <Button
                    variant="contained"
                    sx={{ mt: 2 }}
                    onClick={handleSearch}
                    disabled={loading}
                >
                    {loading ? 'Searching...' : 'Search'}
                </Button>
            </Box>

            <Grid container spacing={2}>
                {loading ? (
                    <Grid item xs={12}>
                        <Typography>Loading...</Typography>
                    </Grid>
                ) : (!registrations || registrations.length === 0) ? (
                    <Grid item xs={12}>
                        <Typography>No registrations found.</Typography>
                    </Grid>
                ) : (
                    registrations.map((registration, index) => (
                        <Grid item xs={12} md={6} lg={4} key={index}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6">{registration.banquetName || 'Unknown Banquet'}</Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Date and Time: {registration.banquetDateTime || 'Unknown'}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Location: {registration.location || 'Unknown'}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Address: {registration.address || 'Unknown'}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Contact: {registration.contactFirstName} {registration.contactLastName}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary" sx={{ mt: 1 }}>
                                        Registration Time: {registration.regTime}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Seat No: {registration.seatNo}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Drink Choice: {registration.drinkChoice}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Meal Choice: {registration.mealChoice}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Remarks: {registration.remarks}
                                    </Typography>
                                </CardContent>
                                <CardActions>
                                    <Button size="small" onClick={() => handleUpdateClick(registration)}>
                                        Update
                                    </Button>
                                    <Button 
                                        size="small" 
                                        color="error" 
                                        onClick={() => handleUnregisterClick(registration)}
                                    >
                                        Unregister
                                    </Button>
                                </CardActions>
                            </Card>
                        </Grid>
                    ))
                )}
            </Grid>

            {selectedRegistration && (
                <Dialog
                    open={openUpdateDialog}
                    onClose={() => setOpenUpdateDialog(false)}
                    maxWidth="sm"
                    fullWidth
                >
                    <DialogTitle>Update Registration for {selectedRegistration.banquetName}</DialogTitle>
                    <DialogContent>
                        <TextField
                            name="newDrinkChoice"
                            label="Drink Choice"
                            value={updateData.newDrinkChoice}
                            onChange={handleTextFieldChange}
                            fullWidth
                            required
                            margin="normal"
                            error={!!errors.newDrinkChoice}
                            helperText={errors.newDrinkChoice}
                        />
                        <FormControl
                            fullWidth
                            required
                            margin="normal"
                            error={!!errors.newMealChoice}
                        >
                            <InputLabel id="newMealChoice-label">Meal Choice</InputLabel>
                            <Select
                                labelId="newMealChoice-label"
                                name="newMealChoice"
                                defaultValue={selectedRegistration.mealChoice}
                                value={updateData.newMealChoice}
                                onChange={handleSelectChange}
                                label="Meal Choice"
                            >
                                {selectedRegistration.meals?.map((meal, index) => (
                                    <MenuItem key={index} value={meal.dishName}>
                                        {meal.dishName} ({meal.type}) - ${meal.price}
                                    </MenuItem>
                                ))}
                            </Select>
                            {errors.newMealChoice && (
                                <Typography variant="caption" color="error">
                                    {errors.newMealChoice}
                                </Typography>
                            )}
                        </FormControl>
                        <TextField
                            name="newRemarks"
                            label="Remarks"
                            value={updateData.newRemarks}
                            onChange={handleTextFieldChange}
                            fullWidth
                            multiline
                            rows={3}
                            margin="normal"
                            error={!!errors.newRemarks}
                            helperText={errors.newRemarks}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenUpdateDialog(false)}>Cancel</Button>
                        <Button onClick={handleUpdateSubmit} variant="contained">
                            Submit
                        </Button>
                    </DialogActions>
                </Dialog>
            )}

            {registrationToUnregister && (
                <Dialog
                    open={openUnregisterDialog}
                    onClose={() => setOpenUnregisterDialog(false)}
                >
                    <DialogTitle>Confirm Unregistration</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            Are you sure you want to unregister from {registrationToUnregister.banquetName}? This action cannot be undone.
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenUnregisterDialog(false)}>Cancel</Button>
                        <Button onClick={handleUnregister} color="error" variant="contained">
                            Unregister
                        </Button>
                    </DialogActions>
                </Dialog>
            )}
        </Box>
    );
};

export default MyRegistrationsTab;