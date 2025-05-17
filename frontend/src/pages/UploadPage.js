import React, { useState } from 'react';
import axios from 'axios';
import {
    TextField, Box, Typography, Button, Stack
} from '@mui/material';
import { Link } from "react-router-dom";

function UploadPage() {
    const [formData, setFormData] = useState({
        birthDate: '', birthPlace: '', address: '',
        phone: '', email: '', inn: '', snils: '', card: ''
    });
    const [passportSeries, setPassportSeries] = useState('');
    const [passportNumber, setPassportNumber] = useState('');
    const [response, setResponse] = useState('');

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handlePassportSeriesChange = (e) => {
        const value = e.target.value.replace(/\D/g, '').slice(0, 4);
        setPassportSeries(value);
    };

    const handlePassportNumberChange = (e) => {
        const value = e.target.value.replace(/\D/g, '').slice(0, 6);
        setPassportNumber(value);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const fullPassport = `${passportSeries} ${passportNumber}`;
        const dataToSend = {
            ...formData,
            passport: fullPassport
        };

        try {
            const res = await axios.post('/api/anonymization/anonymizeData', dataToSend);
            setResponse(JSON.stringify(res.data, null, 2));
        } catch (err) {
            setResponse('Ошибка: ' + err.message);
        }
    };

    return (
        <Box sx={{ p: 4 }}>
            <Typography variant="h5" gutterBottom>Форма загрузки данных</Typography>
            <Button variant="outlined" component={Link} to="/" sx={{ mb: 2 }}>
                Назад
            </Button>
            <form onSubmit={handleSubmit}>
                <Stack spacing={2}>
                    <TextField name="birthDate" label="Дата рождения (ДД.ММ.ГГГГ)" value={formData.birthDate} onChange={handleChange} required />
                    <TextField name="birthPlace" label="Место рождения" value={formData.birthPlace} onChange={handleChange} required />
                    <Stack direction="row" spacing={2}>
                        <TextField
                            label="Серия паспорта (4 цифры)"
                            value={passportSeries}
                            onChange={handlePassportSeriesChange}
                            required
                            inputProps={{ maxLength: 4 }}
                        />
                        <TextField
                            label="Номер паспорта (6 цифр)"
                            value={passportNumber}
                            onChange={handlePassportNumberChange}
                            required
                            inputProps={{ maxLength: 6 }}
                        />
                    </Stack>
                    <TextField name="address" label="Адрес" value={formData.address} onChange={handleChange} required />
                    <TextField name="phone" label="Телефон" value={formData.phone} onChange={handleChange} required />
                    <TextField name="email" label="Email" value={formData.email} onChange={handleChange} required />
                    <TextField name="inn" label="ИНН" value={formData.inn} onChange={handleChange} required />
                    <TextField name="snils" label="СНИЛС" value={formData.snils} onChange={handleChange} required />
                    <TextField name="card" label="Номер карты" value={formData.card} onChange={handleChange} required />
                    <Button type="submit" variant="contained" color="primary">Отправить</Button>
                </Stack>
            </form>
            {response && (
                <Box mt={4}>
                    <Typography variant="h6">Ответ от сервера:</Typography>
                    <pre>{response}</pre>
                </Box>
            )}
        </Box>
    );
}

export default UploadPage;
