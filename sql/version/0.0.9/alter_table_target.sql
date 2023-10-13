CREATE TABLE alarm (
    id serial PRIMARY KEY,
    word int NOT NULL UNIQUE,
    index int NOT NULL UNIQUE,
    code varchar(20) NOT NULL UNIQUE,
    description varchar(100)
);

CREATE TABLE alarm_record (
    id serial PRIMARY KEY,
    alarm_id int,
    equipment_id int,
    production_order_id int,
    status int NOT NULL CHECK (status IN (1, 2, 3)),
    comment text,
    created_at timestamp NOT NULL,
    completed_at timestamp,
    completed_by int,

    FOREIGN KEY (equipment_id) REFERENCES counting_equipment (id),
    FOREIGN KEY (alarm_id) REFERENCES Alarm (id),
    FOREIGN KEY (production_order_id) REFERENCES production_order (id),
    FOREIGN KEY (completed_by) REFERENCES Users (id)
);
