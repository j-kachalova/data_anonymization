import React from 'react';
import { Link } from 'react-router-dom';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';

function Home() {
    return (
        <div style={{ padding: '2rem' }}>
            <h1></h1>
            <Stack direction="column" spacing={2}>
                <Button variant="contained" color="primary" component={Link} to="/upload">
                    Загрузить данные
                </Button>
                <Button variant="contained" color="secondary" component={Link} to="/uploaded">
                    Загруженные данные
                </Button>
                <Button variant="contained" color="success" component={Link} to="/anonymized">
                    Обезличенные данные
                </Button>
                <Button variant="contained" color="success" component={Link} to="/links">
                    Все данные
                </Button>
            </Stack>
        </div>
    );
}

export default Home;