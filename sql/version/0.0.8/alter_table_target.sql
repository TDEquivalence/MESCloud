ALTER TABLE composed_production_order
ADD CONSTRAINT unique_code UNIQUE (code);

ALTER TABLE composed_production_order
ALTER COLUMN code SET NOT NULL;