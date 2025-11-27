CREATE TABLE IF NOT EXISTS recurring_transaction (
    id SERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    category_id BIGINT,
    amount NUMERIC(19, 4) NOT NULL,
    type VARCHAR(20) NOT NULL,
    label VARCHAR(255),
    notes VARCHAR(512),
    merchant VARCHAR(255),
    frequency VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    execution_time TIME NOT NULL DEFAULT '00:00',
    day_of_month INTEGER,
    month_of_year INTEGER,
    day_of_week VARCHAR(20),
    next_execution_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_recurring_transaction_account FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE,
    CONSTRAINT fk_recurring_transaction_category FOREIGN KEY (category_id) REFERENCES transaction_category (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_recurring_transaction_due ON recurring_transaction (is_active, next_execution_date);
