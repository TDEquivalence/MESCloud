BEGIN;

-- Step 1: Create new table production_order_instruction
CREATE TABLE production_instruction (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    value VARCHAR(255),
    production_order_id BIGINT REFERENCES production_order(id)
);

-- Step 2: Copy data from production_order to production_order_instruction
INSERT INTO production_instruction (name, value, production_order_id)
SELECT 'input_batch', input_batch, id FROM production_order;

INSERT INTO production_instruction (name, value, production_order_id)
SELECT 'source', source, id FROM production_order;

INSERT INTO production_instruction (name, value, production_order_id)
SELECT 'gauge', gauge, id FROM production_order;

INSERT INTO production_instruction (name, value, production_order_id)
SELECT 'category', category, id FROM production_order;

INSERT INTO production_instruction (name, value, production_order_id)
SELECT 'washing_process', washing_process, id FROM production_order;

-- Step 3: Drop columns from production_order
ALTER TABLE production_order
DROP COLUMN inputBatch,
DROP COLUMN source,
DROP COLUMN gauge,
DROP COLUMN category,
DROP COLUMN washingProcess,
DROP COLUMN isApproved;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0009_create_production_instruction_table.sql', '1.0.1', '1.0.1_0009');

COMMIT;