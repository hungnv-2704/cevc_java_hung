-- Script to update a user to ADMIN role
-- Replace 'your_email@example.com' with the actual user email

-- Update by email
UPDATE users SET role = 'ADMIN' WHERE email = 'your_email@example.com';

-- Or update by username
-- UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';

-- View all users and their roles
SELECT id, username, email, name, role, provider FROM users;
