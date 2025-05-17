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
    const [errors, setErrors] = useState({});

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

    const validate = () => {
        const newErrors = {};
        if (!/^\d{2}\.\d{2}\.\d{4}$/.test(formData.birthDate)) {
            newErrors.birthDate = 'Дата рождения должна быть в формате ДД.ММ.ГГГГ';
        }
        if (!/^[А-Яа-я\-\s]{2,}$/.test(formData.birthPlace)) {
            newErrors.birthPlace = 'Место рождения должно содержать только кириллицу и быть не короче 2 символов';
        }
        if (!/^\d{4}$/.test(passportSeries)) {
            newErrors.passportSeries = 'Серия паспорта должна состоять из 4 цифр';
        }
        if (!/^\d{6}$/.test(passportNumber)) {
            newErrors.passportNumber = 'Номер паспорта должен состоять из 6 цифр';
        }
        if (!/^.{5,}$/.test(formData.address)) {
            newErrors.address = 'Адрес должен быть не короче 5 символов';
        }
        if (!/^(89|\+79)\d{9}$/.test(formData.phone)) {
            newErrors.phone = 'Введите корректный номер телефона';
        }
        if (!/^[\w.-]+@(?:mail\.ru|gmail\.com|yandex\.ru|bk\.ru|outlook\.com|icloud\.com|rambler\.ru)$/.test(formData.email)) {
            newErrors.email = 'Email должен быть одного из разрешённых доменов';
        }
        if (!/^\d{12}$/.test(formData.inn)) {
            newErrors.inn = 'ИНН должен содержать 12 цифр';
        }
        if (!/^\d{3}-\d{3}-\d{3}\s\d{2}$/.test(formData.snils)) {
            newErrors.snils = 'СНИЛС должен быть в формате 123-456-789 00';
        }
        if (!/^\d{4}([ -]?\d{4}){3}$/.test(formData.card)) {
            newErrors.card = 'Номер карты должен состоять из 16 цифр';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validate()) return;

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
                    <TextField name="birthDate" label="Дата рождения (ДД.ММ.ГГГГ)"
                               value={formData.birthDate} onChange={handleChange} required
                               error={!!errors.birthDate} helperText={errors.birthDate} />

                    <TextField name="birthPlace" label="Место рождения"
                               value={formData.birthPlace} onChange={handleChange} required
                               error={!!errors.birthPlace} helperText={errors.birthPlace} />

                    <Stack direction="row" spacing={2}>
                        <TextField label="Серия паспорта (4 цифры)"
                                   value={passportSeries} onChange={handlePassportSeriesChange} required
                                   inputProps={{ maxLength: 4 }}
                                   error={!!errors.passportSeries} helperText={errors.passportSeries} />

                        <TextField label="Номер паспорта (6 цифр)"
                                   value={passportNumber} onChange={handlePassportNumberChange} required
                                   inputProps={{ maxLength: 6 }}
                                   error={!!errors.passportNumber} helperText={errors.passportNumber} />
                    </Stack>

                    <TextField name="address" label="Адрес" value={formData.address} onChange={handleChange} required
                               error={!!errors.address} helperText={errors.address} />

                    <TextField name="phone" label="Телефон" value={formData.phone} onChange={handleChange} required
                               error={!!errors.phone} helperText={errors.phone} />

                    <TextField name="email" label="Email" value={formData.email} onChange={handleChange} required
                               error={!!errors.email} helperText={errors.email} />

                    <TextField name="inn" label="ИНН" value={formData.inn} onChange={handleChange} required
                               error={!!errors.inn} helperText={errors.inn} />

                    <TextField name="snils" label="СНИЛС" value={formData.snils} onChange={handleChange} required
                               error={!!errors.snils} helperText={errors.snils} />

                    <TextField name="card" label="Номер карты" value={formData.card} onChange={handleChange} required
                               error={!!errors.card} helperText={errors.card} />

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
