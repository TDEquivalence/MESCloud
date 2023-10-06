ALTER TABLE counting_equipment
RENAME COLUMN performance TO performance_target;

ALTER TABLE counting_equipment
RENAME COLUMN quality TO quality_target;

ALTER TABLE counting_equipment
RENAME COLUMN availability TO availability_target;

ALTER TABLE counting_equipment
RENAME COLUMN equipment_effectiveness TO overall_equipment_effectiveness_target;