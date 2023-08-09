DROP TABLE IF EXISTS factory_user CASCADE;
DROP TABLE IF EXISTS production_instruction CASCADE;
DROP TABLE IF EXISTS equipment_status_record CASCADE;
DROP TABLE IF EXISTS counter_record CASCADE;
DROP TABLE IF EXISTS production_order CASCADE;
DROP TABLE IF EXISTS counting_equipment CASCADE;
DROP TABLE IF EXISTS equipment_output CASCADE;
DROP TABLE IF EXISTS equipment_output_alias CASCADE;
DROP TABLE IF EXISTS section CASCADE;
DROP TABLE IF EXISTS factory CASCADE;
DROP TABLE IF EXISTS token CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP VIEW IF EXISTS counter_record_production_conclusion;

CREATE TABLE users (
  id int GENERATED ALWAYS AS IDENTITY,
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  username VARCHAR(50) NOT NULL,
  email VARCHAR(50),
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50),
  user_authorities TEXT[],
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  is_active BOOLEAN,
  is_not_locked BOOLEAN,

  PRIMARY KEY(id)
);

CREATE TABLE token (
  id SERIAL PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  token_type VARCHAR(10) NOT NULL,
  expired BOOLEAN,
  revoked BOOLEAN,
  user_id INTEGER NOT NULL,

  FOREIGN KEY (user_id) REFERENCES users (id)
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

CREATE INDEX idx_counting_equipment_section_id ON counting_equipment (section_id);

CREATE TABLE equipment_output_alias (
    id int GENERATED ALWAYS AS IDENTITY,
    alias varchar(100) UNIQUE,

    PRIMARY KEY(id)
);

CREATE INDEX idx_equipment_output_alias_alias ON equipment_output_alias (alias);

CREATE TABLE equipment_output (
    id int GENERATED ALWAYS AS IDENTITY,
    counting_equipment_id int,
    code varchar(20) UNIQUE NOT NULL,
    equipment_output_alias_id int,

    PRIMARY KEY(id),
    FOREIGN KEY(equipment_output_alias_id) REFERENCES equipment_output_alias(id)
);

CREATE INDEX idx_equipment_output_code ON equipment_output (code);

CREATE TABLE production_order (
    id int GENERATED ALWAYS AS IDENTITY,
    equipment_id int,
    code varchar(20) UNIQUE NOT NULL,
    target_amount int,
    is_equipment_enabled boolean,
    is_completed boolean,
    created_at date,
    input_batch varchar(100),
    source varchar(100),
    gauge varchar(100),
    category varchar(100),
    washing_process varchar(100),

    PRIMARY KEY(id),
    FOREIGN KEY(equipment_id) REFERENCES counting_equipment(id)
);

CREATE INDEX idx_production_order_code ON production_order (code);

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
    registered_at timestamp,

    PRIMARY KEY(id),
    FOREIGN KEY(equipment_output_id) REFERENCES equipment_output(id),
    FOREIGN KEY(production_order_id) REFERENCES production_order(id)
);

CREATE INDEX idx_counter_record_equipment_output_id ON counter_record (equipment_output_id);
CREATE INDEX idx_counter_record_production_order_id ON counter_record (production_order_id);
CREATE INDEX idx_counter_record_registered_at ON counter_record (registered_at);

CREATE TABLE equipment_status_record (
    id int GENERATED ALWAYS AS IDENTITY,
    counting_equipment_id int,
    equipment_status int,
    registered_at date,

    PRIMARY KEY(id),
    FOREIGN KEY(counting_equipment_id) REFERENCES counting_equipment(id)
);

CREATE OR REPLACE VIEW counter_record_production_conclusion AS
SELECT cr.id, cr.equipment_output_id, cr.equipment_output_alias, cr.real_value, cr.computed_value, cr.production_order_id, cr.registered_at, po.code AS production_order_code
FROM (
    SELECT eo.id AS equipment_output_id, cr.equipment_output_alias, po.id AS production_order_id, MAX(cr.id) AS max_counter_record_id
    FROM equipment_output eo
    INNER JOIN production_order po ON eo.counting_equipment_id = po.equipment_id
    INNER JOIN counter_record cr ON eo.id = cr.equipment_output_id AND po.id = cr.production_order_id
    GROUP BY eo.id, cr.equipment_output_alias, po.id
) AS last_counter
JOIN counter_record cr ON last_counter.max_counter_record_id = cr.id
JOIN production_order po ON cr.production_order_id = po.id;