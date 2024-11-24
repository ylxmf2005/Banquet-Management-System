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
import { registrationSchemaForUser } from '../utils/validationSchemas'
import { AlertColor } from '@mui/material';
import api from '../service/api';
import { Meal, Banquet, User } from '../utils/types';

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
        // seatNo: '',  // 移除 seatNo 字段
        drinkChoice: '',
        mealChoice: '',
        remarks: '',
    });
    const [errors, setErrors] = useState<{ [key: string]: string }>({});

    useEffect(() => {
        fetchBanquets();
    }, []);

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

    const fetchBanquets = async () => {
        setLoading(true);
        try {
            const response = await api.get('/getAvailableBanquets');

            handleApiResponse(
                response,
                (data: any) => {
                    const banquets = data.banquets as Banquet[];
                    setBanquets(banquets);
                    setLoading(false);
                },
                'fetching available banquets',
            );
        } catch (error: any) {
            handleApiError(error, 'fetching available banquets');
            setLoading(false);
        }
    };

    const handleRegisterClick = (banquet: Banquet) => {
        setSelectedBanquet(banquet);
        setRegistrationData({
            // seatNo: '',  // 移除 seatNo 字段
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
                    () => {
                        setOpenRegistrationDialog(false);
                        fetchBanquets(); // Refresh the list of banquets
                    },
                    'registering for banquet',
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

    return (
        <Box sx={{ mt: 3 }}>
            <Typography variant="h6" gutterBottom>
                Available Banquets
            </Typography>
            <Grid container spacing={2}>
                {banquets.map((banquet) => (
                    <Grid item xs={12} md={6} lg={4} key={banquet.BIN}>
                        <Card>
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
                ))}
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
                                        {meal.dishName} ({meal.type})
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