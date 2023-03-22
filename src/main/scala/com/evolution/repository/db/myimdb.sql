CREATE DATABASE myimdb

-- Users
CREATE TABLE users (
    id serial NOT NULL,
    PRIMARY KEY (id),
    email varchar NOT NULL,
    password varchar NOT NULL,
    role varchar NOT NULL,
    budget real NOT NULL
);

-- Players
CREATE TABLE players (
    id serial NOT NULL,
    PRIMARY KEY (id),
    firstName varchar NOT NULL,
    lastName varchar NOT NULL,
    club char(3) NOT NULL,
    price real NOT NULL,
    pos char(3) NOT NULL,
    role varchar NOT NULL,
    healthStatus varchar NOT NULL,
    starter boolean NOT NULL
);

-- Link between users and players
ALTER TABLE users
    ADD FOREIGN KEY (team_id) REFERENCES teams (id);

 -- Statistics
CREATE TABLE statistics(
    game_week serial NOT NULL,
    PRIMARY KEY (id),
    goals int NOT NULL,
    assists int NOT NULL,
    minutes int NOT NULL,
    own_goals int NOT NULL,
    yellow_cards int NOT NULL,
    red_cards int NOT NULL
)

--Link between players and statistics
ALTER TABLE players
    ADD FOREIGN KEY (stat) REFERENCES statistics(game_week, goals, assists, minutes, own_goals, yellow_cards, red_cards)

-- Teams
CREATE TABLE teams (
    id serial NOT NULL,
    PRIMARY KEY (id),
    name varchar NOT NULL,
    points int NOT NULL,
    availableTransfers int NOT NULL
);

-- Link between teams and players
CREATE TABLE teams_players (
    team_id serial NOT NULL,
    player_id serial NOT NULL
);

ALTER TABLE teams_players
    ADD CONSTRAINT teams_players_connection PRIMARY KEY (team_id, player_id);
ALTER TABLE teams_players
    ADD FOREIGN KEY (team_id) REFERENCES teams (id);
ALTER TABLE teams_players
    ADD FOREIGN KEY (player_id) REFERENCES players (id);