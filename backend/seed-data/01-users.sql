-- ============================================================
-- Dummy Users for Animal Shelter (PostgreSQL)
-- Database: animal_shelter_users
-- Password for ALL users: password123
-- ============================================================

-- Clear existing data (optional - uncomment if needed)
-- DELETE FROM users;

INSERT INTO users (id, username, name, email, password, role, status, created_at)
VALUES
  ('a1b2c3d4-0001-4000-8000-000000000001', 'admin', 'Marko Petrovic', 'admin@shelter.com',
   '$2b$10$nEWlscV6X3/RfWML2zTIue846vAzVE63CazFSjRd2hum1oxe.1e/S',
   'Admin', 'Active', '2025-01-15 10:00:00'),

  ('a1b2c3d4-0002-4000-8000-000000000002', 'jelena_care', 'Jelena Jovanovic', 'jelena@shelter.com',
   '$2b$10$nEWlscV6X3/RfWML2zTIue846vAzVE63CazFSjRd2hum1oxe.1e/S',
   'Caretaker', 'Active', '2025-02-01 09:30:00'),

  ('a1b2c3d4-0003-4000-8000-000000000003', 'dr_nikola', 'Dr. Nikola Ilic', 'nikola.vet@shelter.com',
   '$2b$10$nEWlscV6X3/RfWML2zTIue846vAzVE63CazFSjRd2hum1oxe.1e/S',
   'Veterinarian', 'Active', '2025-02-10 11:00:00'),

  ('a1b2c3d4-0004-4000-8000-000000000004', 'ana_vol', 'Ana Markovic', 'ana@shelter.com',
   '$2b$10$nEWlscV6X3/RfWML2zTIue846vAzVE63CazFSjRd2hum1oxe.1e/S',
   'Volunteer', 'Active', '2025-03-01 08:00:00'),

  ('a1b2c3d4-0005-4000-8000-000000000005', 'stefan_care', 'Stefan Djordjevic', 'stefan@shelter.com',
   '$2b$10$nEWlscV6X3/RfWML2zTIue846vAzVE63CazFSjRd2hum1oxe.1e/S',
   'Caretaker', 'Inactive', '2025-03-15 14:00:00')

ON CONFLICT (id) DO NOTHING;
