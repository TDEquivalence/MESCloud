-- Drop views
DROP VIEW IF EXISTS production_order_summary;
DROP VIEW IF EXISTS composed_summary;
DROP VIEW IF EXISTS counter_record_production_conclusion;
DROP VIEW IF EXISTS alarm_summary;

-- Drop tables
DROP TABLE IF EXISTS batch;
DROP TABLE IF EXISTS hit;
DROP TABLE IF EXISTS sample;
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
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS factory;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS section;
DROP TABLE IF EXISTS section_config;
DROP TABLE IF EXISTS feature;
DROP TABLE IF EXISTS section_config_feature;
DROP TABLE IF EXISTS ims;

-- Create tables
CREATE TABLE IF NOT EXISTS audit_script (
    id SERIAL PRIMARY KEY,
    run_date DATE,
    process VARCHAR(50),
    version VARCHAR(10),
    schema_name VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS company (
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE
);

CREATE TABLE IF NOT EXISTS factory (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE,
    company_id INT,
    FOREIGN KEY (company_id) REFERENCES company(id)
);

CREATE TABLE IF NOT EXISTS section (
    id SERIAL PRIMARY KEY,
    factory_id INT,
    prefix VARCHAR(20),
    name VARCHAR(100),
    FOREIGN KEY (factory_id) REFERENCES factory(id)
);

CREATE TABLE IF NOT EXISTS section_config (
    id SERIAL PRIMARY KEY,
    section_id INT REFERENCES section(id),
    label VARCHAR(20),
    "order" INTEGER
);

CREATE TABLE IF NOT EXISTS feature (
    id SERIAL PRIMARY KEY,
    name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS section_config_feature (
    section_config_id INT REFERENCES section_config(id),
    feature_id INT REFERENCES feature(id),
    PRIMARY KEY (section_config_id, feature_id)
);

CREATE TABLE IF NOT EXISTS ims (
    id SERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS counting_equipment (
    id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    alias VARCHAR(100),
    section_id INT,
    equipment_status INT,
    p_timer_communication_cycle INT,
    ims_id INT UNIQUE,
    theoretical_production DOUBLE PRECISION,
    quality_target DOUBLE PRECISION,
    availability_target DOUBLE PRECISION,
    performance_target DOUBLE PRECISION,
    overall_equipment_effectiveness_target DOUBLE PRECISION,
    operation_status VARCHAR(20),
    FOREIGN KEY (section_id) REFERENCES section(id),
    FOREIGN KEY (ims_id) REFERENCES ims(id)
);

CREATE TABLE IF NOT EXISTS counting_equipment_feature (
    counting_equipment_id INT REFERENCES counting_equipment(id),
    feature_id INT REFERENCES feature(id),
    PRIMARY KEY (counting_equipment_id, feature_id)
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
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
    company_id INTEGER,
    FOREIGN KEY (company_id) REFERENCES company(id)
);

CREATE TABLE IF NOT EXISTS token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    token_type VARCHAR(10) NOT NULL,
    expired BOOLEAN,
    revoked BOOLEAN,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS equipment_output_alias (
    id SERIAL PRIMARY KEY,
    alias VARCHAR(100) UNIQUE
);

CREATE TABLE IF NOT EXISTS equipment_output (
    id SERIAL PRIMARY KEY,
    counting_equipment_id INT,
    code VARCHAR(20) UNIQUE NOT NULL,
    equipment_output_alias_id INT,
    is_valid_for_production BOOLEAN,
    FOREIGN KEY (counting_equipment_id) REFERENCES counting_equipment(id),
    FOREIGN KEY (equipment_output_alias_id) REFERENCES equipment_output_alias(id)
);

CREATE TABLE IF NOT EXISTS composed_production_order (
    id SERIAL PRIMARY KEY,
    code VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    approved_at TIMESTAMP,
    hit_inserted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS production_order (
    id SERIAL PRIMARY KEY,
    equipment_id INT,
    ims_id INT,
    composed_production_order_id INT,
    code VARCHAR(20) UNIQUE NOT NULL,
    target_amount INT,
    is_equipment_enabled BOOLEAN,
    is_completed BOOLEAN,
    created_at TIMESTAMP,
    completed_at TIMESTAMP,
    input_batch VARCHAR(100),
    source VARCHAR(100),
    gauge VARCHAR(100),
    category VARCHAR(100),
    washing_process VARCHAR(100),
    is_approved BOOLEAN,
    FOREIGN KEY (equipment_id) REFERENCES counting_equipment(id),
    FOREIGN KEY (ims_id) REFERENCES ims(id),
    FOREIGN KEY (composed_production_order_id) REFERENCES composed_production_order(id)
);

CREATE TABLE IF NOT EXISTS production_instruction (
    id SERIAL PRIMARY KEY,
    instruction INT,
    production_order_id INT,
    created_at TIMESTAMP,
    created_by INT,
    FOREIGN KEY (production_order_id) REFERENCES production_order(id),
    FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS counter_record (
    id SERIAL PRIMARY KEY,
    equipment_output_id INT,
    equipment_output_alias VARCHAR(100),
    real_value INT,
    computed_value INT,
    increment INT,
    production_order_id INT,
    registered_at TIMESTAMP,
    is_valid_for_production BOOLEAN,
    active_time INT,
    computed_active_time INT,
    increment_active_time INT,
    FOREIGN KEY (equipment_output_id) REFERENCES equipment_output(id),
    FOREIGN KEY (production_order_id) REFERENCES production_order(id)
);

CREATE TABLE IF NOT EXISTS equipment_status_record (
    id SERIAL PRIMARY KEY,
    counting_equipment_id INT NOT NULL,
    equipment_status INT NOT NULL,
    registered_at TIMESTAMP NOT NULL,
    FOREIGN KEY (counting_equipment_id) REFERENCES counting_equipment(id)
);

CREATE TABLE IF NOT EXISTS sample (
    id SERIAL PRIMARY KEY,
    composed_production_order_id INT,
    amount INT,
    tca_average DOUBLE PRECISION,
    reliability DOUBLE PRECISION,
    created_at TIMESTAMP,
    FOREIGN KEY (composed_production_order_id) REFERENCES composed_production_order(id)
);

CREATE TABLE IF NOT EXISTS hit (
    id SERIAL PRIMARY KEY,
    sample_id INT,
    tca FLOAT,
    is_valid_for_reliability BOOLEAN,
    FOREIGN KEY (sample_id) REFERENCES sample(id)
);

CREATE TABLE IF NOT EXISTS batch (
    id SERIAL PRIMARY KEY,
    code VARCHAR,
    composed_production_order_id INT,
    is_approved BOOLEAN,
    FOREIGN KEY (composed_production_order_id) REFERENCES composed_production_order(id)
);

CREATE TABLE IF NOT EXISTS alarm_configuration (
    id SERIAL PRIMARY KEY,
    word_index INT NOT NULL,
    bit_index INT NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS alarm (
    id SERIAL PRIMARY KEY,
    alarm_configuration_id INT NOT NULL,
    equipment_id INT NOT NULL,
    production_order_id INT,
    status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'RECOGNIZED')),
    comment TEXT,
    created_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    recognized_at TIMESTAMP,
    recognized_by INT,
    FOREIGN KEY (alarm_configuration_id) REFERENCES alarm_configuration(id),
    FOREIGN KEY (equipment_id) REFERENCES counting_equipment(id),
    FOREIGN KEY (production_order_id) REFERENCES production_order(id),
    FOREIGN KEY (recognized_by) REFERENCES users(id)
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
    u.first_name AS recognized_by_first_name,
    u.last_name AS recognized_by_last_name
FROM
    alarm a
JOIN
    alarm_configuration ac ON a.alarm_configuration_id = ac.id
JOIN
    counting_equipment ce ON a.equipment_id = ce.id
LEFT JOIN
    production_order po ON a.production_order_id = po.id
LEFT JOIN
    users u ON a.recognized_by = u.id
WHERE
    a.status IN ('ACTIVE', 'INACTIVE', 'RECOGNIZED');
