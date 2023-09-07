-- Drop views
DROP VIEW IF EXISTS production_order_summary;
DROP VIEW IF EXISTS counter_record_production_conclusion;
DROP VIEW IF EXISTS composed_summary;

-- Drop tables
DROP TABLE IF EXISTS batch;
DROP TABLE IF EXISTS hit;
DROP TABLE IF EXISTS sample;
DROP TABLE IF EXISTS production_instruction;
DROP TABLE IF EXISTS counter_record;
DROP TABLE IF EXISTS production_order;
DROP TABLE IF EXISTS composed_production_order;
DROP TABLE IF EXISTS production_order_recipe;
DROP TABLE IF EXISTS equipment_output;
DROP TABLE IF EXISTS equipment_output_alias;
DROP TABLE IF EXISTS equipment_status_record;
DROP TABLE IF EXISTS counting_equipment;
DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS token;
DROP TABLE IF EXISTS factory_user;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS factory;
DROP TABLE IF EXISTS ims;

-- Create tables
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

CREATE TABLE ims (
    id int GENERATED ALWAYS AS IDENTITY,
    code varchar(100) NOT NULL,

    PRIMARY KEY(id)
);

CREATE TABLE counting_equipment (
    id int GENERATED ALWAYS AS IDENTITY,
    code varchar(20) UNIQUE NOT NULL,
    alias varchar(100),
    section_id int,
    equipment_status int,
    p_timer_communication_cycle int,
    ims_id int UNIQUE,

    PRIMARY KEY(id),
    FOREIGN KEY(section_id) REFERENCES section(id),
    FOREIGN KEY(ims_id) REFERENCES ims(id)
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
    is_valid_for_production boolean,

    PRIMARY KEY(id),
    FOREIGN KEY(equipment_output_alias_id) REFERENCES equipment_output_alias(id)
);

CREATE INDEX idx_equipment_output_code ON equipment_output (code);


CREATE TABLE composed_production_order (
    id int GENERATED ALWAYS AS IDENTITY,
    code VARCHAR(255),
    created_at date,

    PRIMARY KEY(id)
);

CREATE TABLE production_order (
    id int GENERATED ALWAYS AS IDENTITY,
    equipment_id int,
    ims_id int,
    composed_production_order_id int,
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
    FOREIGN KEY(equipment_id) REFERENCES counting_equipment(id),
    FOREIGN KEY(ims_id) REFERENCES ims(id),
    FOREIGN KEY(composed_production_order_id) REFERENCES composed_production_order(id)
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
    is_valid_for_production boolean,

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

CREATE TABLE sample (
  id int GENERATED ALWAYS AS IDENTITY,
  composed_production_order_id INT,
  amount INT,
  tca_average INT,
  reliability INT,
  created_at date,
  PRIMARY KEY (id),
  FOREIGN KEY (composed_production_order_id) REFERENCES composed_production_order (id)
);

CREATE TABLE hit (
  id int GENERATED ALWAYS AS IDENTITY,
  sample_id INT,
  tca FLOAT,
  is_valid_for_reliability BOOLEAN,
  PRIMARY KEY (id),
  FOREIGN KEY (sample_id) REFERENCES sample (id)
);

CREATE TABLE batch (
  id int GENERATED ALWAYS AS IDENTITY,
  code VARCHAR,
  composed_production_order_id INT,
  is_approved BOOLEAN,
  PRIMARY KEY (id),
  FOREIGN KEY (composed_production_order_id) REFERENCES composed_production_order (id)
);

-- Create views
CREATE OR REPLACE VIEW counter_record_production_conclusion AS
SELECT cr.id, cr.equipment_output_id, cr.equipment_output_alias, cr.real_value, cr.computed_value, cr.production_order_id, cr.registered_at, cr.is_valid_for_production, po.code AS production_order_code
FROM (
    SELECT eo.id AS equipment_output_id, cr.equipment_output_alias, po.id AS production_order_id, MAX(cr.id) AS max_counter_record_id
    FROM equipment_output eo
    INNER JOIN production_order po ON eo.counting_equipment_id = po.equipment_id
    INNER JOIN counter_record cr ON eo.id = cr.equipment_output_id AND po.id = cr.production_order_id
    GROUP BY eo.id, cr.equipment_output_alias, po.id
) AS last_counter
JOIN counter_record cr ON last_counter.max_counter_record_id = cr.id
JOIN production_order po ON cr.production_order_id = po.id;

CREATE OR REPLACE VIEW production_order_summary AS
SELECT po.*, COALESCE(SUM(CAST(crpc.real_value AS bigint)), 0) AS valid_amount
FROM production_order po
LEFT JOIN counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
WHERE po.is_completed = true AND po.composed_production_order_id IS NULL AND crpc.is_valid_for_production = true
GROUP BY po.id;

CREATE OR REPLACE VIEW composed_summary AS
SELECT DISTINCT
	cpo.id, cpo.created_at,
	s.amount, s.reliability,
	po.input_batch, po.source, po.gauge, po.category, po.washing_process,
	b.id AS batch_id, b.code AS batch_code, b.is_approved AS is_batch_approved
FROM composed_production_order cpo
LEFT JOIN sample s ON cpo.id = s.composed_production_order_id
LEFT JOIN hit h ON s.id = h.sample_id
LEFT JOIN production_order po ON cpo.id = po.composed_production_order_id
LEFT JOIN batch b ON cpo.id = b.composed_production_order_id;

