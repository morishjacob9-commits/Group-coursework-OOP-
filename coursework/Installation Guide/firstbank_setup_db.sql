CREATE DATABASE IF NOT EXISTS FirstBankDB;
USE FirstBankDB;

-- Table to track sequential counters per branch per year
CREATE TABLE IF NOT EXISTS AccountSequences (
    branch_code VARCHAR(3) NOT NULL,
    year_val INT NOT NULL,
    current_counter INT NOT NULL DEFAULT 0,
    PRIMARY KEY (branch_code, year_val)
);

-- Table to hold account records
CREATE TABLE IF NOT EXISTS Accounts (
    account_number VARCHAR(30) PRIMARY KEY,
    first_name VARCHAR(30) NOT NULL,
    last_name VARCHAR(30) NOT NULL,
    nin VARCHAR(14) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    dob DATE NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    branch VARCHAR(20) NOT NULL,
    opening_deposit DECIMAL(12, 2) NOT NULL
);