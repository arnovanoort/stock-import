CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE stock_market(
    id          uuid            DEFAULT uuid_generate_v4(),
    name        VARCHAR NOT NULL,
    client      VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE stock(
    id              uuid            DEFAULT uuid_generate_v4(),
    ticker          VARCHAR NOT NULL,
    name            VARCHAR NOT NULL,
    stock_market_id uuid,
    PRIMARY KEY (id),
    FOREIGN KEY (stock_market_id) REFERENCES stock_market (id)
);

CREATE TABLE stock_price(
    id              uuid            DEFAULT uuid_generate_v4(),
    open        NUMERIC(6,2) NOT NULL,
    close       NUMERIC(6,2) NOT NULL,
    high        NUMERIC(6,2),
    low         NUMERIC(6,2),
    date        DATE NOT NULL,
    stock_id    uuid,
    PRIMARY KEY (id),
    FOREIGN KEY (stock_id) REFERENCES stock (id)
);
