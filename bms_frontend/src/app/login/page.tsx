'use client';
import React, { useEffect, useContext } from 'react';
import { useForm } from 'react-hook-form';
import { useRouter } from 'next/navigation';
import { AuthContext } from '../../context/AuthContext';
import { SnackbarContext } from '../../context/SnackbarContext';
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

interface LoginFormInputs {
    email: string;
    password: string;
}

export default function LoginPage() {
    const { register, handleSubmit } = useForm<LoginFormInputs>();
    const router = useRouter();
    const auth = useContext(AuthContext);
    const { showMessage } = useContext(SnackbarContext);

    const onSubmit = async (data: LoginFormInputs) => {
        try {
            await auth?.login(data.email, data.password);
            showMessage('Login successful', 'success');
        } catch (error) {
            showMessage('Invalid email or password', 'error');
        }
    };

    useEffect(() => {
        if (auth?.user) {
            showMessage(`Welcome back, ${auth.user.email}`, 'success');
            if (auth.user.role === 'admin') {
                router.push('/admin');
            } else {
                router.push('/user');
            }
        }
    }, [auth?.user, router, showMessage]);

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
                <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                    <LockOutlinedIcon />
                </Avatar>
                <Typography component="h1" variant="h5">
                    Login
                </Typography>
                <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 1 }}>
                    <TextField
                        {...register('email')}
                        margin="normal"
                        required
                        fullWidth
                        label="Email Address"
                        autoComplete="email"
                        autoFocus
                    />
                    <TextField
                        {...register('password')}
                        margin="normal"
                        required
                        fullWidth
                        label="Password"
                        type="password"
                        autoComplete="current-password"
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        Login
                    </Button>
                    <Stack direction="row" justifyContent="space-between">
                        <MuiLink href="/forgot-password" variant="body2">
                            Forgot password?
                        </MuiLink>
                        <MuiLink href="/register" variant="body2">
                            Don't have an account? Sign Up
                        </MuiLink>
                    </Stack>
                </Box>
            </Box>
        </Container>
    );
}