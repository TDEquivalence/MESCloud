--CAREFULLY

--CHECK DATABASE FIRST

BEGIN;

DELETE FROM batch;
DELETE FROM hit;
DELETE FROM sample;
DELETE FROM counter_record;
DELETE FROM alarm;
DELETE FROM alarm_configuration;
DELETE FROM production_order;
DELETE FROM equipment_status_record;
DELETE FROM equipment_output;
DELETE FROM counting_equipment_feature;
DELETE FROM counting_equipment;
DELETE FROM equipment_output_alias;
DELETE FROM composed_production_order;
DELETE FROM feature;
DELETE FROM section_config_feature;
DELETE FROM section_config;
DELETE FROM section;
DELETE FROM ims;
DELETE FROM audit_script;
DELETE FROM token;
DELETE FROM users;
DELETE FROM factory;
DELETE FROM company;

COMMIT;

--CAREFULLY

--CHECK DATABASE FIRST
