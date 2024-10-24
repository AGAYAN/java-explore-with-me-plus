create table EndPointHit (
    id SERIAL PRIMARY KEY,
    app varchar(255) NOT NULL,
    uri varchar(255) NOT NULL,
    ip varchar(39) NOT NULL,
    request_time timestamp with time zone not null
);