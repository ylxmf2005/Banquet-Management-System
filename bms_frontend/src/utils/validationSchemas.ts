// src/validationSchemas.ts

import * as Yup from 'yup';

// Attendee validation schema
export const attendeeSchema = Yup.object().shape({
    firstName: Yup.string()
        .required('This field is required.')
        .matches(/^[A-Za-z]+$/, 'First name must contain only English letters.'),
    lastName: Yup.string()
        .required('This field is required.')
        .matches(/^[A-Za-z]+$/, 'Last name must contain only English letters.'),
    email: Yup.string()
        .required('This field is required.')
        .email('Invalid email format.'),
    address: Yup.string()
        .required('This field is required.'),
    type: Yup.string()
        .required('This field is required.'),
    organization: Yup.string()
        .required('This field is required.'),
    mobileNo: Yup.string()
        .required('This field is required.')
        .matches(/^\d{8}$/, 'Mobile number must be exactly 8 digits.'),
    password: Yup.string()
        .test('password-format', 'Password must be at least 6 characters and contain only letters, numbers, and common special characters', function(value) {
            if (!value) return true;
            if (value.length < 6) return false;
            return /^[A-Za-z0-9!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]*$/.test(value);
        }),
});

// Registration validation schema
export const registrationSchemaForAdmin = Yup.object().shape({
    seatNo: Yup.number()
        .required('Seat number is required')
        .typeError('Seat number must be a number')
        .positive('Seat number must be a positive integer')
        .integer('Seat number must be a positive integer'),
    drinkChoice: Yup.string()
        .required('Drink choice is required')
        .nullable()
        .transform((value) => (value === '' ? null : value))
        .test('drink-choice', 'Invalid drink choice', function(value) {
            return value !== null;
        }),
    mealChoice: Yup.string()
        .required('Meal choice is required')
        .nullable()
        .transform((value) => (value === '' ? null : value))
        .test('meal-choice', 'Invalid meal choice', function(value) {
            return value !== null;
        }),
    remarks: Yup.string()
        .nullable()
        .transform((value) => (value === '' ? null : value)),
    banquetBIN: Yup.number()
        .required('Banquet BIN is required')
        .positive('Invalid banquet BIN'),
    attendeeEmail: Yup.string()
        .required('Attendee email is required')
        .email('Invalid email format'),
});

export const registrationSchemaForUser = Yup.object().shape({
    drinkChoice: Yup.string()
        .required('This field is required.'),
    mealChoice: Yup.string()
        .required('This field is required.'),
    remarks: Yup.string(),
});

// Define the Meal validation schema
const mealSchema = Yup.object().shape({
    type: Yup.string().required('Meal Type is required'),
    dishName: Yup.string().required('Dish Name is required'),
    price: Yup.number()
        .typeError('Price must be a number')
        .required('Price is required')
        .min(0, 'Price cannot be negative'),
    specialCuisine: Yup.string().required('Special Cuisine is required'),
});

// Define the Banquet validation schema
export const banquetSchema = Yup.object().shape({
    name: Yup.string().required('Banquet Name is required'),
    dateTime: Yup.string().required('Date & Time is required'),
    address: Yup.string().required('Address is required'),
    location: Yup.string().required('Location is required'),
    contactFirstName: Yup.string().required('Contact First Name is required'),
    contactLastName: Yup.string().required('Contact Last Name is required'),
    available: Yup.string()
        .required('Available is required')
        .oneOf(['Y', 'N'], 'Available must be "Y" or "N"'),
    quota: Yup.number()
        .typeError('Quota must be a number')
        .required('Quota is required')
        .integer('Quota must be an integer')
        .min(0, 'Quota cannot be negative'),
    meals: Yup.array()
        .of(mealSchema)
        .min(1, 'At least one meal is required')
        .required('Meals are required'),
});

// Registration form validation schema
export const registerFormSchema = Yup.object().shape({
    firstName: Yup.string()
        .required('First name is required')
        .matches(/^[A-Za-z]+$/, 'Only letters are allowed'),
    lastName: Yup.string()
        .required('Last name is required')
        .matches(/^[A-Za-z]+$/, 'Only letters are allowed'),
    address: Yup.string()
        .required('Address is required'),
    type: Yup.string()
        .required('Attendee type is required'),
    email: Yup.string()
        .required('Email is required')
        .email('Invalid email format'),
    password: Yup.string()
        .test('password-format', 'Password must be at least 6 characters and contain only letters, numbers, and common special characters', function(value) {
            if (!value) return true;
            if (value.length < 6) return false;
            return /^[A-Za-z0-9!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]*$/.test(value);
        }),
    mobileNo: Yup.string()
        .required('Mobile number is required')
        .matches(/^\d{8}$/, 'Mobile number must be 8 digits'),
    organization: Yup.string()
        .required('Affiliated organization is required'),
});
