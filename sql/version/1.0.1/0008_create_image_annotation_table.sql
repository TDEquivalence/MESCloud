BEGIN;

CREATE TABLE image_annotation (
    id SERIAL PRIMARY KEY,
    image TEXT NOT NULL,
    model_decision TEXT,
    mes_user_decision TEXT,
    user_decision TEXT,
    classification TEXT,
    user_approval BOOLEAN,
    rejection TEXT[],
    comments TEXT,
    log_decision TEXT,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TEXT,
    user_id INT REFERENCES users(id)
);

CREATE TABLE annotation (
    id SERIAL PRIMARY KEY,
    from_name TEXT,
    to_name TEXT,
    type TEXT,
    original_width INTEGER,
    original_height INTEGER,
    x DOUBLE PRECISION,
    y DOUBLE PRECISION,
    width DOUBLE PRECISION,
    height DOUBLE PRECISION,
    rotation DOUBLE PRECISION,
    rectangle_labels TEXT[],
    image_annotation_id INTEGER REFERENCES image_annotation(id)
);

INSERT INTO audit_script (run_date, process, version, schema)
VALUES
    (CURRENT_DATE, '0008_create_image_annotation_table.sql', '1.0.1', '1.0.1_0008');

COMMIT;