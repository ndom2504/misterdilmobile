# Matrice des Permissions - Misterdil

## Rôles

| Rôle | Description |
|------|-------------|
| `admin` | Conseiller immigration - peut gérer, valider, facturer |
| `user` (client) | Utilisateur final - peut créer, consulter, payer |

---

## Matrice par Ressource

### 📁 Dossiers (dossiers)

| Action | Admin | Client | Backend Rule |
|--------|-------|--------|-------------|
| **Lire tous les dossiers** | ✅ | ❌ | `GET /dossiers` : role=admin |
| **Lire ses propres dossiers** | ✅ | ✅ | `GET /dossiers` : user_id = jwt.userId OR role=admin |
| **Créer un dossier** | ✅ | ✅ | `POST /dossiers` : role=user OR role=admin |
| **Modifier un dossier** | ✅ | ❌ si status='Soumis' | `PUT /dossiers/:id` : role=admin OR (role=user AND status!='Soumis') |
| **Supprimer un dossier** | ✅ | ❌ | `DELETE /dossiers/:id` : role=admin |
| **Modifier le statut** | ✅ | ❌ | `PATCH /dossiers/:id/status` : role=admin |

---

### 💬 Conversations (conversations)

| Action | Admin | Client | Backend Rule |
|--------|-------|--------|-------------|
| **Lire toutes les conversations** | ✅ | ❌ | `GET /conversations` : role=admin |
| **Lire ses conversations** | ✅ | ✅ | `GET /conversations` : user_id = jwt.userId OR admin_id = jwt.userId |
| **Créer une conversation** | ✅ | ✅ | `POST /conversations` : role=user (auto) OR role=admin |
| **Modifier une conversation** | ✅ | ❌ | `PUT /conversations/:id` : role=admin |
| **Supprimer une conversation** | ✅ | ❌ | `DELETE /conversations/:id` : role=admin |

---

### ✉️ Messages (messages)

| Action | Admin | Client | Backend Rule |
|--------|-------|--------|-------------|
| **Lire les messages d'une conversation** | ✅ | ✅ | `GET /conversations/:id/messages` : member of conversation |
| **Envoyer un message** | ✅ | ✅ | `POST /conversations/:id/messages` : member of conversation |
| **Supprimer un message** | ✅ | ❌ | `DELETE /messages/:id` : role=admin |

---

### 💳 Paiements (payments)

| Action | Admin | Client | Backend Rule |
|--------|-------|--------|-------------|
| **Créer une demande de paiement** | ✅ | ❌ | `POST /payments` : role=admin |
| **Lire ses paiements** | ✅ | ✅ | `GET /payments` : user_id = jwt.userId OR role=admin |
| **Effectuer un paiement (Stripe)** | ❌ | ✅ | `POST /payments/:id/pay` : role=user AND payment.user_id = jwt.userId |
| **Annuler un paiement** | ✅ | ❌ | `DELETE /payments/:id` : role=admin |

---

### 👤 Utilisateurs (users)

| Action | Admin | Client | Backend Rule |
|--------|-------|--------|-------------|
| **Lire tous les utilisateurs** | ✅ | ❌ | `GET /users` : role=admin |
| **Lire son profil** | ✅ | ✅ | `GET /users/me` : authenticated |
| **Modifier son profil** | ✅ | ✅ | `PATCH /users/me` : authenticated |
| **Modifier un autre profil** | ✅ | ❌ | `PATCH /users/:id` : role=admin |
| **Supprimer un utilisateur** | ✅ | ❌ | `DELETE /users/:id` : role=admin |
| **Modifier le rôle** | ✅ (super-admin) | ❌ | `PATCH /users/:id/role` : role=super_admin |

---

## Règles Backend (Middleware)

### Middleware `withAuth` (existant)

```javascript
// Vérifie JWT, extrait userId et role
// Ajoute req.user = { userId, role }
```

### Nouveaux Guards

```javascript
// guardAdmin.js
const guardAdmin = (req, res, next) => {
  if (req.user.role !== 'admin') {
    return res.status(403).json({ error: 'Accès refusé' });
  }
  next();
};

// guardOwner.js
const guardOwner = (resourceField) => (req, res, next) => {
  if (req.user.role === 'admin') return next();
  if (req.user.userId === req[resourceField].user_id) return next();
  return res.status(403).json({ error: 'Accès refusé' });
};
```

---

## Règles Database (PostgreSQL RLS)

### Activer RLS

```sql
ALTER TABLE dossiers ENABLE ROW LEVEL SECURITY;
ALTER TABLE conversations ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;
ALTER TABLE payments ENABLE ROW LEVEL SECURITY;
```

### Policy Dossiers

```sql
-- Les admins peuvent tout lire
CREATE POLICY admins_read_all_dossiers ON dossiers
  FOR SELECT TO admin_role
  USING (true);

-- Les clients ne lisent que leurs dossiers
CREATE POLICY users_read_own_dossiers ON dossiers
  FOR SELECT TO user_role
  USING (user_id = current_user_id());

-- Les clients peuvent créer des dossiers
CREATE POLICY users_create_dossiers ON dossiers
  FOR INSERT TO user_role
  WITH CHECK (user_id = current_user_id());

-- Les admins peuvent modifier tous les dossiers
CREATE POLICY admins_update_dossiers ON dossiers
  FOR UPDATE TO admin_role
  USING (true);

-- Les clients peuvent modifier seulement si status != 'Soumis'
CREATE POLICY users_update_own_dossiers ON dossiers
  FOR UPDATE TO user_role
  USING (
    user_id = current_user_id() 
    AND status != 'Soumis'
  );
```

### Policy Conversations

```sql
-- Admins lisent toutes leurs conversations assignées
CREATE POLICY admins_read_assigned_conversations ON conversations
  FOR SELECT TO admin_role
  USING (admin_id = current_user_id());

-- Clients lisent leurs conversations
CREATE POLICY users_read_own_conversations ON conversations
  FOR SELECT TO user_role
  USING (user_id = current_user_id());

-- Clients peuvent créer des conversations
CREATE POLICY users_create_conversations ON conversations
  FOR INSERT TO user_role
  WITH CHECK (user_id = current_user_id());
```

### Policy Messages

```sql
-- Les participants d'une conversation peuvent lire ses messages
CREATE POLICY participants_read_messages ON messages
  FOR SELECT TO authenticated_role
  USING (
    EXISTS (
      SELECT 1 FROM conversations c
      WHERE c.id = conversation_id
      AND (c.user_id = current_user_id() OR c.admin_id = current_user_id())
    )
  );

-- Les participants peuvent envoyer des messages
CREATE POLICY participants_send_messages ON messages
  FOR INSERT TO authenticated_role
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM conversations c
      WHERE c.id = conversation_id
      AND (c.user_id = current_user_id() OR c.admin_id = current_user_id())
    )
  );
```

---

## Summary Checklist

- [x] Matrice permissions documentée
- [ ] Implémenter guards middleware
- [ ] Implémenter RLS PostgreSQL
- [ ] Mettre à jour tous les endpoints avec guards
- [ ] Tester chaque règle
