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
('OBO001', 'Máquina 1', 1, 1, 60),
('OBO002', 'Máquina 2', 1, 1, 120),
('OBO003', 'Máquina 3', 1, 0, 120),
('OBO004', 'Máquina 4', 1, 0, 180),
('OBO005', 'Máquina 5', 1, 0, 60),
('OBO006', 'Máquina 6', 1, 0, 180),
('OBO007', 'Máquina 7', 1, 0, 120),
('OBO008', 'Máquina 8', 1, 0, 180);

INSERT INTO equipment_output_alias
(alias)
VALUES
('OK'),
('NOTOK');

INSERT INTO equipment_output
(counting_equipment_id, code, equipment_output_alias_id)
VALUES
(1, 'OBO001-001', 1),
(1, 'OBO001-002', 2),
(2, 'OBO002-001', 1),
(2, 'OBO002-002', 2),
(3, 'OBO003-001', 1),
(3, 'OBO003-002', 2),
(4, 'OBO004-001', 1),
(4, 'OBO004-002', 2),
(5, 'OBO005-001', 1),
(5, 'OBO005-002', 2),
(6, 'OBO006-001', 1),
(6, 'OBO006-002', 2),
(7, 'OBO007-001', 1),
(7, 'OBO007-002', 2),
(8, 'OBO008-001', 1),
(8, 'OBO008-002', 2);

INSERT INTO production_order
(equipment_id, code, target_amount, is_equipment_enabled, is_completed, created_at, input_batch, source, gauge, category, washing_process)
VALUES
(1, 'PO2301', 1000, true, false, '2023-05-09', 'POIN29Z12', '2123185BP', '45 x 24', 'Flor/Ext/Sup', 'MSN140X'),
(2, 'PO2302', 3000, true, true, '2023-05-11', 'TTON29Z12', '1166685BP', '45 x 24', 'Flor/Ext/Ope', 'MOC145X');


INSERT INTO counter_record (equipment_output_id, equipment_output_alias, real_value, computed_value, production_order_id, registered_at)
VALUES
(1, 'OK', 250, 0, 1, '2023-06-18 22:52:51.198'),
(2, 'NOTOK', 220, 0, 1, '2023-06-18 22:52:51.21'),
(1, 'OK', 350, 100, 1, '2023-06-18 22:55:53.873'),
(2, 'NOTOK', 320, 100, 1, '2023-06-18 22:55:53.887'),
(3, 'OK', 350, 0, 2, '2023-06-18 22:58:51.198'),
(4, 'NOTOK', 520, 0, 2, '2023-06-18 22:58:51.21'),
(1, 'OK', 450, 200, 1, '2023-06-18 22:59:53.873'),
(2, 'NOTOK', 520, 500, 1, '2023-06-18 22:59:53.887'),
(1, 'OK', 550, 300, 1, '2023-06-18 23:01:53.873'),
(2, 'NOTOK', 730, 510, 1, '2023-06-18 23:01:53.887'),
(1, 'OK', 640, 390, 1, '2023-06-18 23:05:53.873'),
(2, 'NOTOK', 780, 560, 1, '2023-06-18 23:05:53.887'),
(3, 'OK', 450, 100, 2, '2023-06-18 23:06:51.198'),
(4, 'NOTOK', 720, 200, 2, '2023-06-18 23:06:51.21'),
(1, 'OK', 750, 500, 1, '2023-06-18 23:07:53.873'),
(2, 'NOTOK', 820, 600, 1, '2023-06-18 23:07:53.887'),
(1, 'OK', 790, 540, 1, '2023-06-18 23:09:53.873'),
(2, 'NOTOK', 850, 630, 1, '2023-06-18 23:09:53.887');