-- =====================================================
-- Notification Table Creation Script
-- Created: 2025-01-15
-- Description: Table to store email notification data and history
-- =====================================================

-- Create notifications table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL COMMENT 'Email address of the notification recipient',
    recipient_name VARCHAR(255) COMMENT 'Name of the notification recipient',
    subject VARCHAR(500) NOT NULL COMMENT 'Email subject line',
    content TEXT COMMENT 'Email content (HTML or plain text)',
    notification_type ENUM(
        'TRAVEL_PLAN_START_REMINDER',
        'TRAVEL_PLAN_COMPLETION', 
        'APPLICATION_STATUS_UPDATE',
        'INVITATION_NOTIFICATION',
        'CUSTOM_NOTIFICATION',
        'SYSTEM_NOTIFICATION'
    ) NOT NULL COMMENT 'Type of notification being sent',
    status ENUM(
        'PENDING',
        'SENT', 
        'FAILED',
        'RETRYING',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING' COMMENT 'Current status of the notification',
    sent_at DATETIME COMMENT 'Timestamp when notification was successfully sent',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when notification was created',
    error_message TEXT COMMENT 'Error message if notification failed to send',
    retry_count INT DEFAULT 0 COMMENT 'Number of retry attempts made',
    max_retries INT DEFAULT 3 COMMENT 'Maximum number of retry attempts allowed',
    
    -- Foreign key references
    travel_plan_id BIGINT COMMENT 'Reference to travel_plans table',
    sender_user_id BIGINT COMMENT 'Reference to users table (who sent the notification)',
    recipient_user_id BIGINT COMMENT 'Reference to users table (who received the notification)',
    
    -- Indexes for better performance
    INDEX idx_recipient_email (recipient_email),
    INDEX idx_recipient_user_id (recipient_user_id),
    INDEX idx_sender_user_id (sender_user_id),
    INDEX idx_travel_plan_id (travel_plan_id),
    INDEX idx_status (status),
    INDEX idx_notification_type (notification_type),
    INDEX idx_created_at (created_at),
    INDEX idx_sent_at (sent_at),
    INDEX idx_retry_count (retry_count),
    
    -- Composite indexes for common queries
    INDEX idx_recipient_status (recipient_email, status),
    INDEX idx_user_type (recipient_user_id, notification_type),
    INDEX idx_plan_type (travel_plan_id, notification_type),
    INDEX idx_status_created (status, created_at)
    
    -- Foreign key constraints (optional - uncomment if you want strict referential integrity)
    -- FOREIGN KEY (travel_plan_id) REFERENCES travel_plans(id) ON DELETE SET NULL,
    -- FOREIGN KEY (sender_user_id) REFERENCES users(id) ON DELETE SET NULL,
    -- FOREIGN KEY (recipient_user_id) REFERENCES users(id) ON DELETE SET NULL
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Stores email notification data and history for the travel plan assistant application';

-- =====================================================
-- Sample Data Insertion (Optional)
-- =====================================================

-- Insert sample notification data for testing
INSERT INTO notifications (
    recipient_email, 
    recipient_name, 
    subject, 
    content, 
    notification_type, 
    status, 
    sent_at, 
    travel_plan_id, 
    sender_user_id, 
    recipient_user_id
) VALUES 
(
    'john.doe@example.com',
    'John Doe',
    'Travel Plan Starting Soon: Tokyo Adventure',
    '<html><body><h1>Your travel plan is starting soon!</h1></body></html>',
    'TRAVEL_PLAN_START_REMINDER',
    'SENT',
    NOW(),
    1,
    2,
    1
),
(
    'jane.smith@example.com',
    'Jane Smith', 
    'You\'re Invited to Join: Paris Getaway',
    '<html><body><h1>You\'re invited to join a travel plan!</h1></body></html>',
    'INVITATION_NOTIFICATION',
    'PENDING',
    NULL,
    2,
    1,
    3
),
(
    'mike.wilson@example.com',
    'Mike Wilson',
    'Application Update: London Trip',
    '<html><body><h1>Your application has been accepted!</h1></body></html>',
    'APPLICATION_STATUS_UPDATE',
    'SENT',
    NOW(),
    3,
    4,
    2
);

-- =====================================================
-- Useful Queries for Notification Management
-- =====================================================

-- View all notifications
-- SELECT * FROM notifications ORDER BY created_at DESC;

-- View pending notifications
-- SELECT * FROM notifications WHERE status = 'PENDING' ORDER BY created_at ASC;

-- View failed notifications that can be retried
-- SELECT * FROM notifications WHERE status = 'FAILED' AND retry_count < max_retries ORDER BY created_at ASC;

-- View notifications for a specific user
-- SELECT * FROM notifications WHERE recipient_user_id = 1 ORDER BY created_at DESC;

-- View notifications for a specific travel plan
-- SELECT * FROM notifications WHERE travel_plan_id = 1 ORDER BY created_at DESC;

-- Count notifications by status
-- SELECT status, COUNT(*) as count FROM notifications GROUP BY status;

-- Count notifications by type
-- SELECT notification_type, COUNT(*) as count FROM notifications GROUP BY notification_type;

-- View notifications with errors
-- SELECT * FROM notifications WHERE error_message IS NOT NULL ORDER BY created_at DESC;

-- View recent notifications (last 7 days)
-- SELECT * FROM notifications WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY) ORDER BY created_at DESC;

-- =====================================================
-- Maintenance Queries
-- =====================================================

-- Delete old notifications (older than 90 days) - run periodically
-- DELETE FROM notifications WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- Update failed notifications to retry status
-- UPDATE notifications SET status = 'RETRYING' WHERE status = 'FAILED' AND retry_count < max_retries;

-- Reset retry count for old failed notifications
-- UPDATE notifications SET retry_count = 0 WHERE status = 'FAILED' AND created_at < DATE_SUB(NOW(), INTERVAL 1 DAY);

-- =====================================================
-- Performance Optimization
-- =====================================================

-- Analyze table for query optimization
-- ANALYZE TABLE notifications;

-- Check table size and index usage
-- SHOW TABLE STATUS LIKE 'notifications';

-- View index usage statistics
-- SHOW INDEX FROM notifications;
