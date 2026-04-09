CREATE TABLE IF NOT EXISTS ticket_record (
    ticket_id         VARCHAR(64) PRIMARY KEY,
    entry_time        TIMESTAMPTZ NOT NULL,
    parking_spot_id   VARCHAR(64) NOT NULL,
    vehicle_type      VARCHAR(32) NOT NULL,
    exit_time         TIMESTAMPTZ
);
