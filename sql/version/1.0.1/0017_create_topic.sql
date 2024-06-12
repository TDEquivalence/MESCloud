BEGIN;

CREATE TABLE topic (
    id SERIAL PRIMARY KEY,
    company_id INTEGER NOT NULL,
    section_id INTEGER NOT NULL,
    protocol_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(id),
    FOREIGN KEY (section_id) REFERENCES section(id)
);

--INSERT DATA
INSERT INTO topic (company_id, section_id, protocol_name)
VALUES (1, 1, 'PROTOCOL_COUNT_V0');


--TOPIC VIEW
CREATE VIEW topic_summary AS
SELECT
    t.id AS id,
    c.prefix AS company_prefix,
    c.id AS company_id,
    s.prefix AS section_prefix,
    s.id AS section_id,
    t.protocol_name,
    CONCAT(REPLACE(c.prefix, '/', ''), '/', s.prefix, '/', t.protocol_name, '/PLC') AS plc_topic,
    CONCAT(REPLACE(c.prefix, '/', ''), '/', s.prefix, '/', t.protocol_name, '/BE') AS backend_topic
FROM
    topic t
JOIN
    company c ON t.company_id = c.id
JOIN
    section s ON t.section_id = s.id;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0017_create_topic.sql', '1.0.1', '1.0.1_0017');

COMMIT;