CREATE TABLE IF NOT EXISTS transaction_category (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(512),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE,
    CONSTRAINT uq_category_user_name UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS account_transaction (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    category_id BIGINT,
    amount NUMERIC(19, 4) NOT NULL,
    type VARCHAR(20) NOT NULL,
    label VARCHAR(255) NOT NULL,
    notes VARCHAR(512),
    merchant VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES transaction_category (id) ON DELETE
    SET
        NULL
);