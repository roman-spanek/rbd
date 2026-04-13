-- V2: Add email column to users table
-- Extends the 'users' table with an email address field.

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email VARCHAR(255) UNIQUE;
