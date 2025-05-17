import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import {
    Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Paper, Typography, Box,
    Button
} from '@mui/material';

const fieldLabels = {
    birthDate: 'Дата рождения',
    birthPlace: 'Место рождения',
    passport: 'Паспорт',
    address: 'Адрес',
    phone: 'Телефон',
    email: 'Email',
    inn: 'ИНН',
    snils: 'СНИЛС',
    card: 'Номер карты',
};

const LinkDetailsPage = () => {
    const { id } = useParams();
    const [link, setLink] = useState(null);
    const [error, setError] = useState('');

    useEffect(() => {
        axios.get(`/api/anonymization/link/${id}`)
            .then(res => setLink(res.data))
            .catch(err => setError('Ошибка при загрузке данных: ' + err.message));
    }, [id]);

    if (error) {
        return <Typography color="error">{error}</Typography>;
    }

    if (!link) {
        return <Typography>Загрузка данных...</Typography>;
    }

    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h5" gutterBottom>Детали связи по паспорту</Typography>
            <Button variant="outlined" component={Link} to="/links" sx={{ mb: 2 }}>
                Назад к списку
            </Button>

            <TableContainer component={Paper}>
                <Table size="small" stickyHeader>
                    <TableHead>
                        <TableRow>
                            <TableCell>Поле</TableCell>
                            <TableCell>Исходные данные</TableCell>
                            <TableCell>Обезличенные данные</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {Object.keys(fieldLabels).map(field => (
                            <TableRow key={field}>
                                <TableCell sx={{ fontWeight: 'bold' }}>{fieldLabels[field]}</TableCell>
                                <TableCell>{link.originalData?.[field] || '—'}</TableCell>
                                <TableCell>{link.anonymizedData?.[field] || '—'}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default LinkDetailsPage;
