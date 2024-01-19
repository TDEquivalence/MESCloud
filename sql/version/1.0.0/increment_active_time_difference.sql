UPDATE counter_record
SET increment_active_time = CASE
    WHEN time_difference < 0 THEN 0
    ELSE time_difference
END
FROM (
    SELECT
        id,
        computed_active_time - LAG(computed_active_time) OVER (ORDER BY id ASC, equipment_output_id) AS time_difference
    FROM
        counter_record
    WHERE equipment_output_id = 1
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 1;