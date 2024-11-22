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
    Stack, // Import Stack for layout
} from '@mui/material';
// Remove Grid import since we're replacing it
import { DataGrid, GridColDef, GridRenderCellParams } from '@mui/x-data-grid';
import api from '../utils/api';

interface Meal {
    type: string;
    dishName: string;
    price: number;
    specialCuisine: string;
}

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
            { type: '', dishName: '', price: 0, specialCuisine: '' },
            { type: '', dishName: '', price: 0, specialCuisine: '' },
            { type: '', dishName: '', price: 0, specialCuisine: '' },
            { type: '', dishName: '', price: 0, specialCuisine: '' },
        ],
    });

    // State to hold validation errors
    const [errors, setErrors] = useState<{ [key: string]: string }>({});

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
                            { type: '', dishName: '', price: 0, specialCuisine: '' },
                            { type: '', dishName: '', price: 0, specialCuisine: '' },
                            { type: '', dishName: '', price: 0, specialCuisine: '' },
                            { type: '', dishName: '', price: 0, specialCuisine: '' },
                        ],
            }));

            setBanquets(fetchedBanquets);

            // After fetching data, calculate column widths
            calculateColumnWidths(fetchedBanquets);
        } catch (error) {
            console.error('Failed to fetch banquets', error);
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
                { type: '', dishName: '', price: 0, specialCuisine: '' },
                { type: '', dishName: '', price: 0, specialCuisine: '' },
                { type: '', dishName: '', price: 0, specialCuisine: '' },
                { type: '', dishName: '', price: 0, specialCuisine: '' },
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
                        { type: '', dishName: '', price: 0, specialCuisine: '' },
                        { type: '', dishName: '', price: 0, specialCuisine: '' },
                        { type: '', dishName: '', price: 0, specialCuisine: '' },
                        { type: '', dishName: '', price: 0, specialCuisine: '' },
                    ],
        });
        setErrors({});
        setIsEditing(true);
        setOpenDialog(true);
    };

    // Delete a banquet
    const handleDeleteBanquet = async (banquetBIN: number) => {
        try {
            const response = await api.delete(`/deleteBanquet`, {
                data: { BIN: banquetBIN },
            });
            if (response.data.status === 'success') {
                fetchBanquets();
            } else {
                console.error('Failed to delete banquet:', response.data.message);
            }
        } catch (error) {
            console.error('Error deleting banquet:', error);
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
                { type: '', dishName: '', price: 0, specialCuisine: '' },
                { type: '', dishName: '', price: 0, specialCuisine: '' },
                { type: '', dishName: '', price: 0, specialCuisine: '' },
                { type: '', dishName: '', price: 0, specialCuisine: '' },
            ],
        });
    };

    // Submit the create or update banquet request
    const handleDialogSubmit = async () => {
        // Validate required fields
        let tempErrors: { [key: string]: string } = {};
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

        if (Object.keys(tempErrors).length > 0) {
            setErrors(tempErrors);
            return;
        }

        if (isEditing) {
            try {
                const response = await api.post('/updateBanquet', selectedBanquet);
                if (response.data.status === 'success') {
                    fetchBanquets();
                    handleDialogClose();
                } else {
                    console.error('Failed to update banquet:', response.data.message);
                }
            } catch (error) {
                console.error('Error updating banquet:', error);
            }
        } else {
            // Create banquet
            try {
                const response = await api.post('/createBanquet', selectedBanquet);
                if (response.data.status === 'success') {
                    fetchBanquets();
                    handleDialogClose();
                } else {
                    console.error('Failed to create banquet:', response.data.message);
                }
            } catch (error) {
                console.error('Error creating banquet:', error);
            }
        }
    };

    return (
        (<Box sx={{ mt: 2 }}>
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
                // Removed autoHeight prop as it's deprecated
                />
            </Box>
            {/* Dialog for Creating/Editing Banquet */}
            <Dialog
                open={openDialog}
                onClose={handleDialogClose}
                maxWidth="md"
                fullWidth
                // Added 'scroll' to enable scrolling if content overflows
                scroll="paper"
            >
                <DialogTitle>{isEditing ? 'Edit Banquet' : 'Create New Banquet'}</DialogTitle>
                {/* Added sx prop to set overflow to visible to prevent label clipping */}
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
                                slotProps={{
                                    inputLabel: {
                                        shrink: true,
                                    }
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
                                {errors.available && <FormHelperText>{errors.available}</FormHelperText>}
                            </FormControl>
                            <TextField
                                label="Quota"
                                type="number"
                                fullWidth
                                value={selectedBanquet.quota}
                                required
                                error={!!errors.quota}
                                helperText={errors.quota}
                                onChange={(e) =>
                                    setSelectedBanquet({
                                        ...selectedBanquet,
                                        quota: parseInt(e.target.value) || 0,
                                    })
                                }
                                slotProps={{
                                    htmlInput: { min: 0, step: 1 }
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
                                    value={meal.price}
                                    onChange={(e) => {
                                        const updatedMeals = [...selectedBanquet.meals];
                                        updatedMeals[index].price =
                                            parseFloat(e.target.value) || 0;
                                        setSelectedBanquet({
                                            ...selectedBanquet,
                                            meals: updatedMeals,
                                        });
                                    }}
                                    slotProps={{
                                        htmlInput: { min: 0 }
                                    }}
                                />
                                <TextField
                                    label={`Special Cuisine ${index + 1}`}
                                    fullWidth
                                    value={meal.specialCuisine}
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
        </Box>)
    );
}