ALTER TABLE counter_record
ADD COLUMN active_time INT DEFAULT 0;

ALTER TABLE counter_record
ADD COLUMN computed_active_time INT DEFAULT 0;

ALTER TABLE production_order
DROP COLUMN active_time;