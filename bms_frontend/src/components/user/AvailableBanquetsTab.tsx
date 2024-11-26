// src/components/AvailableBanquetsTab.tsx

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
} from '@mui/material';
import * as yup from 'yup';
import { registrationSchemaForUser } from '../../utils/validationSchemas';
import { AlertColor } from '@mui/material';
import api from '../../service/api';
import { Meal, Banquet, User } from '../../utils/types';

interface AvailableBanquetsTabProps {
    showMessage: (message: string, severity?: AlertColor) => void;
    user: User;
}

const AvailableBanquetsTab: React.FC<AvailableBanquetsTabProps> = ({ showMessage, user }) => {
    const [banquets, setBanquets] = useState<Banquet[]>([]);
    const [loading, setLoading] = useState(true);

    const [selectedBanquet, setSelectedBanquet] = useState<Banquet | null>(null);
    const [openRegistrationDialog, setOpenRegistrationDialog] = useState(false);
    const [registrationData, setRegistrationData] = useState({
        drinkChoice: '',
        mealChoice: '',
        remarks: '',
    });
    const [errors, setErrors] = useState<{ [key: string]: string }>({});

    const [searchCriteria, setSearchCriteria] = useState({
        banquetName: '',
        startDate: '',
        endDate: ''
    });

    useEffect(() => {
        fetchBanquets();
    }, []);

    useEffect(() => {
        if (!searchCriteria.banquetName && !searchCriteria.startDate && !searchCriteria.endDate) {
            fetchBanquets();
        }
    }, [searchCriteria]);

    const handleApiResponse = (
        response: any,
        successCallback: (data: any) => void,
        action: string
    ) => {
        const data = response.data;
        if (data.status === 'success') {
            const banquets = data.banquets || [];
            successCallback({ ...data, banquets });
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

    const fetchBanquets = async () => {
        setLoading(true);
        try {
            const criteria = {
                banquetName: searchCriteria.banquetName || null,
                startDate: searchCriteria.startDate ? new Date(searchCriteria.startDate).toISOString() : null,
                endDate: searchCriteria.endDate ? new Date(searchCriteria.endDate).toISOString() : null
            };

            const response = await api.post('/searchAvailableBanquets', {
                attendeeEmail: user.email,
                criteria: criteria
            });

            handleApiResponse(
                response,
                (data: any) => {
                    setBanquets(data.banquets || []);
                    if (!data.banquets || data.banquets.length === 0) {
                        showMessage('No banquets found', 'info');
                    }
                },
                'fetching available banquets'
            );
        } catch (error: any) {
            handleApiError(error, 'fetching available banquets');
            setBanquets([]);
        } finally {
            setLoading(false);
        }
    };

    const handleRegisterClick = (banquet: Banquet) => {
        setSelectedBanquet(banquet);
        setRegistrationData({
            drinkChoice: '',
            mealChoice: '',
            remarks: '',
        });
        setErrors({});
        setOpenRegistrationDialog(true);
    };

    const handleRegistrationDataChange = (
        event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
        const { name, value } = event.target;
        setRegistrationData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleSelectChange = (event: SelectChangeEvent<string>) => {
        const { name, value } = event.target;
        setRegistrationData((prevData) => ({
            ...prevData,
            [name!]: value,
        }));
    };

    const handleRegistrationSubmit = async () => {
        // Reset previous errors
        setErrors({});

        // Validate registration data
        try {
            await registrationSchemaForUser.validate(registrationData, { abortEarly: false });
            // Make API call to register for banquet
            try {
                const response = await api.post('/registerForBanquet', {
                    attendeeEmail: user.email,
                    banquetBIN: selectedBanquet!.BIN,
                    ...registrationData,
                });

                handleApiResponse(
                    response,
                    (data: any) => {
                        const registrationResult = data.registrationResult;
                        if (registrationResult.success) {
                            setOpenRegistrationDialog(false);
                            fetchBanquets(); // Refresh the list of banquets
                            showMessage(registrationResult.message, 'success');
                        } else {
                            showMessage(registrationResult.message || 'Registration failed', 'error');
                        }
                    },
                    'registering for banquet'
                );
            } catch (error) {
                handleApiError(error, 'registering for banquet');
            }
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
                console.error('Validation error:', error);
                showMessage('Validation error occurred', 'error');
            }
        }
    };

    const handleSearchCriteriaChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setSearchCriteria((prevCriteria) => ({
            ...prevCriteria,
            [name]: value,
        }));
    };

    const handleClearSearch = () => {
        setSearchCriteria({
            banquetName: '',
            startDate: '',
            endDate: ''
        });
    };

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
                <Box sx={{ mt: 2, display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        onClick={fetchBanquets}
                        disabled={loading}
                    >
                        {loading ? 'Searching...' : 'Search'}
                    </Button>
                    <Button
                        variant="outlined"
                        onClick={handleClearSearch}
                        disabled={loading}
                    >
                        Clear Search
                    </Button>
                </Box>
            </Box>

            <Grid container spacing={2}>
                {loading ? (
                    <Grid item xs={12}>
                        <Typography>Loading...</Typography>
                    </Grid>
                ) : (!banquets || banquets.length === 0) ? (
                    <Grid item xs={12}>
                        <Typography>No available banquets found.</Typography>
                    </Grid>
                ) : (
                    banquets.map((banquet) => (
                        <Grid item xs={12} md={6} lg={4} key={banquet.BIN}>
                            <Card sx={{ 
                                height: '100%',
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'space-between'
                            }}>
                                <CardContent>
                                    <Typography variant="h6">{banquet.name}</Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Date and Time: {banquet.dateTime}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Location: {banquet.location}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Address: {banquet.address}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Contact: {banquet.contactFirstName} {banquet.contactLastName}
                                    </Typography>
                                </CardContent>
                                <CardActions>
                                    {banquet.available ? (
                                        <Button size="small" onClick={() => handleRegisterClick(banquet)}>
                                            Register
                                        </Button>
                                    ) : (
                                        <Button size="small" disabled>
                                            Full
                                        </Button>
                                    )}
                                </CardActions>
                            </Card>
                        </Grid>
                    ))
                )}
            </Grid>

            {selectedBanquet && (
                <Dialog
                    open={openRegistrationDialog}
                    onClose={() => setOpenRegistrationDialog(false)}
                    maxWidth="sm"
                    fullWidth
                >
                    <DialogTitle>Register for {selectedBanquet.name}</DialogTitle>
                    <DialogContent>
                        <TextField
                            name="drinkChoice"
                            label="Drink Choice"
                            value={registrationData.drinkChoice}
                            onChange={handleRegistrationDataChange}
                            fullWidth
                            required
                            margin="normal"
                            error={!!errors.drinkChoice}
                            helperText={errors.drinkChoice}
                        />
                        <FormControl
                            fullWidth
                            required
                            margin="normal"
                            error={!!errors.mealChoice}
                        >
                            <InputLabel id="mealChoice-label">Meal Choice</InputLabel>
                            <Select
                                labelId="mealChoice-label"
                                name="mealChoice"
                                value={registrationData.mealChoice}
                                onChange={handleSelectChange}
                                label="Meal Choice"
                            >
                                {selectedBanquet.meals.map((meal, index) => (
                                    <MenuItem key={index} value={meal.dishName}>
                                        {meal.dishName} ({meal.type}) - ${meal.price}
                                    </MenuItem>
                                ))}
                            </Select>
                            {errors.mealChoice && (
                                <Typography variant="caption" color="error">
                                    {errors.mealChoice}
                                </Typography>
                            )}
                        </FormControl>
                        <TextField
                            name="remarks"
                            label="Remarks"
                            value={registrationData.remarks}
                            onChange={handleRegistrationDataChange}
                            fullWidth
                            multiline
                            rows={3}
                            margin="normal"
                            error={!!errors.remarks}
                            helperText={errors.remarks}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenRegistrationDialog(false)}>Cancel</Button>
                        <Button onClick={handleRegistrationSubmit} variant="contained">
                            Submit
                        </Button>
                    </DialogActions>
                </Dialog>
            )}
        </Box>
    );
};

export default AvailableBanquetsTab;