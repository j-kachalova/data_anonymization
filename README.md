Настройка бд
docker exec -it postgres psql -U postgres
\du
CREATE USER myuser WITH PASSWORD 'mypassword';
GRANT ALL PRIVILEGES ON DATABASE data_anonymization TO myuser;
