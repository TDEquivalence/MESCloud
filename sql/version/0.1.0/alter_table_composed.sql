ALTER TABLE composed_production_order
ADD COLUMN approval_at timestamp;

ALTER TABLE composed_production_order
ADD COLUMN hit_inserted_at timestamp;