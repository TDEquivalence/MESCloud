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
('OBO004', 'Máquina 4', 1, 0, 0),
('OBO005', 'Máquina 5', 1, 0, 0),
('OBO006', 'Máquina 6', 1, 0, 0),
('OBO007', 'Máquina 7', 1, 0, 0),
('OBO008', 'Máquina 8', 1, 0, 0);

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
(4, 'OBO004-002', 2, false),
(5, 'OBO005-001', 1, true),
(5, 'OBO005-002', 2, false),
(6, 'OBO006-001', 1, true),
(6, 'OBO006-002', 2, false),
(7, 'OBO007-001', 1, true),
(7, 'OBO007-002', 2, false),
(8, 'OBO008-001', 1, true),
(8, 'OBO008-002', 2, false);

INSERT INTO alarm (word, index, code, description)
VALUES
(1, 1, 'PLC01', 'High Temperature Alarm'),
(1, 2, 'PLC02', 'Low Speed Alarm'),
(1, 3, 'PLC03', 'Pressure Drop Alarm'),
(1, 4, 'PLC04', 'Voltage Fluctuation Alarm'),
(1, 5, 'PLC05', 'Overload Alarm'),
(1, 6, 'PLC06', 'Emergency Stop Alarm'),
(1, 7, 'PLC07', 'Critical Pressure Alarm'),
(1, 8, 'PLC08', 'Voltage Spike Alarm'),
(1, 9, 'PLC09', 'Temperature Anomaly Alarm'),
(1, 10, 'PLC10', 'Speed Deviation Alarm'),
(1, 11, 'PLC11', 'Low Pressure Alarm'),
(1, 12, 'PLC12', 'Voltage Drop Alarm'),
(1, 13, 'PLC13', 'Temperature Warning'),
(1, 14, 'PLC14', 'Speed Limit Exceeded Alarm'),
(1, 15, 'PLC15', 'Pressure Surge Alarm'),
(1, 16, 'PLC16', 'Voltage Anomaly Alarm'),
(2, 1, 'PLC17', 'Critical Temperature Alarm'),
(2, 2, 'PLC18', 'High Speed Alarm'),
(2, 3, 'PLC19', 'Pressure Buildup Alarm'),
(2, 4, 'PLC20', 'Voltage Imbalance Alarm'),
(2, 5, 'PLC21', 'Temperature Fluctuation Alarm'),
(2, 6, 'PLC22', 'Speed Deviation Warning'),
(2, 7, 'PLC23', 'Pressure Anomaly Alarm'),
(2, 8, 'PLC24', 'Voltage Variation Alarm'),
(2, 9, 'PLC25', 'Overheat Warning'),
(2, 10, 'PLC26', 'Excessive Speed Warning'),
(2, 11, 'PLC27', 'Low Pressure Warning'),
(2, 12, 'PLC28', 'Voltage Instability Alarm'),
(2, 13, 'PLC29', 'Temperature Deviation Alarm'),
(2, 14, 'PLC30', 'Speed Limit Warning'),
(2, 15, 'PLC31', 'Pressure Spike Alarm'),
(2, 16, 'PLC32', 'Voltage Fluctuation Warning'),
(3, 1, 'PLC33', 'Critical Overheat Alarm'),
(3, 2, 'PLC34', 'Maximum Speed Alarm'),
(3, 3, 'PLC35', 'Pressure Drop Warning'),
(3, 4, 'PLC36', 'Voltage Surge Alarm'),
(3, 5, 'PLC37', 'Temperature Emergency'),
(3, 6, 'PLC38', 'Speed Exceedance Alarm'),
(3, 7, 'PLC39', 'Pressure Crisis Alarm'),
(3, 8, 'PLC40', 'Voltage Irregularity Alarm'),
(3, 9, 'PLC41', 'Extreme Temperature Alert'),
(3, 10, 'PLC42', 'Speed Irregularity Alarm'),
(3, 11, 'PLC43', 'Low Pressure Crisis Alarm'),
(3, 12, 'PLC44', 'Voltage Crisis Alert'),
(3, 13, 'PLC45', 'Temperature Critical Warning'),
(3, 14, 'PLC46', 'Maximum Speed Exceeded Alarm'),
(3, 15, 'PLC47', 'Pressure Critical Alarm'),
(3, 16, 'PLC48', 'Voltage Anomaly Alert'),
(4, 1, 'PLC49', 'Temperature Fluctuation Warning'),
(4, 2, 'PLC50', 'Speed Emergency Stop Alarm'),
(4, 3, 'PLC51', 'Pressure Buildup Warning'),
(4, 4, 'PLC52', 'Voltage Surge Alert'),
(4, 5, 'PLC53', 'Overheat Emergency'),
(4, 6, 'PLC54', 'Maximum Speed Warning'),
(4, 7, 'PLC55', 'Pressure Spike Warning'),
(4, 8, 'PLC56', 'Voltage Variation Warning'),
(4, 9, 'PLC57', 'Critical Temperature Alert'),
(4, 10, 'PLC58', 'Speed Fluctuation Alarm'),
(4, 11, 'PLC59', 'Low Pressure Crisis Warning'),
(4, 12, 'PLC60', 'Voltage Crisis Alert'),
(4, 13, 'PLC61', 'Temperature Variation Warning'),
(4, 14, 'PLC62', 'Speed Limit Alert'),
(4, 15, 'PLC63', 'Pressure Anomaly Warning'),
(4, 16, 'PLC64', 'Voltage Emergency Stop Alarm');