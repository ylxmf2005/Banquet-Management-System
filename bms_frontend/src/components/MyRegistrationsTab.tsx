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
} from '@mui/material';
import { AlertColor } from '@mui/material';
import api from '../service/api';

interface MyRegistrationsTabProps {
    showMessage: (message: string, severity?: AlertColor) => void;
    user: User;
}

interface Registration {
    banquetBIN: number;
    banquetName: string;
    dateTime: string;
    seatNo: string;
    drinkChoice: string;
    mealChoice: string;
    remarks: string;
}

interface User {
    email: string;
    role: 'admin' | 'user';
}

const MyRegistrationsTab: React.FC<MyRegistrationsTabProps> = ({ showMessage, user }) => {
    const [searchCriteria, setSearchCriteria] = useState({
        banquetName: '',
        startDate: '',
        endDate: ''
    });
    const [registrations, setRegistrations] = useState<Registration[]>([]);
    const [loading, setLoading] = useState(false);

    const [selectedRegistration, setSelectedRegistration] = useState<Registration | null>(null);
    const [openUpdateDialog, setOpenUpdateDialog] = useState(false);
    const [updateData, setUpdateData] = useState({
        newDrinkChoice: '',
        newMealChoice: '',
        newRemarks: '',
    });

    const handleSearchCriteriaChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setSearchCriteria((prevCriteria) => ({
            ...prevCriteria,
            [name]: value,
        }));
    };

    const handleSearch = async () => {
        setLoading(true);
        try {
            const criteria = {
                banquetName: searchCriteria.banquetName,
                startDate: searchCriteria.startDate ? new Date(searchCriteria.startDate).getTime() : null,
                endDate: searchCriteria.endDate ? new Date(searchCriteria.endDate).getTime() : null
            };

            const response = await api.get('/searchRegisteredBanquets', {
                params: {
                    attendeeEmail: user.email,
                    ...criteria,
                },
            });

            handleApiResponse(
                response,
                (data: any) => {
                    const registrations = data.registrations as Registration[];
                    setRegistrations(registrations);
                    setLoading(false);
                },
                'searching registrations'
            );
        } catch (error) {
            handleApiError(error, 'searching registrations');
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

    const handleUpdateClick = (registration: Registration) => {
        setSelectedRegistration(registration);
        setUpdateData({
            newDrinkChoice: registration.drinkChoice,
            newMealChoice: registration.mealChoice,
            newRemarks: registration.remarks,
        });
        setOpenUpdateDialog(true);
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

    const handleUpdateSubmit = async () => {
        try {
            const response = await api.post('/updateRegistration', {
                attendeeEmail: user.email,
                banquetBIN: selectedRegistration!.banquetBIN,
                ...updateData,
            });

            handleApiResponse(
                response,
                () => {
                    showMessage('Registration updated successfully', 'success');
                    setOpenUpdateDialog(false);
                    handleSearch(); // Refresh the list
                },
                'updating registration'
            );
        } catch (error) {
            handleApiError(error, 'updating registration');
        }
    };

    const formatDateTime = (date: Date) => {
        return date.toISOString().slice(0, 16); 
    };

    useEffect(() => {
        const now = new Date();
        const thirtyDaysLater = new Date();
        thirtyDaysLater.setDate(now.getDate() + 30);
        
        setSearchCriteria({
            banquetName: '',
            startDate: formatDateTime(now),
            endDate: formatDateTime(thirtyDaysLater)
        });
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
                {registrations.map((registration, index) => (
                    <Grid item xs={12} key={index}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6">{registration.banquetName}</Typography>
                                <Typography variant="body2" color="textSecondary">
                                    Date and Time: {registration.dateTime}
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
                            </CardActions>
                        </Card>
                    </Grid>
                ))}
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
                        <FormControl fullWidth required margin="normal">
                            <InputLabel id="newDrinkChoice-label">Drink Choice</InputLabel>
                            <Select
                                labelId="newDrinkChoice-label"
                                name="newDrinkChoice"
                                value={updateData.newDrinkChoice}
                                onChange={handleSelectChange}
                                label="Drink Choice"
                            >
                                <MenuItem value="tea">Tea</MenuItem>
                                <MenuItem value="coffee">Coffee</MenuItem>
                                <MenuItem value="lemon tea">Lemon Tea</MenuItem>
                            </Select>
                        </FormControl>
                        <FormControl fullWidth required margin="normal">
                            <InputLabel id="newMealChoice-label">Meal Choice</InputLabel>
                            <Select
                                labelId="newMealChoice-label"
                                name="newMealChoice"
                                value={updateData.newMealChoice}
                                onChange={handleSelectChange}
                                label="Meal Choice"
                            >
                                <MenuItem value="Fish">Fish</MenuItem>
                                <MenuItem value="Chicken">Chicken</MenuItem>
                                <MenuItem value="Beef">Beef</MenuItem>
                                <MenuItem value="Vegetarian">Vegetarian</MenuItem>
                            </Select>
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
        </Box>
    );
};

export default MyRegistrationsTab;