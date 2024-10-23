create table EndPointHit (
    id SERIAL PRIMARY KEY,
    app varchar(255) NOT NULL,
    url varchar(255) NOT NULL,
    ip varchar(20) NOT NULL,
    time timestamp with time zone not null
);