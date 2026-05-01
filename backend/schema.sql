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
-- Migration dossier_id (exécuter dans Neon SQL Editor si la table existe déjà)
-- ALTER TABLE conversations ADD COLUMN IF NOT EXISTS dossier_id UUID REFERENCES dossiers(id);

-- Indexes
CREATE INDEX idx_dossiers_user       ON dossiers(user_id);
CREATE INDEX idx_conversations_user  ON conversations(user_id);
CREATE INDEX idx_conversations_admin ON conversations(admin_id);
CREATE INDEX idx_messages_conv       ON messages(conversation_id);
CREATE INDEX idx_messages_timestamp  ON messages(timestamp);

-- ==========================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ==========================================

-- Activer RLS sur toutes les tables
ALTER TABLE dossiers ENABLE ROW LEVEL SECURITY;
ALTER TABLE conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;

-- Rôles PostgreSQL (mappés depuis JWT)
-- Ces rôles doivent être créés dans Neon et mappés via JWT claims
-- DO $$
-- BEGIN
--   IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'admin_role') THEN
--     CREATE ROLE admin_role;
--   END IF;
--   IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'user_role') THEN
--     CREATE ROLE user_role;
--   END IF;
--   IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'authenticated_role') THEN
--     CREATE ROLE authenticated_role;
--   END IF;
-- END $$;

-- GRANT roles aux utilisateurs
-- GRANT admin_role, user_role, authenticated_role TO postgres;

-- ==========================================
-- POLICIES: Dossiers
-- ==========================================

-- Les admins peuvent lire tous les dossiers
CREATE POLICY admins_read_all_dossiers ON dossiers
  FOR SELECT TO admin_role
  USING (true);

-- Les clients ne lisent que leurs dossiers
CREATE POLICY users_read_own_dossiers ON dossiers
  FOR SELECT TO user_role
  USING (user_id = current_setting('jwt.user_id')::UUID);

-- Les clients peuvent créer des dossiers
CREATE POLICY users_create_dossiers ON dossiers
  FOR INSERT TO user_role
  WITH CHECK (user_id = current_setting('jwt.user_id')::UUID);

-- Les admins peuvent modifier tous les dossiers
CREATE POLICY admins_update_dossiers ON dossiers
  FOR UPDATE TO admin_role
  USING (true);

-- Les clients peuvent modifier seulement si status != 'Soumis'
CREATE POLICY users_update_own_dossiers ON dossiers
  FOR UPDATE TO user_role
  USING (
    user_id = current_setting('jwt.user_id')::UUID
    AND status != 'Soumis'
  );

-- ==========================================
-- POLICIES: Conversations
-- ==========================================

-- Admins lisent toutes leurs conversations assignées
CREATE POLICY admins_read_assigned_conversations ON conversations
  FOR SELECT TO admin_role
  USING (admin_id = current_setting('jwt.user_id')::UUID);

-- Clients lisent leurs conversations
CREATE POLICY users_read_own_conversations ON conversations
  FOR SELECT TO user_role
  USING (user_id = current_setting('jwt.user_id')::UUID);

-- Les clients peuvent créer des conversations
CREATE POLICY users_create_conversations ON conversations
  FOR INSERT TO user_role
  WITH CHECK (user_id = current_setting('jwt.user_id')::UUID);

-- ==========================================
-- POLICIES: Messages
-- ==========================================

-- Les participants d'une conversation peuvent lire ses messages
CREATE POLICY participants_read_messages ON messages
  FOR SELECT TO authenticated_role
  USING (
    EXISTS (
      SELECT 1 FROM conversations c
      WHERE c.id = conversation_id
      AND (c.user_id = current_setting('jwt.user_id')::UUID OR c.admin_id = current_setting('jwt.user_id')::UUID)
    )
  );

-- Les participants peuvent envoyer des messages
CREATE POLICY participants_send_messages ON messages
  FOR INSERT TO authenticated_role
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM conversations c
      WHERE c.id = conversation_id
      AND (c.user_id = current_setting('jwt.user_id')::UUID OR c.admin_id = current_setting('jwt.user_id')::UUID)
    )
  );

-- Utilisateur de test (mot de passe: test1234)
INSERT INTO users (email, password_hash, name)
VALUES (
  'demo@misterdil.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Demo Conseiller'
);
