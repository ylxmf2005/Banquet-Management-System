// src/services/AttendeeService.ts

import api from './api';
import { Banquet, Attendee, Registration } from '../utils/types';

// Service to handle API calls related to attendees
const Service = {
    // Get attendee by email
    fetchBanquets: async (): Promise<Banquet[]> => {
        const response = await api.get('/getAllBanquets');
        return response.data.banquets || [];
    },

    // Create a new banquet
    createBanquet: async (banquet: Banquet): Promise<any> => {
        const response = await api.post('/createBanquet', banquet);
        return response.data;
    },

    // Update an existing banquet
    updateBanquet: async (banquet: Banquet): Promise<any> => {
        const response = await api.post('/updateBanquet', banquet);
        return response.data;
    },

    // Delete a banquet
    deleteBanquet: async (banquetBIN: number): Promise<any> => {
        const response = await api.post('/deleteBanquet', { banquetBIN });
        return response.data;
    },
    getAttendeeByEmail: async (email: string): Promise<Attendee | null> => {
        const response = await api.get('/getAttendeeByEmail', { params: { email } });
        if (response.data.status === 'success') {
            return response.data.attendee;
        }
        return null;
    },

    // Update attendee profile
    updateAttendeeProfile: async (attendee: Attendee): Promise<any> => {
        const response = await api.post('/updateAttendeeProfile', attendee);
        return response.data;
    },

    // Get registrations by attendee email
    getRegistrationsByEmail: async (email: string): Promise<Registration[]> => {
        const response = await api.get('/getReservesByAttendeeEmail', { params: { email } });
        if (response.data.status === 'success') {
            return response.data.registrations;
        }
        return [];
    },

    // Update attendee registration data
    updateRegistrationData: async (registration: Registration): Promise<any> => {
        const response = await api.post('/updateAttendeeRegistrationData', {
            email: registration.attendeeEmail,
            registrationData: registration,
        });
        return response.data;
    },

    // Delete registration
    deleteRegistration: async (attendeeEmail: string, banquetBIN: number): Promise<any> => {
        const response = await api.post('/deleteReserve', {
            attendeeEmail,
            banquetBIN,
        });
        return response.data;
    },
};

export default Service;