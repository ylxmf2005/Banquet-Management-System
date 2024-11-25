// src/components/RegistrationItem.tsx
'use client';

import React from 'react';
import { Box, Typography, TextField, Button, Stack, FormControl, InputLabel, Select, MenuItem, FormHelperText } from '@mui/material';
import { Registration, Meal } from '../../../utils/types';

interface RegistrationItemProps {
    registration: Registration & { meals?: Meal[] };
    errors: { [key: string]: string };
    successMessage: string;
    onChange: (field: string, value: any) => void;
    onUpdate: () => void;
    onDelete: () => void;
    showEmail?: boolean;
}

const RegistrationItem: React.FC<RegistrationItemProps> = ({
    registration,
    errors,
    successMessage,
    onChange,
    onUpdate,
    onDelete,
    showEmail = false,
}) => {
    return (
        <Box sx={{ mt: 2, p: 2, border: '1px solid #ccc' }}>
            <Typography variant="subtitle1">
                Banquet BIN: {registration.banquetBIN}
            </Typography>

            {/* Registration Time and Seat Number */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="Registration Time"
                    fullWidth
                    disabled
                    value={registration.regTime}
                />
                <TextField
                    label="Seat Number"
                    fullWidth
                    required
                    value={registration.seatNo}
                    error={!!errors.seatNo}
                    helperText={errors.seatNo}
                    onChange={(e) => {
                        // Allow only numbers
                        const value = e.target.value.replace(/\D/g, '');
                        onChange('seatNo', value);
                    }}
                />
            </Stack>

            {/* Drink Choice and Meal Choice */}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="Drink Choice"
                    fullWidth
                    required
                    value={registration.drinkChoice}
                    error={!!errors.drinkChoice}
                    helperText={errors.drinkChoice}
                    onChange={(e) => onChange('drinkChoice', e.target.value)}
                />
                <FormControl
                    fullWidth
                    required
                    error={!!errors.mealChoice}
                >
                    <InputLabel id={`meal-choice-label-${registration.banquetBIN}`}>
                        Meal Choice
                    </InputLabel>
                    <Select
                        labelId={`meal-choice-label-${registration.banquetBIN}`}
                        value={registration.mealChoice || ''}
                        label="Meal Choice"
                        onChange={(e) => onChange('mealChoice', e.target.value)}
                    >
                        {registration.meals?.map((meal, index) => (
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
            </Stack>

            {/* Remarks */}
            <Stack direction="column" spacing={2} sx={{ mt: 2 }}>
                <TextField
                    label="Remarks"
                    fullWidth
                    multiline
                    value={registration.remarks}
                    onChange={(e) => onChange('remarks', e.target.value)}
                />
            </Stack>

            {/* Display form-level error message */}
            {errors.form && (
                <Typography color="error" sx={{ mt: 2 }}>
                    {errors.form}
                </Typography>
            )}

            {/* Display success message */}
            {successMessage && (
                <Typography sx={{ mt: 2, color: 'green' }}>{successMessage}</Typography>
            )}

            {/* Buttons to update and delete registration */}
            <Stack direction="row" spacing={2} sx={{ mt: 2 }}>
                <Button variant="contained" color="primary" onClick={onUpdate}>
                    Update Registration
                </Button>
                <Button variant="contained" color="error" onClick={onDelete}>
                    Delete Registration
                </Button>
            </Stack>
        </Box>
    );
};

export default RegistrationItem;