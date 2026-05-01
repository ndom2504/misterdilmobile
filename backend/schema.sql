-- Schéma Neon PostgreSQL pour Misterdil
-- Exécuter dans le SQL Editor du dashboard Neon

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Utilisateurs
CREATE TABLE users (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email       VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  name        VARCHAR(255) NOT NULL,
  role        VARCHAR(20)  NOT NULL DEFAULT 'user', -- 'user' | 'admin'
  created_at  TIMESTAMP DEFAULT NOW()
);

-- Migration si la table existe déjà (exécuter manuellement dans Neon SQL Editor)
-- ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'user';

-- Dossiers d'immigration
CREATE TABLE dossiers (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  client_name VARCHAR(255) NOT NULL,
  type        VARCHAR(100) NOT NULL,
  status      VARCHAR(100) NOT NULL DEFAULT 'En cours',
  progress    FLOAT        NOT NULL DEFAULT 0,
  last_update VARCHAR(255),
  user_id     UUID REFERENCES users(id) ON DELETE CASCADE,
  created_at  TIMESTAMP DEFAULT NOW()
);

-- Conversations
CREATE TABLE conversations (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  client_name  VARCHAR(255) NOT NULL,
  project_name VARCHAR(255) NOT NULL,
  last_message TEXT         DEFAULT '',
  time         VARCHAR(50),
  unread_count INTEGER      DEFAULT 0,
  user_id      UUID REFERENCES users(id) ON DELETE CASCADE,
  created_at   TIMESTAMP DEFAULT NOW()
);

-- Messages
CREATE TABLE messages (
  id              VARCHAR(255) PRIMARY KEY,
  conversation_id UUID REFERENCES conversations(id) ON DELETE CASCADE,
  sender_id       VARCHAR(255) NOT NULL,
  text            TEXT         NOT NULL,
  timestamp       BIGINT       NOT NULL,
  is_from_me      BOOLEAN      DEFAULT false,
  created_at      TIMESTAMP DEFAULT NOW()
);

-- Migration admin_id (exécuter dans Neon SQL Editor si la table existe déjà)
-- ALTER TABLE conversations ADD COLUMN IF NOT EXISTS admin_id UUID REFERENCES users(id);

-- Indexes
CREATE INDEX idx_dossiers_user       ON dossiers(user_id);
CREATE INDEX idx_conversations_user  ON conversations(user_id);
CREATE INDEX idx_messages_conv       ON messages(conversation_id);
CREATE INDEX idx_messages_timestamp  ON messages(timestamp);

-- Utilisateur de test (mot de passe: test1234)
INSERT INTO users (email, password_hash, name)
VALUES (
  'demo@misterdil.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Demo Conseiller'
);
