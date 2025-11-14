-- 2️⃣ Insere usuários iniciais
INSERT INTO users (name, email, phone, password, role)
VALUES
('Admin Sovrano', 'admin@sovrano.com', '(11) 99999-0000', 'admin123', 'ADMIN'),
('Gabriel Dias', 'dias@sovrano.com', '(11) 98888-1111', 'dias123', 'CUSTOMER'),
('Maria Costa', 'maria@sovrano.com', '(11) 97777-2222', 'maria123', 'CUSTOMER');

-- 3️⃣ Insere mesas do restaurante
INSERT INTO tables (capacity, available)
VALUES
(2, TRUE),
(2, TRUE),
(2, TRUE),
(2, TRUE),
(4, TRUE),
(4, TRUE),
(4, TRUE),
(4, TRUE),
(6, TRUE),
(6, TRUE);

-- 4️⃣ Exemplo de reserva inicial (opcional)
INSERT INTO reservations (reservation_datetime, people_count, status, user_id, table_id)
VALUES
('2025-11-15 20:00:00', 2, 'CONFIRMED', 2, 1);
