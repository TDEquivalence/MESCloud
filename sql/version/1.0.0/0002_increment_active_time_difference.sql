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
    WHERE equipment_output_id = 2
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 2;

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
    WHERE equipment_output_id = 3
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 3;

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
    WHERE equipment_output_id = 4
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 4;

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
    WHERE equipment_output_id = 5
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 5;

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
    WHERE equipment_output_id = 6
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 6;

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
    WHERE equipment_output_id = 7
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 7;

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
    WHERE equipment_output_id = 8
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 8;

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
    WHERE equipment_output_id = 9
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 9;

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
    WHERE equipment_output_id = 10
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 10;

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
    WHERE equipment_output_id = 11
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 11;

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
    WHERE equipment_output_id = 12
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 12;

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
    WHERE equipment_output_id = 13
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 13;

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
    WHERE equipment_output_id = 14
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 14;

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
    WHERE equipment_output_id = 15
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 15;

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
    WHERE equipment_output_id = 16
) AS subquery
WHERE counter_record.id = subquery.id AND counter_record.equipment_output_id = 16;

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0002_increment_active_time_difference', '1.0.0', '1.0.0_0002');