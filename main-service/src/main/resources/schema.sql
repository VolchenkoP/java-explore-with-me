CREATE TABLE IF NOT EXISTS users (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	email VARCHAR NOT NULL UNIQUE,
	name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
	id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	lat VARCHAR NOT NULL,
	lon VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title VARCHAR NOT NULL,
    pinned BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	annotation VARCHAR NOT NULL,
	category INTEGER REFERENCES categories (id) ON DELETE RESTRICT,
	created_on TIMESTAMP,
	description VARCHAR NOT NULL,
	event_date TIMESTAMP,
	initiator BIGINT REFERENCES users (id) ON DELETE CASCADE,
	location BIGINT REFERENCES locations (id) ON DELETE CASCADE,
	paid BOOLEAN NOT NULL,
	participants_limit BIGINT,
	published_on TIMESTAMP,
	request_moderation BOOLEAN NOT NULL,
	state VARCHAR,
	title VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS requests (
	id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	created TIMESTAMP NOT NULL,
	event BIGINT REFERENCES events (id),
	requester BIGINT REFERENCES users (id),
	status VARCHAR
);

CREATE TABLE IF NOT EXISTS events_by_compilations (
    compilation_id INTEGER REFERENCES compilations (id) ON DELETE CASCADE,
    event_id BIGINT REFERENCES events (id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

