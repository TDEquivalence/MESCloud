--CAREFULLY

--CHECK DATABASE FIRST


BEGIN;

DROP VIEW IF EXISTS production_order_summary;
DROP VIEW IF EXISTS composed_summary;
DROP VIEW IF EXISTS counter_record_production_conclusion;
DROP VIEW IF EXISTS alarm_summary;

DROP TABLE IF EXISTS audit_script;
DROP TABLE IF EXISTS batch;
DROP TABLE IF EXISTS hit;
DROP TABLE IF EXISTS sample;
DROP TABLE IF EXISTS counter_record;
DROP TABLE IF EXISTS alarm;
DROP TABLE IF EXISTS alarm_configuration;
DROP TABLE IF EXISTS production_order;
DROP TABLE IF EXISTS equipment_status_record;
DROP TABLE IF EXISTS equipment_output;
DROP TABLE IF EXISTS counting_equipment_feature;
DROP TABLE IF EXISTS counting_equipment;
DROP TABLE IF EXISTS equipment_output_alias;
DROP TABLE IF EXISTS composed_production_order;
DROP TABLE IF EXISTS section_config_feature;
DROP TABLE IF EXISTS feature;
DROP TABLE IF EXISTS section_config;
DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS ims;
DROP TABLE IF EXISTS audit_script;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS factory;
DROP TABLE IF EXISTS company;

COMMIT;

--CAREFULLY

--CHECK DATABASE FIRST