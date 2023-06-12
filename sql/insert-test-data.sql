INSERT INTO factory (
	name
) VALUES (
	'Test Factory'
);

INSERT INTO section (
	factory_id,
	name
) VALUES (
	1,
	'OBO'
);

INSERT INTO counting_equipment (
	code,
	alias,
	section_id,
	equipment_status,
	p_timer_communication_cycle
) VALUES (
	'OBO001',
	'Máquina 1',
	1,
	2,
	60
);

INSERT INTO equipment_output_alias (
	alias
) VALUES (
	'OK'
);

INSERT INTO equipment_output_alias (
	alias
) VALUES (
	'NOTOK'
);

INSERT INTO equipment_output_alias (
	alias
) VALUES (
	'OK2'
);

INSERT INTO equipment_output_alias (
	alias
) VALUES (
	'NOTOK2'
);

INSERT INTO equipment_output (
	counting_equipment_id,
	code,
	equipment_output_alias_id
) VALUES (
	1,
	'OBO001-001',
	1
);

INSERT INTO equipment_output (
	counting_equipment_id,
	code,
	equipment_output_alias_id
) VALUES (
	1,
	'OBO001-002',
	2
);

INSERT INTO equipment_output (
	counting_equipment_id,
	code,
	equipment_output_alias_id
) VALUES (
	1,
	'OBO001-003',
	3
);

INSERT INTO equipment_output (
	counting_equipment_id,
	code,
	equipment_output_alias_id
) VALUES (
	1,
	'OBO001-004',
	4
);

INSERT INTO production_order (
	equipment_id,
	code,
	target_amount,
	is_equipment_enabled,
	is_completed,
	created_at,
	input_batch,
	source,
	gauge,
	category,
	washing_process
) VALUES (
	1,
	'PO2301',
	1000,
	true,
	false,
	'2023-05-09',
	'POIN29Z12',
	'2123185BP',
	'45 x 24',
	'Flor/Ext/Sup',
	'MSN140X'
);