-- Seed demo tenant: 'demo'
INSERT INTO projects (tenant_id,name,description) VALUES
('demo','Sample Project','Seeded project for demo');

INSERT INTO tasks (tenant_id,title,project_id,is_done)
SELECT 'demo','Try the API', p.id, false FROM projects p WHERE p.tenant_id='demo' LIMIT 1;

-- Create a demo user user@demo.local / password: demo
INSERT INTO users (tenant_id,email,password_hash)
VALUES ('demo','user@demo.local', '$2a$12$itC1vOqJYxZ9Qd9s3dZ4LuhF8lF7FVDnFf8k4b9u7e7f6WvQ0pOQW') ON CONFLICT DO NOTHING;
-- role
INSERT INTO user_roles (user_id, role)
SELECT u.id, 'USER' FROM users u WHERE u.tenant_id='demo' AND u.email='user@demo.local';
