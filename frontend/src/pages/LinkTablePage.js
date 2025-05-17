import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
    Table, TableBody, TableCell, TableContainer,
    TableHead, TableRow, Paper, Typography, Box,
    FormGroup, FormControlLabel, Checkbox, TextField, Button
} from '@mui/material';
import {Link, useNavigate} from 'react-router-dom';

const ALL_COLUMNS = [
    { key: 'birthDate', label: 'Дата рождения' },
    { key: 'birthPlace', label: 'Место рождения' },
    { key: 'passport', label: 'Паспорт' },
    { key: 'address', label: 'Адрес' },
    { key: 'phone', label: 'Телефон' },
    { key: 'email', label: 'Email' },
    { key: 'inn', label: 'ИНН' },
    { key: 'snils', label: 'СНИЛС' },
    { key: 'card', label: 'Карта' }
];

const LinkTablePage = () => {
    const [links, setLinks] = useState([]);
    const [error, setError] = useState('');
    const [selectedColumns, setSelectedColumns] = useState(['passport']); // по умолчанию паспорт
    const [searchTerm, setSearchTerm] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        axios.get('/api/anonymization/all-link')
            .then(res => setLinks(res.data))
            .catch(err => setError('Ошибка при загрузке данных: ' + err.message));
    }, []);

    const handleRowClick = (id) => {
        navigate(`/link-details/${id}`);
    };

    const handleColumnToggle = (key) => {
        setSelectedColumns(prev =>
            prev.includes(key)
                ? prev.filter(k => k !== key)
                : [...prev, key]
        );
    };

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
    };

    // Фильтрация данных по поиску
    const filteredLinks = links.filter(link => {
        if (!searchTerm.trim()) return true; // если пустой поиск - показываем всё

        const term = searchTerm.toLowerCase();

        return selectedColumns.some(col => {
            const originalValue = link.originalData?.[col]?.toString().toLowerCase() || '';
            const anonymizedValue = link.anonymizedData?.[col]?.toString().toLowerCase() || '';

            return originalValue.includes(term) || anonymizedValue.includes(term);
        });
    });

    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h5" gutterBottom>Связанные данные (нажмите на запись для деталей)</Typography>
            <Button variant="outlined" component={Link} to="/" sx={{ mb: 2 }}>
                Назад
            </Button>
            <Box sx={{ mb: 2 }}>
                <Typography variant="subtitle1" gutterBottom>Выберите столбцы для отображения:</Typography>
                <FormGroup row>
                    {ALL_COLUMNS.map(col => (
                        <FormControlLabel
                            key={col.key}
                            control={
                                <Checkbox
                                    checked={selectedColumns.includes(col.key)}
                                    onChange={() => handleColumnToggle(col.key)}
                                />
                            }
                            label={col.label}
                        />
                    ))}
                </FormGroup>
            </Box>

            <TextField
                label="Поиск по выбранным столбцам"
                variant="outlined"
                size="small"
                fullWidth
                sx={{ mb: 2 }}
                value={searchTerm}
                onChange={handleSearchChange}
            />

            {error && <Typography color="error">{error}</Typography>}

            <TableContainer component={Paper}>
                <Table size="small" stickyHeader>
                    <TableHead>
                        <TableRow>
                            {selectedColumns.map(col => (
                                <React.Fragment key={col}>
                                    <TableCell>{ALL_COLUMNS.find(c => c.key === col)?.label} (Original)</TableCell>
                                    <TableCell>{ALL_COLUMNS.find(c => c.key === col)?.label} (Anonymized)</TableCell>
                                </React.Fragment>
                            ))}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {filteredLinks.map(link => (
                            <TableRow
                                key={link.id}
                                hover
                                sx={{ cursor: 'pointer' }}
                                onClick={() => handleRowClick(link.id)}
                            >
                                {selectedColumns.map(col => (
                                    <React.Fragment key={col}>
                                        <TableCell>{link.originalData?.[col] ?? '—'}</TableCell>
                                        <TableCell>{link.anonymizedData?.[col] ?? '—'}</TableCell>
                                    </React.Fragment>
                                ))}
                            </TableRow>
                        ))}
                        {filteredLinks.length === 0 && (
                            <TableRow>
                                <TableCell colSpan={selectedColumns.length * 2} align="center">
                                    Нет данных, соответствующих запросу
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Box>
    );
};

export default LinkTablePage;
