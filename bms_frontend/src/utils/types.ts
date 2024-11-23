// src/types.ts

// Interface for Meal object
export interface Meal {
    type: string;
    dishName: string;
    price: number;
    specialCuisine: string;
}

// Interface for Banquet object
export interface Banquet {
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

// Interface for Attendee object
export interface Attendee {
    firstName: string;
    lastName: string;
    email: string;
    address: string;
    type: string;
    organization: string;
    mobileNo: string;
    password?: string; // Optional
    originalEmail?: string; // For tracking email changes
}

// Interface for Registration object
export interface Registration {
    attendeeEmail: string;
    banquetBIN: number;
    seatNo: number;
    regTime: string; // Assuming regTime is a string; adjust if necessary
    drinkChoice: string;
    mealChoice: string;
    remarks: string;
}