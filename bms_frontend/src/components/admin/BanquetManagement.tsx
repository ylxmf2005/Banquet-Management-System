// src/components/BanquetManagement.tsx
'use client';

import React, { useEffect, useState, useContext, useCallback } from 'react';
import { Box, Button } from '@mui/material';
import { GridColDef } from '@mui/x-data-grid';
import { Banquet } from '../../utils/types';
import { SnackbarContext } from '../../context/SnackbarContext';
import BanquetList from '../admin/BanquetList';
import BanquetForm from '../admin/BanquetForm';
import { formatDateTimeForInput } from '../../utils/utils';
import { banquetSchema } from '../../utils/validationSchemas';
import * as Yup from 'yup';
import api from '../../service/api';
import { debounce } from 'lodash';

// BanquetManagement Component
export default function BanquetManagement() {
    const [banquets, setBanquets] = useState<Banquet[]>([]);
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [columns, setColumns] = useState<GridColDef[]>([]);
    const [errors, setErrors] = useState<{ [key: string]: any }>({});
    const { showMessage } = useContext(SnackbarContext);
    
    const debouncedFetchBanquets = useCallback(
        debounce(async () => {
            setLoading(true);
            try {
                const response = await api.get('/getAllBanquets');
                const data = response.data.banquets || [];

                const fetchedBanquets = data.map((banquet: Banquet) => ({
                    ...banquet,
                    meals: banquet.meals?.length > 0
                        ? banquet.meals
                        : Array(4).fill({ type: '', dishName: '', price: NaN, specialCuisine: '' }),
                }));

                setBanquets(fetchedBanquets);
                calculateColumnWidths(fetchedBanquets);
            } catch (error: any) {
                handleApiError(error, 'fetching banquets');
            }
            setLoading(false);
        }, 500),
        []
    );

    useEffect(() => {
        debouncedFetchBanquets();
    }, [debouncedFetchBanquets]);

    // Selected banquet for editing or creating
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
        meals: Array(4).fill({ type: '', dishName: '', price: NaN, specialCuisine: '' }), // Initialize meals with 4 empty meal objects
    });

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
            data.forEach((row) => {
                // @ts-ignore
                const value = row[header.field as keyof Banquet];
                const text = value ? value.toString() : '';
                const width = getTextWidth(text) + 40; // Adding padding
                if (width > maxWidth) {
                    maxWidth = width;
                }
            });
            return {
                field: header.field,
                headerName: header.headerName,
                width: Math.min(maxWidth, 400), // Set a max width to prevent excessively wide columns
            } as GridColDef;
        });

        setColumns(calculatedColumns);
    };

    // Open the dialog to create a new banquet
    const handleCreateBanquet = () => {
        // Reset selectedBanquet to default values
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
            meals: Array(4).fill({ type: '', dishName: '', price: NaN, specialCuisine: '' }),
        });
        setErrors({}); // Reset any previous errors
        setIsEditing(false); // Set editing mode to false
        setOpenDialog(true); // Open the dialog
    };

    // Open the dialog to edit an existing banquet
    const handleEditBanquet = (banquet: Banquet) => {
        // Ensure meals are properly set in selectedBanquet
        setSelectedBanquet({
            ...banquet,
            // Format the dateTime to "YYYY-MM-DDTHH:mm" format for the input field
            dateTime: formatDateTimeForInput(banquet.dateTime),
            meals:
                banquet.meals && banquet.meals.length > 0
                    ? banquet.meals
                    : Array(4).fill({ type: '', dishName: '', price: NaN, specialCuisine: '' }),
        });
        setErrors({}); // Reset any previous errors
        setIsEditing(true); // Set editing mode to true
        setOpenDialog(true); // Open the dialog
    };

    // Delete a banquet
    const handleDeleteBanquet = async (banquetBIN: number) => {
        try {
            const response = await api.post('/deleteBanquet', { banquetBIN });
            handleApiResponse(response.data, 'Banquet deleted successfully!', 'delete banquet', debouncedFetchBanquets);
        } catch (error: any) {
            handleApiError(error, 'deleting banquet');
        }
    };

    // Close the dialog and reset selected banquet
    const handleDialogClose = () => {
        setOpenDialog(false);
        setErrors({}); // Reset errors
        // Reset selectedBanquet to default values
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
            meals: Array(4).fill({ type: '', dishName: '', price: NaN, specialCuisine: '' }),
        });
    };

    // Submit the create or update banquet request
    const handleDialogSubmit = async (banquet: Banquet) => {
        // Reset previous errors
        setErrors({});

        // Validate banquet data using Yup schema
        try {
            await banquetSchema.validate(banquet, { abortEarly: false });
        } catch (err) {
            if (err instanceof Yup.ValidationError) {
                const validationErrors: { [key: string]: any } = {};
                err.inner.forEach((error) => {
                    if (error.path) {
                        // Handle nested errors for meals
                        if (error.path.startsWith('meals')) {
                            const pathParts = error.path.split('.');
                            const index = parseInt(pathParts[1], 10);
                            const field = pathParts[2];
                            if (!validationErrors.meals) {
                                validationErrors.meals = [];
                            }
                            if (!validationErrors.meals[index]) {
                                validationErrors.meals[index] = {};
                            }
                            validationErrors.meals[index][field] = error.message;
                        } else {
                            validationErrors[error.path] = error.message;
                        }
                    }
                });
                setErrors(validationErrors); // Set errors to display in the form
                return; // Exit the function if validation fails
            } else {
                // If it's another type of error, handle accordingly
                console.error('Validation error', err);
                showMessage('Validation error occurred.', 'error');
                return;
            }
        }

        // If validation passes, proceed to create or update
        if (isEditing) {
            try {
                const response = await api.post('/updateBanquet', banquet);
                handleApiResponse(
                    response.data,
                    'Banquet updated successfully!',
                    'update banquet',
                    () => {
                        debouncedFetchBanquets();
                        handleDialogClose();
                    }
                );
            } catch (error: any) {
                handleApiError(error, 'updating banquet');
            }
        } else {
            try {
                const response = await api.post('/createBanquet', banquet);
                handleApiResponse(
                    response.data,
                    'Banquet created successfully!',
                    'create banquet',
                    () => {
                        debouncedFetchBanquets();
                        handleDialogClose();
                    }
                );
            } catch (error: any) {
                handleApiError(error, 'creating banquet');
            }
        }
    };

    // Function to handle API errors and display messages
    const handleApiError = (error: any, action: string) => {
        let message = '';
        if (error.response && error.response.data && error.response.data.message) {
            console.log(`Error ${action}:`, error.response.data.message);
            message = `Error ${action}: ${error.response.data.message}`;
        } else {
            console.log(`Error ${action}:`, error.message);
            message = `Error ${action}: ${error.message}`;
        }
        showMessage(message, 'error'); // Use showMessage from SnackbarContext to display error
    };

    // Function to handle API success and display messages
    const handleApiSuccess = (message: string) => {
        showMessage(message, 'success'); // Use showMessage from SnackbarContext to display success
    };

    // Function to handle API responses and execute callbacks
    const handleApiResponse = (
        response: any,
        successMessage: string,
        action: string,
        successCallback?: Function
    ) => {
        if (response.status === 'success') {
            if (action === 'create banquet' || action === 'delete banquet') {
                debouncedFetchBanquets();
            } else if (action === 'update banquet') {
                if (response.data?.banquet) {
                    setBanquets(prevBanquets =>
                        prevBanquets.map(b =>
                            b.BIN === response.data.banquet.BIN ? response.data.banquet : b
                        )
                    );
                } else {
                    debouncedFetchBanquets();
                }
            }
            if (successCallback) successCallback();
            handleApiSuccess(successMessage);
        } else {
            console.log(`Failed to ${action}:`, response.message);
            const message = `Failed to ${action}: ${response.message}`;
            showMessage(message, 'error');
        }
    };

    return (
        <Box sx={{ mt: 2 }}>
            {/* Button to create a new banquet */}
            <Button variant="contained" color="primary" onClick={handleCreateBanquet}>
                Create New Banquet
            </Button>
            {/* Banquet List component displaying the list of banquets */}
            <BanquetList
                banquets={banquets}
                loading={loading}
                columns={columns}
                onEdit={handleEditBanquet}
                onDelete={handleDeleteBanquet}
            />
            {/* Banquet Form Dialog for creating/editing a banquet */}
            <BanquetForm
                isOpen={openDialog}
                isEditing={isEditing}
                banquet={selectedBanquet}
                errors={errors}
                onClose={handleDialogClose}
                onSubmit={handleDialogSubmit}
                setBanquet={setSelectedBanquet}
                setErrors={setErrors}
            />
        </Box>
    );
}