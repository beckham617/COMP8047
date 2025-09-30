-- Create users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    birth_year INT NOT NULL,
    birth_month INT NOT NULL,
    phone_number VARCHAR(50),
    language VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    profile_picture VARCHAR(255),
    bio VARCHAR(1000),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    last_login DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    
    -- Add indexes for better performance
    INDEX idx_email (email),
    INDEX idx_city (city),
    INDEX idx_country (country),
    INDEX idx_language (language),
    INDEX idx_gender (gender),
    INDEX idx_birth_year (birth_year),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at)
);

CREATE TABLE travel_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    plan_type VARCHAR(20) NOT NULL,
    category VARCHAR(30) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    origin_country VARCHAR(100),
    origin_city VARCHAR(100),
    destination_country VARCHAR(100),
    destination_city VARCHAR(100),
    transportation VARCHAR(20),
    accommodation VARCHAR(20),
    max_members INT NOT NULL,
    description VARCHAR(2000) NOT NULL,
    images JSON,
    gender VARCHAR(20),
    age_min INT,
    age_max INT,
    language VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    cancelled_at DATETIME,
    cancellation_reason VARCHAR(500),
    -- Relationships (foreign keys)
    FOREIGN KEY (owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- USER_PLAN_STATUS TABLE
CREATE TABLE user_plan_status (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    travel_plan_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    applied_at DATETIME,
    invited_at DATETIME,
    accepted_at DATETIME,
    refused_at DATETIME,
    response_message VARCHAR(500),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plans(id)
);

-- CHAT MESSAGES TABLE
CREATE TABLE chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content VARCHAR(2000) NOT NULL,
    sender_id BIGINT NOT NULL,
    travel_plan_id BIGINT NOT NULL,
    message_type VARCHAR(20) NOT NULL,
    is_system_message BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    edited_at DATETIME,
    is_edited BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at DATETIME,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plans(id)
);

-- MESSAGE READ RECEIPTS TABLE
CREATE TABLE message_read_receipts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    message_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    read_at DATETIME NOT NULL,
    FOREIGN KEY (message_id) REFERENCES chat_messages(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- POLLS TABLE
CREATE TABLE polls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    question VARCHAR(500) NOT NULL,
    travel_plan_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    is_multiple_choice BOOLEAN DEFAULT FALSE,
    is_anonymous BOOLEAN DEFAULT FALSE,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    closed_at DATETIME,
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plans(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- POLL OPTIONS TABLE
CREATE TABLE poll_options (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    text VARCHAR(200) NOT NULL,
    poll_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (poll_id) REFERENCES polls(id)
);

-- POLL VOTES TABLE
CREATE TABLE poll_votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    poll_id BIGINT NOT NULL,
    poll_option_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    voted_at DATETIME NOT NULL,
    FOREIGN KEY (poll_id) REFERENCES polls(id),
    FOREIGN KEY (poll_option_id) REFERENCES poll_options(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- SHARED EXPENSES TABLE
CREATE TABLE shared_expenses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(500) NOT NULL,
    purpose VARCHAR(200) NOT NULL,
    travel_plan_id BIGINT NOT NULL,
    paid_by BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'CAD',
    expense_date DATETIME NOT NULL,
    payment_method VARCHAR(100),
    receipt_image VARCHAR(255),
    notes VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    settled_at DATETIME,
    FOREIGN KEY (travel_plan_id) REFERENCES travel_plans(id),
    FOREIGN KEY (paid_by) REFERENCES users(id)
);

-- EXPENSE ALLOCATIONS TABLE
CREATE TABLE expense_allocations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shared_expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    paid_at DATETIME,
    payment_method VARCHAR(100),
    notes VARCHAR(500),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (shared_expense_id) REFERENCES shared_expenses(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
); 


drop table chat_messages;
drop table message_read_receipts;
drop table poll_votes;
drop table poll_options;
drop table polls;
drop table expense_allocations;
drop table shared_expenses;
drop table user_plan_status;
drop table travel_plans;
drop table users;