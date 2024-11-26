'use client';

import React, { useState, useContext, useEffect } from 'react';
import { Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TextField, Button, 
    Stack, SelectChangeEvent, FormControl, InputLabel, Select, MenuItem, Dialog, DialogTitle, DialogContent, DialogActions, DialogContentText, FormHelperText} from '@mui/material';
import { Registration, Banquet, Meal } from '../../../utils/types';
import BanquetSearch from './BanquetSearch';
import * as Yup from 'yup';
import { SnackbarContext } from '../../../context/SnackbarContext';
import api from '../../../service/api';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { registrationSchemaForAdmin } from '../../../utils/validationSchemas';


// Extend Registration interface with meals
interface RegistrationWithMeals extends Registration {
    meals?: Meal[];
}

export default function RegistrationManagement() {
    const { showMessage } = useContext(SnackbarContext);
    const [banquet, setBanquet] = useState<Banquet | null>(null);
    const [registrations, setRegistrations] = useState<RegistrationWithMeals[]>([]);
    const [loading, setLoading] = useState(false);
    const [searchError, setSearchError] = useState<string>('');
    
    // Edit state management
    const [selectedRegistration, setSelectedRegistration] = useState<RegistrationWithMeals | null>(null);
    const [openEditDialog, setOpenEditDialog] = useState(false);
    const [updateData, setUpdateData] = useState({
        newSeatNo: '',
        newDrinkChoice: '',
        newMealChoice: '',
        newRemarks: ''
    });
    const [errors, setErrors] = useState<{ [key: string]: string }>({});

    // Delete confirmation dialog state
    const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
    const [registrationToDelete, setRegistrationToDelete] = useState<Registration | null>(null);

    // Add statistics calculation
    const calculateStats = () => {
        const drinkStats = Object.entries(
            registrations.reduce((acc: { [key: string]: number }, reg) => {
                const drink = reg.drinkChoice || 'Not Specified';
                acc[drink] = (acc[drink] || 0) + 1;
                return acc;
            }, {})
        ).map(([name, value]) => ({ name, value }));

        const mealStats = Object.entries(
            registrations.reduce((acc: { [key: string]: number }, reg) => {
                const meal = reg.mealChoice || 'Not Specified';
                acc[meal] = (acc[meal] || 0) + 1;
                return acc;
            }, {})
        ).map(([name, value]) => ({ name, value }));

        return { drinkStats, mealStats };
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
        if (error.response?.data?.message) {
            message = `Error ${action}: ${error.response.data.message}`;
        } else {
            message = `Error ${action}: ${error.message}`;
        }
        showMessage(message, 'error');
    };

    const handleSearch = async (BIN: number) => {
        setLoading(true);
        setSearchError('');
        setBanquet(null);
        setRegistrations([]);
        try {
            const banquetResponse = await api.get('/getBanquetByBIN', { params: { banquetBIN: BIN } });
            const registrationsResponse = await api.get('/getReservationsByBIN', { params: { banquetBIN: BIN } });

            handleApiResponse(
                banquetResponse,
                (data) => {
                    setBanquet(data.banquet);
                },
                'fetching banquet'
            );

            handleApiResponse(
                registrationsResponse,
                (data) => {
                    if (Array.isArray(data.registrations)) {
                        const registrationsWithMeals = data.registrations.map((registration: Registration) => ({
                            ...registration,
                            mealChoice: registration.mealChoice || 'Not Specified',
                            drinkChoice: registration.drinkChoice || 'Not Specified',
                            meals: banquetResponse.data.banquet.meals
                        }));
                        setRegistrations(registrationsWithMeals);
                    } else {
                        setRegistrations([]);
                    }
                },
                'fetching registrations'
            );
        } catch (error) {
            handleApiError(error, 'fetching data');
        }
        setLoading(false);
    };

    const handleEdit = async (registration: RegistrationWithMeals) => {
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
                    newSeatNo: registration.seatNo.toString(),
                    newDrinkChoice: registration.drinkChoice || '',
                    newMealChoice: registration.mealChoice || '',
                    newRemarks: registration.remarks || ''
                });
                setErrors({});
                setOpenEditDialog(true);
            } else {
                showMessage('Failed to fetch banquet details', 'error');
            }
        } catch (error: any) {
            handleApiError(error, 'fetching banquet details');
        }
    };

    const handleTextFieldChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = event.target;
        setUpdateData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSelectChange = (event: SelectChangeEvent<string>) => {
        const { name, value } = event.target;
        setUpdateData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSave = async () => {
        if (!selectedRegistration) {
            return;
        }

        try {
            const registrationData = {
                ...selectedRegistration,
                seatNo: Number(updateData.newSeatNo),
                drinkChoice: updateData.newDrinkChoice,
                mealChoice: updateData.newMealChoice,
                remarks: updateData.newRemarks
            };

            // Validate the data
            await registrationSchemaForAdmin.validate(registrationData, { abortEarly: false });

            const response = await api.post('/updateAttendeeRegistrationData', {
                registrationData: registrationData
            });

            handleApiResponse(
                response,
                () => {
                    showMessage('Registration updated successfully', 'success');
                    setOpenEditDialog(false);
                    handleSearch(banquet!.BIN);
                },
                'updating registration'
            );
        } catch (error) {
            if (error instanceof Yup.ValidationError) {
                const validationErrors: { [key: string]: string } = {};
                error.inner.forEach((err) => {
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

    // Handle delete button click
    const handleDeleteClick = (registration: Registration) => {
        setRegistrationToDelete(registration);
        setOpenDeleteDialog(true);
    };

    // Handle confirmation delete
    const handleDelete = async () => {
        if (!registrationToDelete) return;

        try {
            const response = await api.post('/deleteReserve', {
                attendeeEmail: registrationToDelete.attendeeEmail,
                banquetBIN: registrationToDelete.banquetBIN,
            });

            handleApiResponse(
                response,
                () => {
                    setRegistrations(registrations.filter(r => 
                        !(r.attendeeEmail === registrationToDelete.attendeeEmail && 
                          r.banquetBIN === registrationToDelete.banquetBIN)
                    ));
                    showMessage('Registration deleted successfully', 'success');
                    setOpenDeleteDialog(false);
                },
                'deleting registration'
            );
        } catch (error) {
            handleApiError(error, 'deleting registration');
        }
    };

    return (
        <Box sx={{ mt: 2 }}>
            <BanquetSearch
                onSearch={handleSearch}
                loading={loading}
                errorMessage={searchError}
            />

            {registrations && registrations.length > 0 && (
                <>
                    {/* Statistics Charts */}
                    <Box sx={{ mt: 4, mb: 4, display: 'flex', gap: 4 }}>
                        <Paper sx={{ p: 2, flex: 1, height: 300 }}>
                            <Typography variant="h6" gutterBottom align="center">
                                Drink Choice Statistics
                            </Typography>
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart
                                    data={calculateStats().drinkStats}
                                    margin={{ top: 20, right: 30, left: 20, bottom: 70 }}
                                >
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis 
                                        dataKey="name" 
                                        angle={-45} 
                                        textAnchor="end" 
                                        height={70} 
                                        interval={0}
                                    />
                                    <YAxis />
                                    <Tooltip />
                                    <Bar dataKey="value" fill="#8884d8" name="Count" />
                                </BarChart>
                            </ResponsiveContainer>
                        </Paper>
                        <Paper sx={{ p: 2, flex: 1, height: 300 }}>
                            <Typography variant="h6" gutterBottom align="center">
                                Meal Choice Statistics
                            </Typography>
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart
                                    data={calculateStats().mealStats}
                                    margin={{ top: 20, right: 30, left: 20, bottom: 70 }}
                                >
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis 
                                        dataKey="name" 
                                        angle={-45} 
                                        textAnchor="end" 
                                        height={70} 
                                        interval={0}
                                    />
                                    <YAxis />
                                    <Tooltip />
                                    <Bar dataKey="value" fill="#82ca9d" name="Count" />
                                </BarChart>
                            </ResponsiveContainer>
                        </Paper>
                    </Box>

                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>Email</TableCell>
                                    <TableCell>Seat No</TableCell>
                                    <TableCell>Registration Time</TableCell>
                                    <TableCell>Drink Choice</TableCell>
                                    <TableCell>Meal Choice</TableCell>
                                    <TableCell>Remarks</TableCell>
                                    <TableCell>Actions</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {registrations.map((registration, index) => (
                                    <TableRow key={index}>
                                        <TableCell>{registration.attendeeEmail}</TableCell>
                                        <TableCell>{registration.seatNo}</TableCell>
                                        <TableCell>{new Date(registration.regTime).toLocaleString()}</TableCell>
                                        <TableCell>{registration.drinkChoice}</TableCell>
                                        <TableCell>{registration.mealChoice}</TableCell>
                                        <TableCell>{registration.remarks}</TableCell>
                                        <TableCell>
                                            <Stack direction="row" spacing={1}>
                                                <Button 
                                                    size="small" 
                                                    variant="contained" 
                                                    onClick={() => handleEdit(registration)}
                                                >
                                                    Edit
                                                </Button>
                                                <Button 
                                                    size="small" 
                                                    variant="contained" 
                                                    color="error"
                                                    onClick={() => handleDeleteClick(registration)}
                                                >
                                                    Delete
                                                </Button>
                                            </Stack>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </>
            )}

            {/* Edit dialog */}
            {selectedRegistration && (
                <Dialog
                    open={openEditDialog}
                    onClose={() => setOpenEditDialog(false)}
                    maxWidth="sm"
                    fullWidth
                >
                    <DialogTitle>Edit Registration</DialogTitle>
                    <DialogContent>
                        <TextField
                            name="newSeatNo"
                            label="Seat Number"
                            value={updateData.newSeatNo}
                            onChange={handleTextFieldChange}
                            fullWidth
                            required
                            margin="normal"
                            error={!!errors.seatNo}
                            helperText={errors.seatNo}
                        />
                        <TextField
                            name="newDrinkChoice"
                            label="Drink Choice"
                            value={updateData.newDrinkChoice}
                            onChange={handleTextFieldChange}
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
                            <InputLabel id="newMealChoice-label">Meal Choice</InputLabel>
                            <Select
                                labelId="newMealChoice-label"
                                name="newMealChoice"
                                value={updateData.newMealChoice}
                                onChange={handleSelectChange}
                                label="Meal Choice"
                            >
                                {banquet?.meals?.map((meal, index) => (
                                    <MenuItem key={index} value={meal.dishName}>
                                        {meal.dishName} ({meal.type}) - ${meal.price}
                                    </MenuItem>
                                ))}
                            </Select>
                            {errors.mealChoice && (
                                <FormHelperText error>
                                    {errors.mealChoice}
                                </FormHelperText>
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
                            error={!!errors.remarks}
                            helperText={errors.remarks}
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenEditDialog(false)}>Cancel</Button>
                        <Button onClick={handleSave} variant="contained">
                            Save
                        </Button>
                    </DialogActions>
                </Dialog>
            )}

            {/* Delete confirmation dialog */}
            <Dialog
                open={openDeleteDialog}
                onClose={() => setOpenDeleteDialog(false)}
            >
                <DialogTitle>Confirm Deletion</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete the registration for{' '}
                        {registrationToDelete?.attendeeEmail}? This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDeleteDialog(false)}>Cancel</Button>
                    <Button 
                        onClick={handleDelete} 
                        color="error" 
                        variant="contained"
                    >
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
