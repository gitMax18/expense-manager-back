CREATE TABLE IF NOT EXISTS account (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    balance NUMERIC(19, 4) NOT NULL DEFAULT 0,
    type VARCHAR(20) NOT NULL,
    currency CHAR(3) NOT NULL,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES app_user (id) ON DELETE CASCADE
);