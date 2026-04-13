-- V3: Create products table
-- Adds a simple products catalogue table.

CREATE TABLE IF NOT EXISTS products (
    id          SERIAL         PRIMARY KEY,
    name        VARCHAR(200)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);
