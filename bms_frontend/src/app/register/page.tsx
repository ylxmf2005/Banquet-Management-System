'use client';
import React, { useContext } from 'react';
import { useForm } from 'react-hook-form';
import api from '../../service/api';
import { useRouter } from 'next/navigation';
import { yupResolver } from '@hookform/resolvers/yup';
import {
    Avatar,
    Button,
    TextField,
    Link as MuiLink,
    Stack,
    Box,
    Typography,
    Container,
} from '@mui/material';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import { SnackbarContext } from '../../context/SnackbarContext';
import { registerFormSchema } from '../../utils/validationSchemas';
import { RegisterFormInputs } from '../../utils/types';

export default function RegisterPage() {
    const {
        register,
        handleSubmit,
        formState: { errors },
        setValue,
    } = useForm<RegisterFormInputs>({
        resolver: yupResolver(registerFormSchema),
    });
    const router = useRouter();
    const { showMessage } = useContext(SnackbarContext);

    const handleNameInput = (
        event: React.ChangeEvent<HTMLInputElement>,
        field: 'firstName' | 'lastName'
    ) => {
        const filteredValue = event.target.value.replace(/[^a-zA-Z]/g, '');
        setValue(field, filteredValue);
    };

    const handleMobileInput = (event: React.ChangeEvent<HTMLInputElement>) => {
        const filteredValue = event.target.value.replace(/[^0-9]/g, '').slice(0, 8);
        setValue('mobileNo', filteredValue);
    };

    const onSubmit = async (data: RegisterFormInputs) => {
        try {
            const response = await api.post('/registerAttendee', data);
            handleApiResponse(
                response,
                () => {
                    showMessage('Registration Successful', 'success');
                    router.push('/login');
                },
                'registering'
            );
        } catch (error) {
            handleApiError(error, 'registering');
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

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Avatar sx={{ m: 1, bgcolor: 'primary.main' }}>
                    <LockOutlinedIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Register
                </Typography>
                <Box
                    component="form"
                    onSubmit={handleSubmit(onSubmit)}
                    noValidate
                    sx={{ mt: 3 }}
                >
                    <Stack spacing={2}>
                        <Stack direction="row" spacing={2}>
                            <TextField
                                {...register('firstName')}
                                required
                                fullWidth
                                label="First Name"
                                autoComplete="given-name"
                                error={!!errors.firstName}
                                helperText={errors.firstName?.message}
                                onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleNameInput(e, 'firstName')}
                            />
                            <TextField
                                {...register('lastName')}
                                required
                                fullWidth
                                label="Last Name"
                                autoComplete="family-name"
                                error={!!errors.lastName}
                                helperText={errors.lastName?.message}
                                onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleNameInput(e, 'lastName')}
                            />
                        </Stack>
                        
                        <TextField
                            {...register('address')}
                            required
                            fullWidth
                            label="Address"
                            autoComplete="address"
                            error={!!errors.address}
                            helperText={errors.address?.message}
                        />
                        
                        <TextField
                            {...register('type')}
                            required
                            fullWidth
                            label="Attendee Type"
                            error={!!errors.type}
                            helperText={errors.type?.message}
                        />
                        
                        <TextField
                            {...register('email')}
                            required
                            fullWidth
                            label="Email Address"
                            autoComplete="email"
                            error={!!errors.email}
                            helperText={errors.email?.message}
                        />
                        
                        <TextField
                            {...register('password')}
                            required
                            fullWidth
                            label="Password"
                            type="password"
                            autoComplete="new-password"
                            error={!!errors.password}
                            helperText={errors.password?.message}
                        />
                        
                        <TextField
                            {...register('mobileNo')}
                            required
                            fullWidth
                            label="Mobile Number"
                            error={!!errors.mobileNo}
                            helperText={errors.mobileNo?.message}
                            onChange={handleMobileInput}
                            inputProps={{ maxLength: 8 }}
                        />
                        
                        <TextField
                            {...register('organization')}
                            required
                            fullWidth
                            label="Affiliated Organization"
                            error={!!errors.organization}
                            helperText={errors.organization?.message}
                        />
                        
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 1 }}
                        >
                            Register
                        </Button>
                        
                        <Stack direction="row" justifyContent="flex-end">
                            <MuiLink href="/login" variant="body2">
                                Already have an account? Login
                            </MuiLink>
                        </Stack>
                    </Stack>
                </Box>
            </Box>
        </Container>
    );
}