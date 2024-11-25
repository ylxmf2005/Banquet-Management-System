// src/components/BanquetList.tsx
'use client';

import React from 'react';
import { Box, Button } from '@mui/material';
import { DataGrid, GridColDef, GridRenderCellParams } from '@mui/x-data-grid';
import { Banquet } from '../../utils/types';

// Props interface for BanquetList
interface BanquetListProps {
    banquets: Banquet[];
    loading: boolean;
    columns: GridColDef[];
    onEdit: (banquet: Banquet) => void;
    onDelete: (banquetBIN: number) => void;
}

// BanquetList Component
const BanquetList: React.FC<BanquetListProps> = ({
    banquets,
    loading,
    columns,
    onEdit,
    onDelete,
}) => {
    // Define action column with Edit and Delete buttons
    const actionColumn: GridColDef = {
        field: 'actions',
        headerName: 'Actions',
        width: 180,
        renderCell: (params: GridRenderCellParams) => (
            <div>
                <Button
                    size="small"
                    variant="outlined"
                    onClick={() => onEdit(params.row as Banquet)}
                >
                    Edit
                </Button>
                <Button
                    size="small"
                    variant="outlined"
                    color="error"
                    onClick={() => onDelete((params.row as Banquet).BIN)}
                    sx={{ ml: 1 }}
                >
                    Delete
                </Button>
            </div>
        ),
    };

    return (
        <Box
            sx={{
                mt: 2,
                overflowX: 'auto',
                overflowY: 'auto',
                '& .MuiDataGrid-root': {
                    overflowX: 'visible',
                },
                '& .MuiDataGrid-columnHeader, & .MuiDataGrid-cell': {
                    outline: 'none !important',
                    whiteSpace: 'nowrap',
                },
                '& .MuiDataGrid-columnHeaders': {
                    backgroundColor: '#f5f5f5',
                },
                // Custom scrollbar styles
                '&::-webkit-scrollbar': {
                    height: '10px',
                    width: '10px',
                },
                '&::-webkit-scrollbar-thumb': {
                    backgroundColor: '#c1c1c1',
                    borderRadius: '5px',
                },
                '&::-webkit-scrollbar-track': {
                    backgroundColor: '#f0f0f0',
                },
                // For Firefox
                scrollbarWidth: 'thin',
                scrollbarColor: '#c1c1c1 #f0f0f0',
                // Set a fixed height for the DataGrid container
                height: 600, // Adjust as needed
            }}
        >
            <DataGrid
                rows={banquets}
                columns={[...columns, actionColumn]} // Add the action column
                loading={loading}
                getRowId={(row) => row.BIN}
                paginationMode="client"
                initialState={{
                    pagination: {
                        paginationModel: { pageSize: 10, page: 0 },
                    },
                }}
                pageSizeOptions={[10]}
            />
        </Box>
    );
};

export default BanquetList;