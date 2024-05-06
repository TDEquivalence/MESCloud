BEGIN;

-- Step 1: Create new table production_order_instruction
CREATE TABLE production_order_instruction (
    instruction_name TEXT,
    instruction_value TEXT,
    production_order_id BIGINT,
    FOREIGN KEY (production_order_id) REFERENCES production_order(id),
    PRIMARY KEY (instruction_name, production_order_id)
);

-- Step 2: Copy data from production_order to production_order_instruction
INSERT INTO production_order_instruction (instruction_name, instruction_value, production_order_id)
SELECT 'input_batch', input_batch, id FROM production_order;

INSERT INTO production_order_instruction (instruction_name, instruction_value, production_order_id)
SELECT 'source', source, id FROM production_order;

INSERT INTO production_order_instruction (instruction_name, instruction_value, production_order_id)
SELECT 'gauge', gauge, id FROM production_order;

INSERT INTO production_order_instruction (instruction_name, instruction_value, production_order_id)
SELECT 'category', category, id FROM production_order;

INSERT INTO production_order_instruction (instruction_name, instruction_value, production_order_id)
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