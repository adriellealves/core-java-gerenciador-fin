-- Alinha schema existente com as entidades JPA atuais (modo validate).
-- Seguro para rodar mais de uma vez (usa IF NOT EXISTS).

-- users
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- accounts
ALTER TABLE accounts
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- categories
ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- transactions
ALTER TABLE transactions
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ;

-- Preenche updated_at para registros legados que vierem nulos.
UPDATE users SET updated_at = NOW() WHERE updated_at IS NULL;
UPDATE accounts SET updated_at = NOW() WHERE updated_at IS NULL;
UPDATE categories SET updated_at = NOW() WHERE updated_at IS NULL;
UPDATE transactions SET updated_at = NOW() WHERE updated_at IS NULL;
