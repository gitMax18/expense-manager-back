ALTER TABLE account_transaction
    ADD COLUMN IF NOT EXISTS is_recurring_transaction BOOLEAN NOT NULL DEFAULT FALSE;
