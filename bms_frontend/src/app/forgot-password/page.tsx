'use client';
import React from 'react';
import { useForm } from 'react-hook-form';
import api from '../../service/api';
import {
    Avatar,
    Button,
    TextField,
    Box,
    Typography,
    Container,
} from '@mui/material';
import LockResetIcon from '@mui/icons-material/LockReset';

interface ForgotPasswordFormInputs {
    email: string;
}

export default function ForgotPasswordPage() {
    const { register, handleSubmit } = useForm<ForgotPasswordFormInputs>();

    const onSubmit = async (data: ForgotPasswordFormInputs) => {
        try {
            await api.post('/forgotPassword', data);
            alert('Password reset email sent');
        } catch (error) {
            alert('Failed to send password reset email');
        }
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
                <Avatar sx={{ m: 1, bgcolor: 'warning.main' }}>
                    <LockResetIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Forgot password
                </Typography>
                <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 1 }}>
                    <TextField
                        {...register('email')}
                        margin="normal"
                        required
                        fullWidth
                        label="邮箱地址"
                        autoComplete="email"
                        autoFocus
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="warning"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        Reset password
                    </Button>
                </Box>
            </Box>
        </Container>
    );
}
