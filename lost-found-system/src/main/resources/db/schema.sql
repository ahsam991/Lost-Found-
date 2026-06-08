-- Create database
CREATE DATABASE IF NOT EXISTS campus_lost_found;
USE campus_lost_found;

-- Drop existing views/procedures/triggers if they exist to allow clean re-runs
DROP VIEW IF EXISTS dashboard_stats;
DROP PROCEDURE IF EXISTS GetMonthlyReport;
DROP TRIGGER IF EXISTS after_lost_item_update;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    student_id VARCHAR(20) UNIQUE,
    department VARCHAR(50),
    campus_location VARCHAR(50),
    role ENUM('student', 'staff', 'security', 'admin') DEFAULT 'student',
    profile_pic VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_student_id (student_id)
);

-- Lost items table
CREATE TABLE IF NOT EXISTS lost_items (
    lost_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    color VARCHAR(30),
    serial_number VARCHAR(100),
    description TEXT NOT NULL,
    location_lost VARCHAR(100) NOT NULL,
    location_lat DECIMAL(10, 8),
    location_lng DECIMAL(11, 8),
    date_lost DATE NOT NULL,
    estimated_value DECIMAL(10, 2),
    photos TEXT, -- Stored as comma-separated or JSON list of paths
    status ENUM('active', 'matched', 'recovered', 'closed', 'expired') DEFAULT 'active',
    match_score DECIMAL(5, 2),
    matched_item_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_date_lost (date_lost),
    INDEX idx_user (user_id)
);

-- Found items table
CREATE TABLE IF NOT EXISTS found_items (
    found_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    color VARCHAR(30),
    serial_number VARCHAR(100),
    description TEXT NOT NULL,
    location_found VARCHAR(100) NOT NULL,
    location_lat DECIMAL(10, 8),
    location_lng DECIMAL(11, 8),
    date_found DATE NOT NULL,
    safe_keeping_location VARCHAR(100),
    photos TEXT, -- Stored as comma-separated or JSON list of paths
    status ENUM('available', 'verification', 'claimed', 'returned', 'archived') DEFAULT 'available',
    claimed_by_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (claimed_by_id) REFERENCES users(user_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_date_found (date_found),
    INDEX idx_user (user_id)
);

-- Claims table
CREATE TABLE IF NOT EXISTS claims (
    claim_id INT PRIMARY KEY AUTO_INCREMENT,
    claimant_id INT NOT NULL,
    found_item_id INT NOT NULL,
    lost_item_id INT,
    proof_documents TEXT, -- comma-separated paths
    security_answers TEXT, -- JSON or serialized text
    qr_code_path VARCHAR(255),
    status ENUM('pending', 'review', 'additional_info', 'approved', 'rejected', 'completed') DEFAULT 'pending',
    rejection_reason TEXT,
    verification_notes TEXT,
    reviewed_by INT NULL,
    reviewed_at TIMESTAMP NULL,
    admin_approved_by INT NULL,
    admin_approved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (claimant_id) REFERENCES users(user_id),
    FOREIGN KEY (found_item_id) REFERENCES found_items(found_id),
    FOREIGN KEY (lost_item_id) REFERENCES lost_items(lost_id),
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id),
    FOREIGN KEY (admin_approved_by) REFERENCES users(user_id),
    INDEX idx_status (status),
    INDEX idx_claimant (claimant_id),
    INDEX idx_created (created_at)
);

-- Handovers table
CREATE TABLE IF NOT EXISTS handovers (
    handover_id INT PRIMARY KEY AUTO_INCREMENT,
    claim_id INT NOT NULL,
    security_officer_id INT NOT NULL,
    qr_scanned_at TIMESTAMP NULL,
    signature_path VARCHAR(255),
    id_verified BOOLEAN DEFAULT FALSE,
    handover_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    FOREIGN KEY (claim_id) REFERENCES claims(claim_id),
    FOREIGN KEY (security_officer_id) REFERENCES users(user_id),
    INDEX idx_claim (claim_id),
    INDEX idx_handover_date (handover_date)
);

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    type ENUM('match', 'claim_update', 'reminder', 'system') NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    related_id INT,
    is_read BOOLEAN DEFAULT FALSE,
    email_sent BOOLEAN DEFAULT FALSE,
    sms_sent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created (created_at)
);

-- Audit logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_user_action (user_id, action),
    INDEX idx_created (created_at)
);

-- Views for analytics
CREATE VIEW dashboard_stats AS
SELECT 
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM lost_items WHERE status = 'active') AS active_lost,
    (SELECT COUNT(*) FROM found_items WHERE status = 'available') AS available_found,
    (SELECT COUNT(*) FROM claims WHERE status = 'pending') AS pending_claims,
    (SELECT COUNT(*) FROM handovers) AS total_returned;

-- Stored procedure for monthly report
DELIMITER //
CREATE PROCEDURE GetMonthlyReport(IN report_month DATE)
BEGIN
    SELECT 
        DATE_FORMAT(created_at, '%Y-%m') AS month,
        COUNT(*) AS total_lost,
        SUM(CASE WHEN status = 'recovered' THEN 1 ELSE 0 END) AS recovered
    FROM lost_items
    WHERE DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(report_month, '%Y-%m')
    GROUP BY DATE_FORMAT(created_at, '%Y-%m');
END //
DELIMITER ;

-- Trigger for audit logging
DELIMITER //
CREATE TRIGGER after_lost_item_update
AFTER UPDATE ON lost_items
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (user_id, action, entity_type, entity_id, old_value, new_value)
    VALUES (NEW.user_id, 'UPDATE', 'lost_item', NEW.lost_id, 
            CONCAT('{"status":"', OLD.status, '"}'), 
            CONCAT('{"status":"', NEW.status, '"}'));
END //
DELIMITER ;
