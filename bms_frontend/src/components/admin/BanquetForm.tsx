// src/components/BanquetForm.tsx
'use client';

import React, { useState } from 'react';
import {
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
    Button,
    Typography,
} from '@mui/material';
import { Banquet, Meal } from '../../utils/types';

// Props interface for BanquetForm
interface BanquetFormProps {
    isOpen: boolean;
    isEditing: boolean;
    banquet: Banquet;
    errors: { [key: string]: any };
    onClose: () => void;
    onSubmit: (banquet: Banquet) => void;
    setBanquet: React.Dispatch<React.SetStateAction<Banquet>>;
    setErrors: React.Dispatch<React.SetStateAction<{ [key: string]: any }>>;
}

// BanquetForm Component
const BanquetForm: React.FC<BanquetFormProps> = ({
    isOpen,
    isEditing,
    banquet,
    errors,
    onClose,
    onSubmit,
    setBanquet,
    setErrors,
}) => {
    // Handle form submission
    const handleFormSubmit = () => {
        onSubmit(banquet);
    };

    return (
        <Dialog open={isOpen} onClose={onClose} maxWidth="md" fullWidth scroll="paper">
            <DialogTitle>{isEditing ? 'Edit Banquet' : 'Create New Banquet'}</DialogTitle>
            <DialogContent sx={{ overflow: 'visible', paddingTop: 2 }}>
                {/* Use Stack for layout */}
                <Stack spacing={2}>
                    {/* Banquet detail fields */}
                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                        <TextField
                            label="Banquet Name"
                            fullWidth
                            value={banquet.name}
                            required
                            error={!!errors.name}
                            helperText={errors.name}
                            onChange={(e) => setBanquet({ ...banquet, name: e.target.value })}
                        />
                        <TextField
                            label="Date & Time"
                            type="datetime-local"
                            fullWidth
                            required
                            error={!!errors.dateTime}
                            helperText={errors.dateTime}
                            value={banquet.dateTime}
                            onChange={(e) => setBanquet({ ...banquet, dateTime: e.target.value })}
                            InputLabelProps={{
                                shrink: true,
                            }}
                        />
                    </Stack>
                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                        <TextField
                            label="Address"
                            fullWidth
                            value={banquet.address}
                            required
                            error={!!errors.address}
                            helperText={errors.address}
                            onChange={(e) => setBanquet({ ...banquet, address: e.target.value })}
                        />
                        <TextField
                            label="Location"
                            fullWidth
                            value={banquet.location}
                            required
                            error={!!errors.location}
                            helperText={errors.location}
                            onChange={(e) => setBanquet({ ...banquet, location: e.target.value })}
                        />
                    </Stack>
                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                        <TextField
                            label="Contact First Name"
                            fullWidth
                            value={banquet.contactFirstName}
                            required
                            error={!!errors.contactFirstName}
                            helperText={errors.contactFirstName}
                            onChange={(e) => setBanquet({ ...banquet, contactFirstName: e.target.value })}
                        />
                        <TextField
                            label="Contact Last Name"
                            fullWidth
                            value={banquet.contactLastName}
                            required
                            error={!!errors.contactLastName}
                            helperText={errors.contactLastName}
                            onChange={(e) => setBanquet({ ...banquet, contactLastName: e.target.value })}
                        />
                    </Stack>
                    <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                        <FormControl fullWidth error={!!errors.available} required>
                            <InputLabel>Available</InputLabel>
                            <Select
                                value={banquet.available}
                                onChange={(e) => setBanquet({ ...banquet, available: e.target.value as string })}
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
                            value={isNaN(banquet.quota) ? '' : banquet.quota}
                            required
                            error={!!errors.quota}
                            helperText={errors.quota}
                            onChange={(e) =>
                                setBanquet({
                                    ...banquet,
                                    quota: !isNaN(parseInt(e.target.value)) ? parseInt(e.target.value) : NaN,
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
                    {banquet.meals.map((meal: Meal, index: number) => (
                        <Stack key={index} direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                            <TextField
                                label={`Meal Type ${index + 1}`}
                                fullWidth
                                value={meal.type}
                                required
                                error={!!errors.meals && errors.meals[index]?.type}
                                helperText={errors.meals && errors.meals[index]?.type}
                                onChange={(e) => {
                                    const updatedMeals = banquet.meals.map((m, i) => 
                                        i === index ? { ...m, type: e.target.value } : m
                                    );
                                    setBanquet({ ...banquet, meals: updatedMeals });
                                }}
                            />
                            <TextField
                                label={`Dish Name ${index + 1}`}
                                fullWidth
                                value={meal.dishName}
                                required
                                error={!!errors.meals && errors.meals[index]?.dishName}
                                helperText={errors.meals && errors.meals[index]?.dishName}
                                onChange={(e) => {
                                    const updatedMeals = banquet.meals.map((m, i) => 
                                        i === index ? { ...m, dishName: e.target.value } : m
                                    );
                                    setBanquet({ ...banquet, meals: updatedMeals });
                                }}
                            />
                            <TextField
                                label={`Price ${index + 1}`}
                                type="number"
                                fullWidth
                                value={isNaN(meal.price) ? '' : meal.price}
                                required
                                error={!!errors.meals && errors.meals[index]?.price}
                                helperText={errors.meals && errors.meals[index]?.price}
                                onChange={(e) => {
                                    const updatedMeals = banquet.meals.map((m, i) => 
                                        i === index ? { ...m, price: !isNaN(parseFloat(e.target.value)) ? parseFloat(e.target.value) : NaN } : m
                                    );
                                    setBanquet({ ...banquet, meals: updatedMeals });
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
                                error={!!errors.meals && errors.meals[index]?.specialCuisine}
                                helperText={errors.meals && errors.meals[index]?.specialCuisine}
                                onChange={(e) => {
                                    const updatedMeals = banquet.meals.map((m, i) => 
                                        i === index ? { ...m, specialCuisine: e.target.value } : m
                                    );
                                    setBanquet({ ...banquet, meals: updatedMeals });
                                }}
                            />
                        </Stack>
                    ))}
                </Stack>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Cancel</Button>
                <Button onClick={handleFormSubmit}>{isEditing ? 'Update' : 'Create'}</Button>
            </DialogActions>
        </Dialog>
    );
};

export default BanquetForm;