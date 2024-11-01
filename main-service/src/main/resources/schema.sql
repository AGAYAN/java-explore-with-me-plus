CREATE TABLE IF NOT EXISTS category
(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_NAME UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS location
(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    latitude FLOAT NOT NULL CHECK (latitude BETWEEN -90 AND 90),
    longitude FLOAT NOT NULL CHECK (longitude BETWEEN -180 AND 180),
    CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS event
(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE  NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    confirmed_requests INT DEFAULT 0,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    initiator_id BIGINT NOT NULL,
    participant_limit INT NOT NULL DEFAULT 0,
    title VARCHAR(120) NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    state VARCHAR(20) NOT NULL CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED')),
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE RESTRICT,
    CONSTRAINT fk_location FOREIGN KEY (location_id) REFERENCES location (id) ON DELETE RESTRICT
--    CONSTRAINT fk_initiator FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_event_category ON event (category_id);
--CREATE INDEX idx_event_initiator ON event (initiator_id);
CREATE INDEX  IF NOT EXISTS idx_event_state ON event (state);
CREATE INDEX  IF NOT EXISTS idx_event_date ON event (event_date);