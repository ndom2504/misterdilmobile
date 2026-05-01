-- Schéma Neon PostgreSQL pour Misterdil
-- Exécuter dans le SQL Editor du dashboard Neon

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Utilisateurs
CREATE TABLE users (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email       VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  name        VARCHAR(255) NOT NULL,
  avatar_url  TEXT,
  role        VARCHAR(20)  NOT NULL DEFAULT 'user', -- 'user' | 'admin'
  created_at  TIMESTAMP DEFAULT NOW()
);

-- Migration si la table existe déjà (exécuter manuellement dans Neon SQL Editor)
-- ALTER TABLE users ADD COLUMN IF NOT EXISTS role VARCHAR(20) NOT NULL DEFAULT 'user';
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar_url TEXT;

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
-- AUDIT LOGS TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS audit_logs (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID NOT NULL,
  action      VARCHAR(50) NOT NULL,
  resource    VARCHAR(100) NOT NULL,
  resource_id UUID,
  details     JSONB,
  timestamp   TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource, resource_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);

-- Trigger function for audit logging
CREATE OR REPLACE FUNCTION audit_log_trigger()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO audit_logs (user_id, action, resource, resource_id, details)
  VALUES (
    current_setting('request.jwt.user_id', true)::UUID,
    TG_OP,
    TG_TABLE_NAME,
    COALESCE(NEW.id, OLD.id),
    jsonb_build_object(
      'old_data', to_jsonb(OLD),
      'new_data', to_jsonb(NEW)
    )
  );
  RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

-- ==========================================
-- ROW LEVEL SECURITY (RLS) POLICIES
-- ==========================================

-- Activer RLS sur toutes les tables
ALTER TABLE dossiers ENABLE ROW LEVEL SECURITY;
ALTER TABLE conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;

-- ==========================================
-- RLS POLICIES BASED ON JWT CLAIMS
-- ==========================================
--
-- Les policies utilisent current_setting('request.jwt.claims') pour extraire
-- le rôle et l'ID utilisateur depuis le JWT. Le middleware doit configurer
-- ces settings avant chaque requête.
--
-- Middleware required:
-- SET LOCAL request.jwt.claims = '{"userId":"uuid","role":"admin"}';
-- SET LOCAL request.jwt.user_id = 'uuid';

-- ==========================================
-- POLICIES: Dossiers
-- ==========================================

-- DENY par défaut (fail-safe)
CREATE POLICY deny_all_dossiers ON dossiers
  FOR ALL
  USING (false);

-- Les admins peuvent lire tous les dossiers
CREATE POLICY admins_read_all_dossiers ON dossiers
  FOR SELECT
  USING (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'admin'
  );

-- Les clients ne lisent que leurs dossiers
CREATE POLICY users_read_own_dossiers ON dossiers
  FOR SELECT
  USING (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'user'
    AND user_id = current_setting('request.jwt.user_id', true)::UUID
  );

-- Les clients peuvent créer des dossiers (uniquement si status = 'En attente')
CREATE POLICY users_create_dossiers ON dossiers
  FOR INSERT
  WITH CHECK (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'user'
    AND user_id = current_setting('request.jwt.user_id', true)::UUID
    AND status = 'En attente'
  );

-- Les admins peuvent modifier tous les dossiers
CREATE POLICY admins_update_dossiers ON dossiers
  FOR UPDATE
  USING (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'admin'
  );

-- Les clients peuvent modifier seulement si status != 'Soumis' et != 'Complété'
CREATE POLICY users_update_own_dossiers ON dossiers
  FOR UPDATE
  USING (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'user'
    AND user_id = current_setting('request.jwt.user_id', true)::UUID
    AND status NOT IN ('Soumis', 'Complété')
  );

-- Audit trigger pour dossiers
CREATE TRIGGER audit_dossiers_trigger
  AFTER INSERT OR UPDATE OR DELETE ON dossiers
  FOR EACH ROW EXECUTE FUNCTION audit_log_trigger();

-- ==========================================
-- POLICIES: Conversations
-- ==========================================

-- DENY par défaut (fail-safe)
CREATE POLICY deny_all_conversations ON conversations
  FOR ALL
  USING (false);

-- Admins lisent toutes leurs conversations assignées
CREATE POLICY admins_read_assigned_conversations ON conversations
  FOR SELECT
  USING (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'admin'
    AND admin_id = current_setting('request.jwt.user_id', true)::UUID
  );

-- Clients lisent leurs conversations
CREATE POLICY users_read_own_conversations ON conversations
  FOR SELECT
  USING (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'user'
    AND user_id = current_setting('request.jwt.user_id', true)::UUID
  );

-- Les clients peuvent créer des conversations
CREATE POLICY users_create_conversations ON conversations
  FOR INSERT
  WITH CHECK (
    (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'user'
    AND user_id = current_setting('request.jwt.user_id', true)::UUID
  );

-- Audit trigger pour conversations
CREATE TRIGGER audit_conversations_trigger
  AFTER INSERT OR UPDATE OR DELETE ON conversations
  FOR EACH ROW EXECUTE FUNCTION audit_log_trigger();

-- ==========================================
-- POLICIES: Messages
-- ==========================================

-- DENY par défaut (fail-safe)
CREATE POLICY deny_all_messages ON messages
  FOR ALL
  USING (false);

-- Les participants d'une conversation peuvent lire ses messages
CREATE POLICY participants_read_messages ON messages
  FOR SELECT
  USING (
    EXISTS (
      SELECT 1 FROM conversations c
      WHERE c.id = conversation_id
      AND (
        (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'admin'
        OR c.user_id = current_setting('request.jwt.user_id', true)::UUID
        OR c.admin_id = current_setting('request.jwt.user_id', true)::UUID
      )
    )
  );

-- Les participants peuvent envoyer des messages
CREATE POLICY participants_send_messages ON messages
  FOR INSERT
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM conversations c
      WHERE c.id = conversation_id
      AND (
        (current_setting('request.jwt.claims', true)::jsonb ->> 'role') = 'admin'
        OR c.user_id = current_setting('request.jwt.user_id', true)::UUID
        OR c.admin_id = current_setting('request.jwt.user_id', true)::UUID
      )
    )
  );

-- Audit trigger pour messages
CREATE TRIGGER audit_messages_trigger
  AFTER INSERT OR UPDATE OR DELETE ON messages
  FOR EACH ROW EXECUTE FUNCTION audit_log_trigger();

-- Utilisateur de test (mot de passe: test1234)
INSERT INTO users (email, password_hash, name)
VALUES (
  'demo@misterdil.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Demo Conseiller'
);
