-- USERS TABLE
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    age INT NOT NULL,
    phone_number VARCHAR(50),
    main_language VARCHAR(100) NOT NULL,
    additional_languages VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    profile_picture VARCHAR(255),
    bio VARCHAR(1000),
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    last_login DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    role VARCHAR(20) NOT NULL
);

-- TRAVEL PLANS TABLE
CREATE TABLE travel_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    plan_type VARCHAR(50) NOT NULL,
    origin_location VARCHAR(255),
    destination VARCHAR(255) NOT NULL,
    destination_timezone VARCHAR(100),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    transport_type VARCHAR(50),
    accommodation_type VARCHAR(255),
    accommodation_details VARCHAR(1000),
    estimated_budget DECIMAL(10,2),
    optional_expenses VARCHAR(1000),
    max_members INT NOT NULL,
    min_members INT DEFAULT 1,
    gender_preference VARCHAR(50),
    min_age INT,
    max_age INT,
    required_languages VARCHAR(255),
    communication_languages VARCHAR(255),
    plan_status VARCHAR(50) NOT NULL,
    visibility VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    started_at DATETIME,
    completed_at DATETIME,
    cancelled_at DATETIME,
    cancellation_reason VARCHAR(500),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

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