INSERT INTO users (first_name, last_name, username, email, password, role, created_at, is_active, is_not_locked)
VALUES
('alcina', 'toto', 'admin', 'pepe@email.com', '$2a$10$kpCZJKj.gmCOVbxs1XNDY.w/cY405lZtoW7ciWRYPy2F38MayZzMS', 'ADMIN', '2023-06-18 22:50:06.035', true, true),
('Post', 'Man', 'postman', 'postman@example.com', '$2a$10$d3RKgivqOkHQw9b/x4Hnpec.7jwLdIpIYCDKAeiTLWAZAcsn7Z9GS', 'SUPER_ADMIN', '2023-06-23 10:56:53.167', true, true);

INSERT INTO factory
(name)
VALUES
('Test Factory');

INSERT INTO section
(factory_id, name)
VALUES
(1, 'OBO');

INSERT INTO counting_equipment
(code, alias, section_id, equipment_status, p_timer_communication_cycle)
VALUES
('OBO001', 'Máquina 1', 1, 0, 0),
('OBO002', 'Máquina 2', 1, 0, 0),
('OBO003', 'Máquina 3', 1, 0, 0),
('OBO004', 'Máquina 4', 1, 0, 0);

INSERT INTO equipment_output_alias
(alias)
VALUES
('OK'),
('NOTOK');

INSERT INTO equipment_output
(counting_equipment_id, code, equipment_output_alias_id, is_valid_for_production)
VALUES
(1, 'OBO001-001', 1, true),
(1, 'OBO001-002', 2, false),
(2, 'OBO002-001', 1, true),
(2, 'OBO002-002', 2, false),
(3, 'OBO003-001', 1, true),
(3, 'OBO003-002', 2, false),
(4, 'OBO004-001', 1, true),
(4, 'OBO004-002', 2, false);
