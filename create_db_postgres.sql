CREATE TYPE oracle_type AS ENUM('fair', 'nice', 'evil');

CREATE FUNCTION update_stamp() RETURNS trigger AS $stamp$
    BEGIN
        NEW.last_updated := current_timestamp;
        RETURN NEW;
    END;
$stamp$ LANGUAGE plpgsql;

CREATE TABLE oracle (
    id SERIAL PRIMARY KEY,
    contest_entry_id integer, -- can be null
    solved boolean NOT NULL DEFAULT FALSE,
    type oracle_type NOT NULL,
    base integer NOT NULL check(1 < base),
    length integer NOT NULL check(0 < length),
    deserializer text NOT NULL,
    state bytea,
    first_guess timestamp with time zone,
    attempts integer default 0,
    solving_guess timestamp with time zone,
    created timestamp with time zone NOT NULL DEFAULT now(),
    last_updated timestamp with time zone NOT NULL
);

CREATE TRIGGER oracle_stamp BEFORE INSERT OR UPDATE ON oracle
    FOR EACH ROW EXECUTE PROCEDURE update_stamp();

GRANT INSERT(contest_entry_id, solved, type, base, length, deserializer, state)
  ON oracle TO number_oracle_db_user;

GRANT UPDATE(first_guess, solved, state, solving_guess, attempts)
  ON oracle TO number_oracle_db_user;

CREATE INDEX old_stale_oracles_index ON oracle(solved, last_updated);
CREATE INDEX contest_entries ON oracle(contest_entry_id);

CREATE TABLE contest (
    id SERIAL PRIMARY KEY,
    title text NOT NULL,
    description text,
    created timestamp with time zone NOT NULL DEFAULT now(),
    last_updated timestamp with time zone NOT NULL
);

CREATE TRIGGER contest_stamp BEFORE INSERT OR UPDATE ON contest
    FOR EACH ROW EXECUTE PROCEDURE update_stamp();

GRANT INSERT(title, description) ON contest TO number_oracle_db_user;

CREATE TABLE contest_games (
    id SERIAL PRIMARY KEY,
    contest_id integer REFERENCES contest(id) ON DELETE CASCADE,
    base integer NOT NULL,
    length integer NOT NULL,
    type oracle_type NOT NULL,
    oraclefactory_uri text NOT NULL,
    created timestamp with time zone NOT NULL DEFAULT now(),
    last_updated timestamp with time zone NOT NULL
);

CREATE TRIGGER contest_games_stamp BEFORE INSERT OR UPDATE ON contest_games
    FOR EACH ROW EXECUTE PROCEDURE update_stamp();

GRANT INSERT(contest_id, base, length, type, oraclefactory_uri) 
    ON contest_games TO number_oracle_db_user;

CREATE INDEX contest_games_foreign_key ON contest_games(contest_id);

CREATE TABLE contest_entry (
    id SERIAL PRIMARY KEY,
    contest_id integer REFERENCES contest(id) ON DELETE CASCADE,
    team_name text NOT NULL,
    software_version text NOT NULL,
    training boolean DEFAULT FALSE,
    created timestamp with time zone NOT NULL DEFAULT now(),
    last_updated timestamp with time zone NOT NULL
);

CREATE TRIGGER contest_entry_stamp BEFORE INSERT OR UPDATE ON contest_entry
    FOR EACH ROW EXECUTE PROCEDURE update_stamp();

CREATE INDEX contest_entry_foreign_key ON contest_entry(contest_id, team_name);

GRANT INSERT(contest_id, team_name, software_version)
    ON contest_entry TO number_oracle_db_user;

GRANT UPDATE(training, software_version)
    ON contest_entry TO number_oracle_db_user;

GRANT SELECT ON ALL TABLES IN SCHEMA public TO public;

GRANT USAGE ON ALL SEQUENCES IN SCHEMA public TO number_oracle_db_user;

