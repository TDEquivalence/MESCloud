ALTER TABLE alarm
ADD COLUMN recognized_at timestamp;

ALTER TABLE alarm
RENAME COLUMN completed_by TO recognized_by;