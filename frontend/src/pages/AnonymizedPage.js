import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
    Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Paper, Typography, Box, Button
} from '@mui/material';
import {Link} from "react-router-dom";

const AnonymizedPage = () => {
    const [data, setData] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        axios.get('/api/anonymization/all-anonymized')
            .then((res) => setData(res.data))
            .catch((err) => setError('Ошибка при загрузке данных: ' + err.message));
    }, []);

    return (
        <Box sx={{p: 4}}>
            <Typography variant="h5" gutterBottom>Обезличенные данные</Typography>
            {error && <Typography color="error">{error}</Typography>}
            <Button variant="outlined" component={Link} to="/" sx={{ mb: 2 }}>
                Назад
            </Button>
            <TableContainer component={Paper}>
                <Table size="small">
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Дата рождения</TableCell>
                            <TableCell>Место рождения</TableCell>
                            <TableCell>Паспорт</TableCell>
                            <TableCell>Адрес</TableCell>
                            <TableCell>Телефон</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>ИНН</TableCell>
                            <TableCell>СНИЛС</TableCell>
                            <TableCell>Карта</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {data.map((row) => (
                            <TableRow key={row.id}>
                                <TableCell>{row.id}</TableCell>
                                <TableCell>{row.birthDate}</TableCell>
                                <TableCell>{row.birthPlace}</TableCell>
                                <TableCell>{row.passport}</TableCell>
                                <TableCell>{row.address}</TableCell>
                                <TableCell>{row.phone}</TableCell>
                                <TableCell>{row.email}</TableCell>
                                <TableCell>{row.inn}</TableCell>
                                <TableCell>{row.snils}</TableCell>
                                <TableCell>{row.card}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
}
export default AnonymizedPage;