ALTER TABLE counting_equipment
ADD COLUMN overallEquipmentEffectiveness DOUBLE PRECISION,
ADD COLUMN availability DOUBLE PRECISION,
ADD COLUMN performance DOUBLE PRECISION,
ADD COLUMN quality DOUBLE PRECISION;

ALTER TABLE production_order
ADD COLUMN completed_at DATE;
