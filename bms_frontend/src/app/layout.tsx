'use client';
import { AuthProvider } from '../context/AuthContext';
import { SnackbarProvider } from '../context/SnackbarContext';
import Navbar from '../components/Navbar';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2', 
    },
    secondary: {
      main: '#dc004e', 
    },
  },
});

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <AuthProvider>
          <SnackbarProvider>
            <ThemeProvider theme={theme}>
              <CssBaseline />
              <Navbar />
              {children}
            </ThemeProvider>
          </SnackbarProvider>
        </AuthProvider>
      </body>
    </html>
  );
}