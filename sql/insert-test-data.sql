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
('OBO001', 'Máquina 1', 1, 2, 60),
('OBO002', 'Máquina 2', 1, 2, 120),
('OBO003', 'Máquina 3', 1, 2, 120),
('OBO004', 'Máquina 4', 1, 2, 180);

INSERT INTO equipment_output_alias
(alias)
VALUES
('OK'),
('NOTOK'),
('OK2'),
('NOTOK2');

INSERT INTO equipment_output
(counting_equipment_id, code, equipment_output_alias_id)
VALUES
(1, 'OBO001-001', 1),
(1, 'OBO001-002', 2),
(1, 'OBO001-003', 3),
(1, 'OBO001-004', 4),
(2, 'OBO002-001', 1),
(2, 'OBO002-002', 2),
(3, 'OBO003-001', 1),
(3, 'OBO003-002', 2),
(3, 'OBO003-003', 3),
(3, 'OBO003-004', 4),
(4, 'OBO004-001', 1),
(4, 'OBO004-002', 2),
(4, 'OBO004-003', 3),
(4, 'OBO004-004', 4);

INSERT INTO production_order
(equipment_id, code, target_amount, is_equipment_enabled, is_completed, created_at, input_batch, source, gauge, category, washing_process)
VALUES
(1, 'PO2301', 1000, true, false, '2023-05-09', 'POIN29Z12', '2123185BP', '45 x 24', 'Flor/Ext/Sup', 'MSN140X'),
(2, 'PO2302', 3000, true, true, '2023-05-11', 'TTON29Z12', '1166685BP', '45 x 24', 'Flor/Ext/Ope', 'MOC145X');


INSERT INTO counter_record (equipment_output_id, equipment_output_alias, real_value, computed_value, production_order_id, registered_at)
VALUES
(1, 'OK', 250, 0, 1, '2023-06-18 22:52:51.198'),
(2, 'NOTOK', 220, 0, 1, '2023-06-18 22:52:51.21'),
(3, 'OK2', 65435, 0, 1, '2023-06-18 22:52:51.221'),
(4, 'NOTOK2', 430, 0, 1, '2023-06-18 22:52:51.232'),
(1, 'OK', 350, 100, 1, '2023-06-18 22:55:53.873'),
(2, 'NOTOK', 320, 100, 1, '2023-06-18 22:55:53.887'),
(3, 'OK2', 100, 201, 1, '2023-06-18 22:55:53.902'),
(4, 'NOTOK2', 600, 170, 1, '2023-06-18 22:55:53.914'),
(5, 'OK', 350, 0, 2, '2023-06-18 22:58:51.198'),
(6, 'NOTOK', 520, 0, 2, '2023-06-18 22:58:51.21'),
(1, 'OK', 450, 100, 1, '2023-06-18 22:59:53.873'),
(2, 'NOTOK', 520, 300, 1, '2023-06-18 22:59:53.887'),
(3, 'OK2', 600, 401, 1, '2023-06-18 22:59:53.902'),
(4, 'NOTOK2', 800, 270, 1, '2023-06-18 22:59:53.914');

