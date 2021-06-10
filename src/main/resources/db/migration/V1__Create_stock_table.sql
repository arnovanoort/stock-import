
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE stock_market(
    id          uuid            DEFAULT uuid_generate_v4(),
    name        VARCHAR NOT NULL,
    client      VARCHAR NOT NULL,
    PRIMARY KEY (id),
    UNIQUE(name)
);

CREATE TABLE stock(
    id              uuid            DEFAULT uuid_generate_v4(),
    ticker          VARCHAR NOT NULL,
    name            VARCHAR NOT NULL,
    asset_type      VARCHAR NOT NULL,
    currency        VARCHAR NOT NULL,
    date_listed     DATE,
    date_unlisted   DATE,
    stock_market_id uuid,
    PRIMARY KEY (id),
    FOREIGN KEY (stock_market_id) REFERENCES stock_market (id),
    UNIQUE(name)
);

CREATE TABLE stock_price(
    id              uuid            DEFAULT uuid_generate_v4(),
    open        NUMERIC NOT NULL,
    close       NUMERIC NOT NULL,
    high        NUMERIC,
    low         NUMERIC,
    volume      NUMERIC,
    date        DATE NOT NULL,
    stock_id    uuid,
    PRIMARY KEY (id),
    FOREIGN KEY (stock_id) REFERENCES stock (id)
);
