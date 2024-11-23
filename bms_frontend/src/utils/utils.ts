// src/utils/utils.ts

// Utility function to format dateTime for input fields
export const formatDateTimeForInput = (dateTimeStr: string): string => {
    const date = new Date(dateTimeStr);
    // Check if date is valid
    if (isNaN(date.getTime())) {
        return '';
    }
    // Format date to 'YYYY-MM-DDTHH:mm'
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2); // Months are zero-based
    const day = ('0' + date.getDate()).slice(-2);
    const hours = ('0' + date.getHours()).slice(-2);
    const minutes = ('0' + date.getMinutes()).slice(-2);
    return `${year}-${month}-${day}T${hours}:${minutes}`;
};