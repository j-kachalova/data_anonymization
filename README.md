Настройка бд
docker exec -it postgres psql -U postgres
\du
CREATE USER myuser WITH PASSWORD 'mypassword';
GRANT ALL PRIVILEGES ON DATABASE data_anonymization TO myuser;
зайти в бд в докере
psql -U myuser -d data_anonymization
\dt

frontend
npm install

загрузка job
http://localhost:8081/#/submit 

