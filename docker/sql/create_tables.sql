-- Users
CREATE TABLE IF NOT EXISTS users (
    id serial NOT NULL PRIMARY KEY,
    email varchar NOT NULL,
    password varchar NOT NULL,
    role varchar NOT NULL,
    budget real NOT NULL
);

-- Teams
CREATE TABLE IF NOT EXISTS teams (
    id serial NOT NULL PRIMARY KEY,
    name varchar NOT NULL,
    points int NOT NULL,
    available_transfers int NOT NULL,
    user_id serial REFERENCES users (id)
);

-- Players
CREATE TABLE IF NOT EXISTS players (
    id serial NOT NULL PRIMARY KEY,
    name varchar NOT NULL,
    surname varchar NOT NULL,
    club char(3) NOT NULL,
    price real NOT NULL,
    pos char(3) NOT NULL,
    role varchar NOT NULL,
    health_status varchar NOT NULL,
    starter boolean NOT NULL
);

-- Link between teams and players
CREATE TABLE IF NOT EXISTS teams_players (
    team_id serial NOT NULL REFERENCES teams (id),
    player_id serial NOT NULL REFERENCES players (id)
);

ALTER TABLE teams_players
    ADD CONSTRAINT teams_players_connection PRIMARY KEY (team_id, player_id);

-- Statistics
CREATE TABLE IF NOT EXISTS statistics(
    game_week int NOT NULL,
    goals int NOT NULL,
    assists int NOT NULL,
    minutes int NOT NULL,
    own_goals int NOT NULL,
    yellow_cards int NOT NULL,
    red_cards int NOT NULL,
    saves int NOT NULL,
    clean_sheet boolean NOT NULL,
    player_id serial NOT NULL REFERENCES players (id)
);