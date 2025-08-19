-- User Entity MySQL Script
-- This script creates the users table and inserts sample data

-- Drop table if exists (for development/testing)
DROP TABLE IF EXISTS users;

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

-- Insert sample users
INSERT INTO users (
    email, 
    password, 
    first_name, 
    last_name, 
    gender, 
    birth_year, 
    birth_month, 
    phone_number, 
    language, 
    city, 
    country, 
    profile_picture, 
    bio, 
    created_at, 
    is_active, 
    role
) VALUES 
-- Sample user 1
(
    'john.doe@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'John',
    'Doe',
    'MALE',
    1990,
    3,
    '+1-555-0123',
    'English',
    'New York',
    'United States',
    'https://example.com/profiles/john.jpg',
    'Adventure seeker and travel enthusiast. Love exploring new cultures and meeting people from around the world.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 2
(
    'sarah.smith@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Sarah',
    'Smith',
    'FEMALE',
    1988,
    7,
    '+1-555-0456',
    'English',
    'Los Angeles',
    'United States',
    'https://example.com/profiles/sarah.jpg',
    'Passionate about sustainable travel and eco-friendly adventures. Always looking for meaningful experiences.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 3
(
    'mike.wilson@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Mike',
    'Wilson',
    'MALE',
    1992,
    11,
    '+1-555-0789',
    'English',
    'Chicago',
    'United States',
    'https://example.com/profiles/mike.jpg',
    'Photography enthusiast and backpacker. Love capturing moments and sharing stories.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 4
(
    'emma.johnson@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Emma',
    'Johnson',
    'FEMALE',
    1995,
    5,
    '+1-555-0321',
    'English',
    'Boston',
    'United States',
    'https://example.com/profiles/emma.jpg',
    'Foodie and culture lover. Always excited to try local cuisines and learn about different traditions.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 5
(
    'david.brown@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'David',
    'Brown',
    'MALE',
    1987,
    9,
    '+1-555-0654',
    'English',
    'Seattle',
    'United States',
    'https://example.com/profiles/david.jpg',
    'Tech professional who loves combining work and travel. Digital nomad lifestyle enthusiast.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 6 (Admin)
(
    'admin@travelplanassistant.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Admin',
    'User',
    'OTHER',
    1985,
    1,
    '+1-555-0987',
    'English',
    'San Francisco',
    'United States',
    'https://example.com/profiles/admin.jpg',
    'System administrator for Travel Plan Assistant platform.',
    NOW(),
    TRUE,
    'ADMIN'
),

-- Sample user 7 (International)
(
    'maria.garcia@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Maria',
    'Garcia',
    'FEMALE',
    1993,
    4,
    '+34-555-0123',
    'Spanish',
    'Madrid',
    'Spain',
    'https://example.com/profiles/maria.jpg',
    'Amante de la aventura y la exploración. Siempre buscando nuevas experiencias y amigos.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 8 (International)
(
    'pierre.dubois@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Pierre',
    'Dubois',
    'MALE',
    1991,
    8,
    '+33-555-0456',
    'French',
    'Paris',
    'France',
    'https://example.com/profiles/pierre.jpg',
    'Passionné de voyage et de découverte. J\'aime partager des expériences uniques.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 9 (International)
(
    'anna.mueller@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Anna',
    'Mueller',
    'FEMALE',
    1989,
    12,
    '+49-555-0789',
    'German',
    'Berlin',
    'Germany',
    'https://example.com/profiles/anna.jpg',
    'Reiseliebhaberin und Kulturbegeisterte. Immer auf der Suche nach neuen Abenteuern.',
    NOW(),
    TRUE,
    'USER'
),

-- Sample user 10 (International)
(
    'yuki.tanaka@example.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- password: password
    'Yuki',
    'Tanaka',
    'OTHER',
    1994,
    6,
    '+81-555-0321',
    'Japanese',
    'Tokyo',
    'Japan',
    'https://example.com/profiles/yuki.jpg',
    '旅行と文化体験が大好きです。新しい友達を作ることを楽しんでいます。',
    NOW(),
    TRUE,
    'USER'
);

-- Create a view for active users (useful for queries)
CREATE OR REPLACE VIEW active_users AS
SELECT 
    id,
    email,
    first_name,
    last_name,
    CONCAT(first_name, ' ', last_name) AS full_name,
    gender,
    birth_year,
    birth_month,
    TIMESTAMPDIFF(YEAR, CONCAT(birth_year, '-', birth_month, '-01'), CURDATE()) AS calculated_age,
    phone_number,
    language,
    city,
    country,
    profile_picture,
    bio,
    created_at,
    updated_at,
    last_login,
    role
FROM users 
WHERE is_active = TRUE;

-- Create a view for user statistics
CREATE OR REPLACE VIEW user_statistics AS
SELECT 
    COUNT(*) AS total_users,
    COUNT(CASE WHEN gender = 'MALE' THEN 1 END) AS male_users,
    COUNT(CASE WHEN gender = 'FEMALE' THEN 1 END) AS female_users,
    COUNT(CASE WHEN gender = 'OTHER' THEN 1 END) AS other_users,
    COUNT(CASE WHEN is_active = TRUE THEN 1 END) AS active_users,
    COUNT(CASE WHEN is_active = FALSE THEN 1 END) AS inactive_users,
    COUNT(CASE WHEN last_login IS NOT NULL THEN 1 END) AS users_with_login,
    AVG(TIMESTAMPDIFF(YEAR, CONCAT(birth_year, '-', birth_month, '-01'), CURDATE())) AS average_age,
    COUNT(DISTINCT country) AS unique_countries,
    COUNT(DISTINCT city) AS unique_cities,
    COUNT(DISTINCT language) AS unique_languages
FROM users;

-- Show sample data
SELECT 
    id,
    email,
    first_name,
    last_name,
    gender,
    birth_year,
    birth_month,
    TIMESTAMPDIFF(YEAR, CONCAT(birth_year, '-', birth_month, '-01'), CURDATE()) AS age,
    language,
    city,
    country,
    is_active,
    role,
    created_at
FROM users
ORDER BY id;

-- Show statistics
SELECT * FROM user_statistics; 