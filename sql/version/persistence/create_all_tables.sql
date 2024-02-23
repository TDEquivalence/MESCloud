-- Drop views
DROP VIEW IF EXISTS production_order_summary;
DROP VIEW IF EXISTS composed_summary;
DROP VIEW IF EXISTS counter_record_production_conclusion;
DROP VIEW IF EXISTS alarm_summary;

-- Drop tables
DROP TABLE IF EXISTS batch;
DROP TABLE IF EXISTS hit;
DROP TABLE IF EXISTS sample;
DROP TABLE IF EXISTS production_instruction;
DROP TABLE IF EXISTS counter_record;
DROP TABLE IF EXISTS alarm;
DROP TABLE IF EXISTS alarm_configuration;
DROP TABLE IF EXISTS production_order;
DROP TABLE IF EXISTS equipment_status_record;
DROP TABLE IF EXISTS equipment_output;
DROP TABLE IF EXISTS counting_equipment;
DROP TABLE IF EXISTS equipment_output_alias;
DROP TABLE IF EXISTS composed_production_order;
DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS token;
DROP TABLE IF EXISTS factory_user;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS factory;
DROP TABLE IF EXISTS ims;
DROP TABLE IF EXISTS audit_script;

-- Create tables
CREATE TABLE audit_script (
    id SERIAL PRIMARY KEY,
    run_date DATE,
    process VARCHAR(50),
    version VARCHAR(10),
    schema VARCHAR(50)
);

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
    code varchar(100) NOT NULL UNIQUE,

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
    theoretical_production DOUBLE PRECISION,
    quality_target DOUBLE PRECISION,
    availability_target DOUBLE PRECISION,
    performance_target DOUBLE PRECISION,
    overall_equipment_effectiveness_target DOUBLE PRECISION,
    operation_status VARCHAR(20),

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
    code VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    approved_at TIMESTAMP,
    hit_inserted_at TIMESTAMP,

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
    created_at TIMESTAMP,
    completed_at TIMESTAMP,
    input_batch varchar(100),
    source varchar(100),
    gauge varchar(100),
    category varchar(100),
    washing_process varchar(100),
    is_approved boolean,

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
    created_at TIMESTAMP,
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
    increment int,
    production_order_id int,
    registered_at TIMESTAMP,
    is_valid_for_production boolean,
    active_time int,
    computed_active_time int,
    increment_active_time int,

    PRIMARY KEY(id),
    FOREIGN KEY(equipment_output_id) REFERENCES equipment_output(id),
    FOREIGN KEY(production_order_id) REFERENCES production_order(id)
);

CREATE INDEX idx_counter_record_equipment_output_id ON counter_record (equipment_output_id);
CREATE INDEX idx_counter_record_production_order_id ON counter_record (production_order_id);
CREATE INDEX idx_counter_record_registered_at ON counter_record (registered_at);

CREATE TABLE equipment_status_record (
    id int GENERATED ALWAYS AS IDENTITY,
    counting_equipment_id int NOT NULL,
    equipment_status int NOT NULL,
    registered_at TIMESTAMP NOT NULL,

    PRIMARY KEY(id),
    FOREIGN KEY(counting_equipment_id) REFERENCES counting_equipment(id)
);

CREATE TABLE sample (
  id int GENERATED ALWAYS AS IDENTITY,
  composed_production_order_id INT,
  amount INT,
  tca_average DOUBLE PRECISION,
  reliability DOUBLE PRECISION,
  created_at TIMESTAMP,
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

CREATE TABLE alarm_configuration (
    id serial PRIMARY KEY,
    word_index int NOT NULL,
    bit_index int NOT NULL,
    code varchar(20) NOT NULL UNIQUE,
    description varchar(100),

    CONSTRAINT word_bit_indexes_unique UNIQUE (word_index, bit_index)
);

CREATE TABLE alarm (
    id serial PRIMARY KEY,
    alarm_configuration_id int NOT NULL,
    equipment_id int NOT NULL,
    production_order_id int,
    status varchar(10) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'RECOGNIZED')),
    comment text,
    created_at timestamp NOT NULL,
    completed_at timestamp,
    recognized_at timestamp,
    recognized_by int,

    FOREIGN KEY (equipment_id) REFERENCES counting_equipment (id),
    FOREIGN KEY (alarm_configuration_id) REFERENCES alarm_configuration (id),
    FOREIGN KEY (production_order_id) REFERENCES production_order (id),
    FOREIGN KEY (recognized_by) REFERENCES Users (id)
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
SELECT po.*, COALESCE(SUM(CAST(crpc.computed_value AS bigint)), 0) AS valid_amount
FROM production_order po
LEFT JOIN counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
WHERE po.is_completed = true AND crpc.is_valid_for_production = true
GROUP BY po.id;

CREATE OR REPLACE VIEW composed_summary AS
SELECT DISTINCT ON (cpo.id)
    cpo.id, cpo.created_at, cpo.code, cpo.approved_at, cpo.hit_inserted_at,
    s.amount, s.reliability,
    po.input_batch, po.source, po.gauge, po.category, po.washing_process,
    b.id AS batch_id,
    b.code AS batch_code,
    s.amount AS sample_amount,
    b.is_approved AS is_batch_approved,
    subquery.amount_of_hits,
    COALESCE(SUM(CAST(crpc.computed_value AS bigint)), 0) AS valid_amount
FROM composed_production_order cpo
LEFT JOIN sample s ON cpo.id = s.composed_production_order_id
LEFT JOIN production_order po ON cpo.id = po.composed_production_order_id
LEFT JOIN batch b ON cpo.id = b.composed_production_order_id
LEFT JOIN counter_record_production_conclusion crpc ON po.id = crpc.production_order_id
LEFT JOIN (
    SELECT s.composed_production_order_id, COUNT(h.id) AS amount_of_hits
    FROM sample s
    JOIN hit h ON s.id = h.sample_id
    GROUP BY s.composed_production_order_id
) AS subquery ON cpo.id = subquery.composed_production_order_id
WHERE crpc.is_valid_for_production = true
GROUP BY
    cpo.id, cpo.created_at, cpo.code,
    s.amount, s.reliability,
    po.input_batch, po.source, po.gauge, po.category, po.washing_process,
    b.id, b.code, b.is_approved,
    subquery.amount_of_hits;

CREATE VIEW alarm_summary AS
SELECT
    a.id AS id,
    ac.code AS configuration_code,
    ac.description AS configuration_description,
    ce.alias AS equipment_alias,
    po.code AS production_order_code,
    a.status AS status,
    a.comment AS comment,
    a.created_at AS created_at,
    a.completed_at AS completed_at,
    a.recognized_at AS recognized_at,
    u.username AS recognized_by_username  -- Include username here
FROM
    alarm a
JOIN
    alarm_configuration ac ON a.alarm_configuration_id = ac.id
JOIN
    counting_equipment ce ON a.equipment_id = ce.id
LEFT JOIN
    production_order po ON a.production_order_id = po.id
LEFT JOIN
    users u ON a.recognized_by = u.id  -- Join to users table to get the username
WHERE
    a.status IN ('ACTIVE', 'INACTIVE', 'RECOGNIZED');