'use client';

import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    Typography,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    FormHelperText,
    Stack,
    Snackbar,
    Alert,
} from '@mui/material';
import { DataGrid, GridColDef, GridRenderCellParams } from '@mui/x-data-grid';
import api from '../utils/api';

// Interface for Meal object
interface Meal {
    type: string;
    dishName: string;
    price: number;
    specialCuisine: string;
}

// Interface for Banquet object
interface Banquet {
    BIN: number;
    name: string;
    dateTime: string;
    address: string;
    location: string;
    contactFirstName: string;
    contactLastName: string;
    available: string;
    quota: number;
    meals: Meal[];
}

export default function BanquetManagement() {
    const [banquets, setBanquets] = useState<Banquet[]>([]);
    const [loading, setLoading] = useState(true);

    const [openDialog, setOpenDialog] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [selectedBanquet, setSelectedBanquet] = useState<Banquet>({
        BIN: 0,
        name: '',
        dateTime: '',
        address: '',
        location: '',
        contactFirstName: '',
        contactLastName: '',
        available: 'Y',
        quota: 0,
        meals: [
            { type: '', dishName: '', price: NaN, specialCuisine: '' },
            { type: '', dishName: '', price: NaN, specialCuisine: '' },
            { type: '', dishName: '', price: NaN, specialCuisine: '' },
            { type: '', dishName: '', price: NaN, specialCuisine: '' },
        ],
    });

    // State to hold validation errors, including meals errors
    const [errors, setErrors] = useState<{ [key: string]: any }>({});

    // Snackbar state
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarSeverity, setSnackbarSeverity] = useState<'success' | 'error'>('success');

    // Function to handle closing of the snackbar
    const handleSnackbarClose = (event?: React.SyntheticEvent | Event, reason?: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setSnackbarOpen(false);
    };

    // Fetch banquets when component mounts
    useEffect(() => {
        fetchBanquets();
    }, []);

    // Function to fetch banquets from the backend
    const fetchBanquets = async () => {
        setLoading(true);
        try {
            const response = await api.get('/getAllBanquets');
            let fetchedBanquets: Banquet[] = response.data.banquets || [];

            // Map over fetched banquets and ensure meals are properly set
            fetchedBanquets = fetchedBanquets.map((banquet) => ({
                ...banquet,
                meals:
                    banquet.meals && banquet.meals.length > 0
                        ? banquet.meals
                        : [
                            { type: '', dishName: '', price: NaN, specialCuisine: '' },
                            { type: '', dishName: '', price: NaN, specialCuisine: '' },
                            { type: '', dishName: '', price: NaN, specialCuisine: '' },
                            { type: '', dishName: '', price: NaN, specialCuisine: '' },
                        ],
            }));

            setBanquets(fetchedBanquets);

            // After fetching data, calculate column widths
            calculateColumnWidths(fetchedBanquets);
        } catch (error: any) {
            handleApiError(error, 'fetching banquets');
        }
        setLoading(false);
    };

    // State to hold column definitions
    const [columns, setColumns] = useState<GridColDef[]>([]);

    // Function to calculate column widths based on content
    const calculateColumnWidths = (data: Banquet[]) => {
        const headers = [
            { field: 'BIN', headerName: 'BIN' },
            { field: 'name', headerName: 'Banquet Name' },
            { field: 'dateTime', headerName: 'Date & Time' },
            { field: 'address', headerName: 'Address' },
            { field: 'location', headerName: 'Location' },
            { field: 'contactFirstName', headerName: 'Contact First Name' },
            { field: 'contactLastName', headerName: 'Contact Last Name' },
            { field: 'available', headerName: 'Available' },
            { field: 'quota', headerName: 'Quota' },
            { field: 'actions', headerName: 'Actions' },
        ];

        const ctx = document.createElement('canvas').getContext('2d');

        if (!ctx) return;

        const font = '14px Roboto';
        ctx.font = font;

        const getTextWidth = (text: string) => {
            return ctx.measureText(text).width;
        };

        const calculatedColumns = headers.map((header) => {
            let maxWidth = getTextWidth(header.headerName) + 40; // Adding padding
            if (header.field !== 'actions') {
                data.forEach((row) => {
                    // @ts-ignore
                    const value = row[header.field as keyof Banquet];
                    const text = value ? value.toString() : '';
                    const width = getTextWidth(text) + 40; // Adding padding
                    if (width > maxWidth) {
                        maxWidth = width;
                    }
                });
            } else {
                // Actions column
                maxWidth = 180;
            }

            return {
                field: header.field,
                headerName: header.headerName,
                width: Math.min(maxWidth, 400), // Set a max width to prevent excessively wide columns
                renderCell:
                    header.field === 'actions'
                        ? (params: GridRenderCellParams) => (
                            <div>
                                <Button
                                    size="small"
                                    variant="outlined"
                                    onClick={() => handleEditBanquet(params.row as Banquet)}
                                >
                                    Edit
                                </Button>
                                <Button
                                    size="small"
                                    variant="outlined"
                                    color="error"
                                    onClick={() => handleDeleteBanquet((params.row as Banquet).BIN)}
                                    sx={{ ml: 1 }}
                                >
                                    Delete
                                </Button>
                            </div>
                        )
                        : undefined,
            } as GridColDef;
        });

        setColumns(calculatedColumns);
    };

    // Open the dialog to create a new banquet
    const handleCreateBanquet = () => {
        setSelectedBanquet({
            BIN: 0,
            name: '',
            dateTime: '',
            address: '',
            location: '',
            contactFirstName: '',
            contactLastName: '',
            available: 'Y',
            quota: 0,
            meals: [
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
            ],
        });
        setErrors({});
        setIsEditing(false);
        setOpenDialog(true);
    };

    // Helper function to format dateTime for input field
    const formatDateTimeForInput = (dateTimeStr: string): string => {
        const date = new Date(dateTimeStr);
        // Check if date is valid
        if (isNaN(date.getTime())) {
            return '';
        }
        // Format date to 'YYYY-MM-DDTHH:mm'
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2); // Months are zero-based
        const day = ('0' + date.getDate()).slice(-2);
        const hours = ('0' + date.getHours()).slice(-2);
        const minutes = ('0' + date.getMinutes()).slice(-2);
        return `${year}-${month}-${day}T${hours}:${minutes}`;
    };

    // Open the dialog to edit an existing banquet
    const handleEditBanquet = (banquet: Banquet) => {
        // Ensure meals are properly set in selectedBanquet
        setSelectedBanquet({
            ...banquet,
            // Format the dateTime to "YYYY-MM-DDTHH:mm" format
            dateTime: formatDateTimeForInput(banquet.dateTime),
            meals:
                banquet.meals && banquet.meals.length > 0
                    ? banquet.meals
                    : [
                        { type: '', dishName: '', price: NaN, specialCuisine: '' },
                        { type: '', dishName: '', price: NaN, specialCuisine: '' },
                        { type: '', dishName: '', price: NaN, specialCuisine: '' },
                        { type: '', dishName: '', price: NaN, specialCuisine: '' },
                    ],
        });
        setErrors({});
        setIsEditing(true);
        setOpenDialog(true);
    };

    // Function to handle API errors and display messages
    const handleApiError = (error: any, action: string) => {
        if (error.response && error.response.data && error.response.data.message) {
            console.log(`Error ${action}:`, error.response.data.message);
            setSnackbarMessage(`Error ${action}: ${error.response.data.message}`);
        } else {
            console.log(`Error ${action}:`, error.message);
            setSnackbarMessage(`Error ${action}: ${error.message}`);
        }
        setSnackbarSeverity('error');
        setSnackbarOpen(true);
    };

    // Function to handle API success and display messages
    const handleApiSuccess = (message: string) => {
        setSnackbarMessage(message);
        setSnackbarSeverity('success');
        setSnackbarOpen(true);
    };

    // Function to handle API responses and execute callbacks
    const handleApiResponse = (
        response: any,
        successMessage: string,
        action: string,
        successCallback?: Function
    ) => {
        if (response.data.status === 'success') {
            if (successCallback) successCallback();
            handleApiSuccess(successMessage);
        } else {
            console.log(`Failed to ${action}:`, response.data.message);
            setSnackbarMessage(`Failed to ${action}: ${response.data.message}`);
            setSnackbarSeverity('error');
            setSnackbarOpen(true);
        }
    };

    // Delete a banquet
    const handleDeleteBanquet = async (banquetBIN: number) => {
        try {
            const response = await api.post(`/deleteBanquet`, { banquetBIN });
            console.log('Delete banquet response:', response.data);
            handleApiResponse(response, 'Banquet deleted successfully!', 'delete banquet', fetchBanquets);
        } catch (error: any) {
            handleApiError(error, 'deleting banquet');
        }
    };

    // Close the dialog and reset selected banquet
    const handleDialogClose = () => {
        setOpenDialog(false);
        setErrors({});
        setSelectedBanquet({
            BIN: 0,
            name: '',
            dateTime: '',
            address: '',
            location: '',
            contactFirstName: '',
            contactLastName: '',
            available: 'Y',
            quota: 0,
            meals: [
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
                { type: '', dishName: '', price: NaN, specialCuisine: '' },
            ],
        });
    };

    // Submit the create or update banquet request
    const handleDialogSubmit = async () => {
        // Validate required fields
        let tempErrors: { [key: string]: any } = {};
        if (!selectedBanquet.name) tempErrors.name = 'Banquet Name is required';
        if (!selectedBanquet.dateTime) tempErrors.dateTime = 'Date & Time is required';
        if (!selectedBanquet.address) tempErrors.address = 'Address is required';
        if (!selectedBanquet.location) tempErrors.location = 'Location is required';
        if (!selectedBanquet.contactFirstName)
            tempErrors.contactFirstName = 'Contact First Name is required';
        if (!selectedBanquet.contactLastName)
            tempErrors.contactLastName = 'Contact Last Name is required';
        if (!selectedBanquet.available) tempErrors.available = 'Available is required';
        if (
            selectedBanquet.quota === null ||
            selectedBanquet.quota === undefined ||
            isNaN(selectedBanquet.quota)
        )
            tempErrors.quota = 'Quota is required and must be a number';

        // Validate meals
        const mealsErrors = selectedBanquet.meals.map((meal, index) => {
            const mealErrors: { [key: string]: string } = {};
            if (!meal.type) mealErrors.type = 'Meal Type is required';
            if (!meal.dishName) mealErrors.dishName = 'Dish Name is required';
            if (meal.price === null || meal.price === undefined || isNaN(meal.price))
                mealErrors.price = 'Price is required and must be a number';
            if (!meal.specialCuisine) mealErrors.specialCuisine = 'Special Cuisine is required';
            return mealErrors;
        });

        // Check if there are any errors in meals
        if (mealsErrors.some((mealError) => Object.keys(mealError).length > 0)) {
            tempErrors.meals = mealsErrors;
        }

        if (Object.keys(tempErrors).length > 0) {
            setErrors(tempErrors);
            return;
        }

        if (isEditing) {
            try {
                const response = await api.post('/updateBanquet', selectedBanquet);
                handleApiResponse(
                    response,
                    'Banquet updated successfully!',
                    'update banquet',
                    () => {
                        fetchBanquets();
                        handleDialogClose();
                    }
                );
            } catch (error: any) {
                handleApiError(error, 'updating banquet');
            }
        } else {
            // Create banquet
            try {
                const response = await api.post('/createBanquet', selectedBanquet);
                handleApiResponse(
                    response,
                    'Banquet created successfully!',
                    'create banquet',
                    () => {
                        fetchBanquets();
                        handleDialogClose();
                    }
                );
            } catch (error: any) {
                handleApiError(error, 'creating banquet');
            }
        }
    };

    return (
        <Box sx={{ mt: 2 }}>
            {/* Button to create a new banquet */}
            <Button variant="contained" color="primary" onClick={handleCreateBanquet}>
                Create New Banquet
            </Button>
            {/* DataGrid container with scrollbar */}
            <Box
                sx={{
                    mt: 2,
                    overflowX: 'auto',
                    overflowY: 'auto',
                    '& .MuiDataGrid-root': {
                        overflowX: 'visible',
                    },
                    '& .MuiDataGrid-columnHeader, & .MuiDataGrid-cell': {
                        outline: 'none !important',
                        whiteSpace: 'nowrap',
                    },
                    '& .MuiDataGrid-columnHeaders': {
                        backgroundColor: '#f5f5f5',
                    },
                    // Custom scrollbar styles
                    '&::-webkit-scrollbar': {
                        height: '10px',
                        width: '10px',
                    },
                    '&::-webkit-scrollbar-thumb': {
                        backgroundColor: '#c1c1c1',
                        borderRadius: '5px',
                    },
                    '&::-webkit-scrollbar-track': {
                        backgroundColor: '#f0f0f0',
                    },
                    // For Firefox
                    scrollbarWidth: 'thin',
                    scrollbarColor: '#c1c1c1 #f0f0f0',
                    // Set a fixed height for the DataGrid container
                    height: 600, // Adjust as needed
                }}
            >
                <DataGrid
                    rows={banquets}
                    columns={columns}
                    loading={loading}
                    getRowId={(row) => row.BIN}
                    paginationMode="client"
                    initialState={{
                        pagination: {
                            paginationModel: { pageSize: 10, page: 0 },
                        },
                    }}
                    pageSizeOptions={[10]}
                />
            </Box>
            {/* Dialog for Creating/Editing Banquet */}
            <Dialog
                open={openDialog}
                onClose={handleDialogClose}
                maxWidth="md"
                fullWidth
                scroll="paper"
            >
                <DialogTitle>{isEditing ? 'Edit Banquet' : 'Create New Banquet'}</DialogTitle>
                <DialogContent sx={{ overflow: 'visible', paddingTop: 2 }}>
                    {/* Use Stack for layout instead of Grid */}
                    <Stack spacing={2}>
                        {/* Banquet detail fields */}
                        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                            <TextField
                                label="Banquet Name"
                                fullWidth
                                value={selectedBanquet.name}
                                required
                                error={!!errors.name}
                                helperText={errors.name}
                                onChange={(e) =>
                                    setSelectedBanquet({ ...selectedBanquet, name: e.target.value })
                                }
                            />
                            <TextField
                                label="Date & Time"
                                type="datetime-local"
                                fullWidth
                                required
                                error={!!errors.dateTime}
                                helperText={errors.dateTime}
                                value={selectedBanquet.dateTime}
                                onChange={(e) =>
                                    setSelectedBanquet({
                                        ...selectedBanquet,
                                        dateTime: e.target.value,
                                    })
                                }
                                InputLabelProps={{
                                    shrink: true,
                                }}
                            />
                        </Stack>
                        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                            <TextField
                                label="Address"
                                fullWidth
                                value={selectedBanquet.address}
                                required
                                error={!!errors.address}
                                helperText={errors.address}
                                onChange={(e) =>
                                    setSelectedBanquet({ ...selectedBanquet, address: e.target.value })
                                }
                            />
                            <TextField
                                label="Location"
                                fullWidth
                                value={selectedBanquet.location}
                                required
                                error={!!errors.location}
                                helperText={errors.location}
                                onChange={(e) =>
                                    setSelectedBanquet({ ...selectedBanquet, location: e.target.value })
                                }
                            />
                        </Stack>
                        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                            <TextField
                                label="Contact First Name"
                                fullWidth
                                value={selectedBanquet.contactFirstName}
                                required
                                error={!!errors.contactFirstName}
                                helperText={errors.contactFirstName}
                                onChange={(e) =>
                                    setSelectedBanquet({
                                        ...selectedBanquet,
                                        contactFirstName: e.target.value,
                                    })
                                }
                            />
                            <TextField
                                label="Contact Last Name"
                                fullWidth
                                value={selectedBanquet.contactLastName}
                                required
                                error={!!errors.contactLastName}
                                helperText={errors.contactLastName}
                                onChange={(e) =>
                                    setSelectedBanquet({
                                        ...selectedBanquet,
                                        contactLastName: e.target.value,
                                    })
                                }
                            />
                        </Stack>
                        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                            <FormControl fullWidth error={!!errors.available} required>
                                <InputLabel>Available *</InputLabel>
                                <Select
                                    value={selectedBanquet.available}
                                    onChange={(e) =>
                                        setSelectedBanquet({
                                            ...selectedBanquet,
                                            available: e.target.value as string,
                                        })
                                    }
                                    label="Available"
                                >
                                    <MenuItem value="Y">Yes</MenuItem>
                                    <MenuItem value="N">No</MenuItem>
                                </Select>
                                {errors.available && (
                                    <FormHelperText>{errors.available}</FormHelperText>
                                )}
                            </FormControl>
                            <TextField
                                label="Quota"
                                type="number"
                                fullWidth
                                value={isNaN(selectedBanquet.quota) ? '' : selectedBanquet.quota}
                                required
                                error={!!errors.quota}
                                helperText={errors.quota}
                                onChange={(e) =>
                                    setSelectedBanquet({
                                        ...selectedBanquet,
                                        quota: !isNaN(parseInt(e.target.value))
                                            ? parseInt(e.target.value)
                                            : NaN,
                                    })
                                }
                                InputProps={{
                                    inputProps: { min: 0, step: 1 },
                                }}
                            />
                        </Stack>
                        {/* Meals Inputs */}
                        <Typography variant="h6" gutterBottom>
                            Meals
                        </Typography>
                        {selectedBanquet.meals.map((meal: Meal, index: number) => (
                            <Stack
                                key={index}
                                direction={{ xs: 'column', sm: 'row' }}
                                spacing={2}
                            >
                                <TextField
                                    label={`Meal Type ${index + 1}`}
                                    fullWidth
                                    value={meal.type}
                                    required
                                    error={
                                        !!errors.meals &&
                                        errors.meals[index] &&
                                        !!errors.meals[index].type
                                    }
                                    helperText={
                                        errors.meals &&
                                        errors.meals[index] &&
                                        errors.meals[index].type
                                    }
                                    onChange={(e) => {
                                        const updatedMeals = [...selectedBanquet.meals];
                                        updatedMeals[index].type = e.target.value;
                                        setSelectedBanquet({
                                            ...selectedBanquet,
                                            meals: updatedMeals,
                                        });
                                    }}
                                />
                                <TextField
                                    label={`Dish Name ${index + 1}`}
                                    fullWidth
                                    value={meal.dishName}
                                    required
                                    error={
                                        !!errors.meals &&
                                        errors.meals[index] &&
                                        !!errors.meals[index].dishName
                                    }
                                    helperText={
                                        errors.meals &&
                                        errors.meals[index] &&
                                        errors.meals[index].dishName
                                    }
                                    onChange={(e) => {
                                        const updatedMeals = [...selectedBanquet.meals];
                                        updatedMeals[index].dishName = e.target.value;
                                        setSelectedBanquet({
                                            ...selectedBanquet,
                                            meals: updatedMeals,
                                        });
                                    }}
                                />
                                <TextField
                                    label={`Price ${index + 1}`}
                                    type="number"
                                    fullWidth
                                    value={isNaN(meal.price) ? '' : meal.price}
                                    required
                                    error={
                                        !!errors.meals &&
                                        errors.meals[index] &&
                                        !!errors.meals[index].price
                                    }
                                    helperText={
                                        errors.meals &&
                                        errors.meals[index] &&
                                        errors.meals[index].price
                                    }
                                    onChange={(e) => {
                                        const updatedMeals = [...selectedBanquet.meals];
                                        updatedMeals[index].price = !isNaN(
                                            parseFloat(e.target.value)
                                        )
                                            ? parseFloat(e.target.value)
                                            : NaN;
                                        setSelectedBanquet({
                                            ...selectedBanquet,
                                            meals: updatedMeals,
                                        });
                                    }}
                                    InputProps={{
                                        inputProps: { min: 0 },
                                    }}
                                />
                                <TextField
                                    label={`Special Cuisine ${index + 1}`}
                                    fullWidth
                                    value={meal.specialCuisine}
                                    required
                                    error={
                                        !!errors.meals &&
                                        errors.meals[index] &&
                                        !!errors.meals[index].specialCuisine
                                    }
                                    helperText={
                                        errors.meals &&
                                        errors.meals[index] &&
                                        errors.meals[index].specialCuisine
                                    }
                                    onChange={(e) => {
                                        const updatedMeals = [...selectedBanquet.meals];
                                        updatedMeals[index].specialCuisine = e.target.value;
                                        setSelectedBanquet({
                                            ...selectedBanquet,
                                            meals: updatedMeals,
                                        });
                                    }}
                                />
                            </Stack>
                        ))}
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleDialogClose}>Cancel</Button>
                    <Button onClick={handleDialogSubmit}>
                        {isEditing ? 'Update' : 'Create'}
                    </Button>
                </DialogActions>
            </Dialog>
            {/* Snackbar for notifications */}
            <Snackbar
                open={snackbarOpen}
                autoHideDuration={2000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
            >
                <Alert
                    onClose={handleSnackbarClose}
                    severity={snackbarSeverity}
                    sx={{ width: '100%' }}
                >
                    {snackbarMessage}
                </Alert>
            </Snackbar>
        </Box>
    );
}