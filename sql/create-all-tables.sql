DROP TABLE IF EXISTS factory_user;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS production_instruction;
DROP TABLE IF EXISTS production_order;
DROP TABLE IF EXISTS equipment_status_record;
DROP TABLE IF EXISTS counting_equipment;
DROP TABLE IF EXISTS counter_record;
DROP TABLE IF EXISTS equipment_output;
DROP TABLE IF EXISTS equipment_output_alias;
DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS factory;


CREATE TABLE users (
 id int GENERATED ALWAYS AS IDENTITY,
 first_name varchar(20),
 last_name varchar(20),
 email varchar(50),
 password varchar(255),

 PRIMARY KEY(id)
);

CREATE TABLE factory (
 id int GENERATED ALWAYS AS IDENTITY,
 name varchar(100) UNIQUE,

 PRIMARY KEY(id)
);

CREATE TABLE factory_user (
 factory_id int,
 user_id int,

 FOREIGN KEY(factory_id) REFERENCES factory(id),
 FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE section (
 id int GENERATED ALWAYS AS IDENTITY,
 factory_id int,
 name varchar(100),

 PRIMARY KEY(id),
 FOREIGN KEY(factory_id) REFERENCES factory(id)
);

CREATE TABLE counting_equipment (
	id int GENERATED ALWAYS AS IDENTITY,
	code varchar(20) UNIQUE NOT NULL,
	alias varchar(100),
	section_id int,
	equipment_status int,
	p_timer_communication_cycle int,

	PRIMARY KEY(id),
	FOREIGN KEY(section_id) REFERENCES section(id)
);

CREATE TABLE equipment_output_alias (
	id int GENERATED ALWAYS AS IDENTITY,
	alias varchar(100) UNIQUE,

	PRIMARY KEY(id)
);


CREATE TABLE equipment_output (
	id int GENERATED ALWAYS AS IDENTITY,
	counting_equipment_id int,
	code varchar(20) UNIQUE NOT NULL,
	equipment_output_alias_id int,

	PRIMARY KEY(id),
	FOREIGN KEY(equipment_output_alias_id) REFERENCES equipment_output_alias(id)
);

CREATE TABLE production_order (
	id int GENERATED ALWAYS AS IDENTITY,
	equipment_id int,
	code varchar(20) UNIQUE NOT NULL,
	target_amount int,
	is_equipment_enabled boolean,
	is_completed boolean,
	created_at date,

	PRIMARY KEY(id),
	FOREIGN KEY(equipment_id) REFERENCES counting_equipment(id)
);

CREATE TABLE production_instruction (
	id int GENERATED ALWAYS AS IDENTITY,
    instruction int,
    production_order_id int,
    created_at date,
    created_by int,

    PRIMARY KEY(id),
    FOREIGN KEY(production_order_id) REFERENCES production_order(id),
    FOREIGN KEY(created_by) REFERENCES users(id)
);

CREATE TABLE counter_record (
	id int GENERATED ALWAYS AS IDENTITY,
	equipment_output_id int,
	equipment_output_alias varchar(100),
	real_value int,
	computed_value int,
	production_order_id int,
	registered_at date,

	PRIMARY KEY(id),
	FOREIGN KEY(equipment_output_id) REFERENCES equipment_output(id)
);

CREATE TABLE equipment_status_record (
	id int GENERATED ALWAYS AS IDENTITY,
	counting_equipment_id int,
	equipment_status int,
	registered_at date,

	PRIMARY KEY(id),
	FOREIGN KEY(counting_equipment_id) REFERENCES counting_equipment(id)
);