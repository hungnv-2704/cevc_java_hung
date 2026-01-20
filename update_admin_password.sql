-- Update admin password to "admin123"
-- BCrypt hash of "admin123" is: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2qKZ9Fz6cCPk/hP5lOjdDqq

UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2qKZ9Fz6cCPk/hP5lOjdDqq'
WHERE username = 'admin';

-- Check the result
SELECT id, username, name, email, role, 
       CASE WHEN password IS NOT NULL THEN 'Password is set' ELSE 'No password' END as password_status
FROM users 
WHERE username = 'admin';
