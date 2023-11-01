DELETE FROM alarm_record where id > 0;
DELETE FROM alarm where id > 0;

DROP TABLE alarm_record;
DROP TABLE alarm;

CREATE TABLE alarm_configuration (
    id serial PRIMARY KEY,
    word_index int NOT NULL,
    bit_index int NOT NULL,
    code varchar(20) NOT NULL UNIQUE,
    description varchar(100),

    CONSTRAINT word_bit_indexes_unique UNIQUE (word_index, bit_index)
);

CREATE TABLE alarm (
    id serial PRIMARY KEY,
    alarm_configuration_id int NOT NULL,
    equipment_id int NOT NULL,
    production_order_id int,
    status varchar(10) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE', 'RECOGNIZED')),
    comment text,
    created_at timestamp NOT NULL,
    completed_at timestamp,
    completed_by int,

    FOREIGN KEY (equipment_id) REFERENCES counting_equipment (id),
    FOREIGN KEY (alarm_configuration_id) REFERENCES alarm_configuration (id),
    FOREIGN KEY (production_order_id) REFERENCES production_order (id),
    FOREIGN KEY (completed_by) REFERENCES Users (id)
);