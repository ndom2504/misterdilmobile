# Guide d'installation Neon PostgreSQL
*Exécution de schema.sql sur Neon pour Misterdil*

---

## 📋 Prérequis

- Compte Neon actif (https://neon.tech)
- Projet Neon créé
- Connection string PostgreSQL disponible

---

## 🚀 Étape 1 : Accéder au SQL Editor

1. Connectez-vous à [Neon Console](https://console.neon.tech)
2. Sélectionnez votre projet
3. Cliquez sur **"SQL Editor"** dans le menu de gauche
4. Vous verrez un écran avec un champ de saisie SQL

---

## 📝 Étape 2 : Exécuter schema.sql

1. Ouvrez le fichier `backend/schema.sql` dans votre IDE
2. Copiez tout le contenu du fichier
3. Collez le contenu dans le SQL Editor Neon
4. Cliquez sur **"Run"** (ou utilisez `Ctrl+Enter`)

**⚠️ Important :** Si des tables existent déjà, vous devrez peut-être exécuter les migrations manuellement :

```sql
-- Migration admin_id
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS admin_id UUID REFERENCES users(id);

-- Migration dossier_id
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS dossier_id UUID REFERENCES dossiers(id);
```

---

## ✅ Étape 3 : Vérifier l'installation

Exécutez ces commandes pour vérifier que tout est correct :

### Vérifier les tables
```sql
SELECT tablename 
FROM pg_tables 
WHERE schemaname = 'public';
```

**Résultat attendu :**
```
- users
- dossiers
- conversations
- messages
- audit_logs
```

### Vérifier les indexes
```sql
SELECT indexname, tablename 
FROM pg_indexes 
WHERE schemaname = 'public';
```

**Résultat attendu :**
```
- idx_dossiers_user
- idx_conversations_user
- idx_conversations_admin
- idx_messages_conv
- idx_messages_timestamp
- idx_audit_logs_user
- idx_audit_logs_resource
- idx_audit_logs_timestamp
```

### Vérifier RLS activé
```sql
SELECT tablename, rowsecurity 
FROM pg_tables 
WHERE schemaname = 'public';
```

**Résultat attendu :** `rowsecurity = true` pour `dossiers`, `conversations`, `messages`

### Vérifier les RLS policies
```sql
SELECT schemaname, tablename, policyname, permissive, roles, cmd 
FROM pg_policies 
WHERE schemaname = 'public'
ORDER BY tablename, policyname;
```

**Résultat attendu :**
```
- deny_all_dossiers
- admins_read_all_dossiers
- users_read_own_dossiers
- users_create_dossiers
- admins_update_dossiers
- users_update_own_dossiers
- deny_all_conversations
- admins_read_assigned_conversations
- users_read_own_conversations
- users_create_conversations
- deny_all_messages
- participants_read_messages
- participants_send_messages
```

### Vérifier les triggers d'audit
```sql
SELECT trigger_name, event_object_table, action_statement 
FROM information_schema.triggers 
WHERE trigger_schema = 'public';
```

**Résultat attendu :**
```
- audit_dossiers_trigger
- audit_conversations_trigger
- audit_messages_trigger
```

---

## 🔧 Étape 4 : Récupérer la Connection String

1. Dans Neon Console, cliquez sur votre projet
2. Cliquez sur **"Connection Details"**
3. Copiez la **Connection string** (format : `postgresql://user:password@ep-xxx.us-east-1.aws.neon.tech/neondb?sslmode=require`)

**⚠️ Sécurité :** Ne communiquez jamais cette connection string publiquement.

---

## 📝 Étape 5 : Configurer Environment Variables

### Backend (Vercel)

Dans le dashboard Vercel :
1. Allez dans **Settings** → **Environment Variables**
2. Ajoutez les variables suivantes :

| Variable | Valeur |
|----------|--------|
| `DATABASE_URL` | Connection string Neon |
| `JWT_SECRET` | Clé secrète JWT (générer avec `openssl rand -hex 32`) |
| `STRIPE_SECRET_KEY` | (optionnel) Clé secrète Stripe |
| `STRIPE_PUBLISHABLE_KEY` | (optionnel) Clé publique Stripe |

### Générer JWT_SECRET
```bash
openssl rand -hex 32
```

---

## 🧪 Étape 6 : Test RLS (Optionnel mais recommandé)

Pour vérifier que les RLS fonctionnent correctement :

### Test 1 : Client ne peut pas voir les dossiers d'autres clients
```sql
-- Simuler un client
SET LOCAL request.jwt.claims = '{"userId":"test-user-id","role":"user"}';
SET LOCAL request.jwt.user_id = 'test-user-id';

-- Essayer de lire tous les dossiers
SELECT * FROM dossiers;
```

**Résultat attendu :** Aucune ligne (ou seulement les dossiers de ce user)

### Test 2 : Admin peut voir tous les dossiers
```sql
-- Simuler un admin
SET LOCAL request.jwt.claims = '{"userId":"admin-id","role":"admin"}';
SET LOCAL request.jwt.user_id = 'admin-id';

-- Lire tous les dossiers
SELECT * FROM dossiers;
```

**Résultat attendu :** Tous les dossiers

### Test 3 : Audit logs fonctionnent
```sql
-- Vérifier que les triggers sont actifs
INSERT INTO dossiers (client_name, type, status, progress, last_update, user_id)
VALUES ('Test Client', 'Études', 'En attente', 0.0, '01/01/2026', 'test-user-id');

SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 1;
```

**Résultat attendu :** Une ligne d'audit avec action = 'INSERT', resource = 'dossiers'

---

## 🚨 Dépannage

### Erreur : "relation does not exist"
**Cause :** Le schema.sql n'a pas été exécuté complètement
**Solution :** Réexécutez le schema.sql ou exécutez les migrations manuelles

### Erreur : "column does not exist"
**Cause :** Migration manquante (admin_id ou dossier_id)
**Solution :** Exécutez les migrations manuelles indiquées dans l'étape 2

### Erreur : "role does not exist"
**Cause :** RLS policy fait référence à un rôle inexistant
**Solution :** Les nouvelles policies utilisent des claims JWT, pas de rôles PostgreSQL. Vérifiez que vous utilisez la version à jour de schema.sql

---

## ✅ Checklist Finale

- [ ] schema.sql exécuté sans erreur
- [ ] Tables créées (5 tables)
- [ ] Indexes créés (8 indexes)
- [ ] RLS activé sur dossiers, conversations, messages
- [ ] RLS policies créées (13 policies)
- [ ] Triggers audit créés (3 triggers)
- [ ] Connection string Neon récupérée
- [ ] Environment variables configurées (Vercel)
- [ ] JWT_SECRET généré
- [ ] Test RLS réussi

---

## 📞 Support

Si vous rencontrez des problèmes :
1. Vérifiez les logs Neon Console
2. Consultez la documentation Neon : https://neon.tech/docs
3. Vérifiez que vous utilisez la dernière version de schema.sql
