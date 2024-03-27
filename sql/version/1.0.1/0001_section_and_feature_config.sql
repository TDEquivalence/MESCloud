--add column user company in all tables and these tables
--delete relational table user factory
DROP TABLE IF EXISTS factory_user;
DROP TABLE IF EXISTS production_instruction;

ALTER TABLE factory RENAME TO company;

ALTER TABLE users
ADD COLUMN company_id INT REFERENCES company(id);

ALTER TABLE section
RENAME COLUMN name TO prefix;

ALTER TABLE section
ADD COLUMN name VARCHAR(20);

CREATE TABLE IF NOT EXISTS factory (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE,
    company_id INT,
    FOREIGN KEY (company_id) REFERENCES company(id)
);

CREATE TABLE IF NOT EXISTS section_config (
    id SERIAL PRIMARY KEY,
    section_id INT REFERENCES section(id),
    label VARCHAR(50),
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

CREATE TABLE IF NOT EXISTS counting_equipment_feature (
    counting_equipment_id INT REFERENCES counting_equipment(id),
    feature_id INT REFERENCES feature(id),
    PRIMARY KEY (counting_equipment_id, feature_id)
);

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0001_section_and_feature_config', '1.0.1', '1.0.1_0001');
