CREATE TABLE block (
       id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
       hash TEXT NOT NULL,
       height INT NOT NULL,
       paid BOOLEAN DEFAULT FALSE NOT NULL,
       created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE TABLE miner (
       id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
       wallet_address TEXT NOT NULL UNIQUE,
       created_at TIMESTAMP DEFAULT NOW() NOT NULL
);

CREATE TABLE share (
       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
       miner_id INT REFERENCES miner(id) ON DELETE CASCADE NOT NULL,
       difficulty INT NOT NULL,
       created_at TIMESTAMP DEFAULT NOW() NOT NULL
);
