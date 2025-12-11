-- SQL script to ensure posts integration works properly
-- This script ensures the necessary tables exist and have proper relationships

-- Ensure User table exists (should already exist from auth system)
-- CREATE TABLE IF NOT EXISTS User (
--     userId BIGINT AUTO_INCREMENT PRIMARY KEY,
--     fullName VARCHAR(255) NOT NULL,
--     email VARCHAR(255) UNIQUE NOT NULL,
--     password VARCHAR(255) NOT NULL,
--     phoneNumber VARCHAR(20),
--     gender ENUM('MALE', 'FEMALE'),
--     birthDate DATE,
--     isVerified BOOLEAN DEFAULT FALSE,
--     createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
-- );

-- Ensure Post table exists with proper structure
CREATE TABLE IF NOT EXISTS Post (
    postId BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    content TEXT NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES User(userId) ON DELETE CASCADE
);

-- Ensure Attachment table exists with proper structure
CREATE TABLE IF NOT EXISTS Attachment (
    attachId BIGINT AUTO_INCREMENT PRIMARY KEY,
    postId BIGINT NOT NULL,
    typeOfAttachment ENUM('Image', 'Video', 'Document') NOT NULL,
    attachmentURL VARCHAR(500) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (postId) REFERENCES Post(postId) ON DELETE CASCADE
);

-- Insert sample data if tables are empty (for testing)
INSERT IGNORE INTO Post (userId, content, createdAt) VALUES 
(1, 'Welcome to SmartLink! This is our first post on the platform.', NOW()),
(1, 'Excited to share updates and connect with professionals here.', NOW() - INTERVAL 1 HOUR),
(1, 'Looking forward to building meaningful connections.', NOW() - INTERVAL 2 HOUR);

-- Verify the structure
SELECT 'Post table structure:' as info;
DESCRIBE Post;

SELECT 'Attachment table structure:' as info;
DESCRIBE Attachment;

SELECT 'Sample posts:' as info;
SELECT postId, userId, LEFT(content, 50) as content_preview, createdAt FROM Post LIMIT 5;