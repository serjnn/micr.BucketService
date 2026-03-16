CREATE TABLE bucket (
    id SERIAL PRIMARY KEY,
    client_id BIGINT unique NOT NULL
);
