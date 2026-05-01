# MVP Final Design - Misterdil
*Ce qui part en production maintenant*

---

## ✅ Critères de Production

| Critère | Statut | Notes |
|---------|--------|-------|
| Sécurité JWT + RLS | ✅ | Claims JWT, DENY policies, audit logs |
| Authentification | ✅ | Login/inscription, profil |
| Création dossiers | ✅ | Formulaire dynamique, sélection admin |
| Messagerie | ✅ | Chat 1-1, fichiers, paiement |
| Dashboard admin | ✅ | Liste clients, filtres, actions |
| Verrouillage métier | ✅ | Status-based RLS |
| Audit trail | ✅ | Table audit_logs avec triggers |

---

## 🚀 Fonctionnalités MVP (Production)

### 🔐 Sécurité
- JWT authentification avec claims
- RLS PostgreSQL basé sur claims JWT
- DENY policies par défaut (fail-safe)
- Audit logs automatiques (triggers)
- Guards middleware (guardAdmin, guardConversationMember)

### 👤 Utilisateurs
- Inscription/Connexion
- Profil utilisateur (nom, email)
- Rôles : admin / user
- 2 comptes admin fixes (seed)

### 📁 Dossiers
- Création dossier client
- Formulaire dynamique par type
- Sélection admin après création
- Conversation automatique créée
- Status : En attente → Soumis → En cours → Complété
- Verrouillage : client ne peut pas modifier si Soumis/Complété
- Admin peut voir et modifier tous les dossiers

### 💬 Messagerie
- Liste conversations
- Chat 1-1 en temps réel
- Partage de fichiers
- Demande de paiement (admin → client)
- Carte paiement côté client avec bouton "Payer"

### 🎨 UI/UX
- Bottom navigation sans labels
- Accueil client avec dossiers + sélection admin
- Dashboard admin avec liste clients + filtres
- Filtres par statut (Tous, En attente, Soumis, En cours, Complété)
- Filtres par type (Tous + types dynamiques)
- Dossier verrouillé si soumis (message + badge)

---

## ⚠️ Ce qui N'EST PAS dans MVP (Post-MVP)

### 💳 Paiements
- Intégration Stripe complète
- Webhooks Stripe
- Liste paiements
- Reçu PDF
- *Actuellement : simple demande de paiement via chat*

### 📊 Analytics
- Dashboard statistiques admin
- Rapports dossiers
- KPIs
- Export CSV

### 🔔 Notifications
- Push notifications
- Email notifications
- In-app notifications

### 🎨 UX Premium
- Dark mode
- Animations
- Onboarding tutorial
- FAQ intégrée

---

## 🗄️ Architecture Production

### Backend (Node.js + Vercel)
```
backend/
├── api/v1/
│   ├── auth/          # JWT auth
│   ├── dossiers/      # CRUD dossiers
│   ├── conversations/ # Chat + messages
│   └── users/         # Profil
├── lib/
│   ├── db.js          # Neon PostgreSQL
│   ├── auth.js        # JWT verify/sign
│   ├── middleware.js  # withAuth + SET LOCAL claims
│   └── guards.js      # guardAdmin, guardConversationMember
└── schema.sql         # Tables + RLS + audit logs
```

### Frontend (Android Kotlin)
```
app/src/main/java/com/example/misterdil/
├── MainActivity.kt
├── data/
│   ├── models/        # Dossier, Conversation, Message
│   ├── remote/        # API services
│   ├── local/         # Room DB
│   └── repository/    # Repository pattern
├── ui/
│   ├── screens/       # Compose screens
│   └── viewmodels/    # State management
└── navigation/        # Navigation
```

### Database (Neon PostgreSQL)
```
Tables:
- users (id, email, password_hash, name, role)
- dossiers (id, client_name, type, status, progress, user_id)
- conversations (id, client_name, project_name, user_id, admin_id, dossier_id)
- messages (id, conversation_id, sender_id, text, timestamp, is_from_me)
- audit_logs (id, user_id, action, resource, resource_id, details, timestamp)

RLS Policies:
- DENY par défaut sur toutes les tables
- Admin peut tout voir/modifier
- Client voit seulement ses données
- Verrouillage par statut (Soumis/Complété)
```

---

## 📋 Checklist Pré-Production

### Backend
- [x] Schema.sql avec RLS + audit logs
- [x] Middleware withAuth avec SET LOCAL claims
- [x] Guards middleware
- [ ] Rate limiting (Vercel)
- [ ] CORS configuré
- [ ] Environment variables (JWT_SECRET, DATABASE_URL)
- [ ] Tests E2O (end-to-end)

### Frontend
- [x] Android app fonctionnelle
- [ ] Build release APK
- [ ] Signature APK
- [ ] Proguard/R8
- [ ] Crashlytics (Sentry)
- [ ] Analytics (Firebase)

### Neon
- [ ] Exécuter schema.sql complet
- [ ] Vérifier RLS policies actives
- [ ] Vérifier audit_logs triggers
- [ ] Configurer connection pooling
- [ ] Backup automatique

### Vercel
- [ ] Connecter repo GitHub
- [ ] Configurer environment variables
- [ ] Déployer backend
- [ ] Configurer custom domain
- [ ] HTTPS automatique

### Sécurité
- [x] JWT secret fort
- [x] Password hashing bcrypt
- [x] RLS policies basées sur JWT claims
- [x] Audit logs
- [ ] Input validation
- [ ] SQL injection protected (parameterized queries)

---

## 🚀 Déploiement

### 1. Backend (Vercel)
```bash
cd backend
npm install
vercel login
vercel
# Configurer environment variables:
# - JWT_SECRET
# - DATABASE_URL (Neon)
```

### 2. Frontend (Android)
```bash
cd app
./gradlew assembleRelease
# APK généré dans app/build/outputs/apk/release/
```

### 3. Database (Neon)
```sql
-- Exécuter schema.sql complet dans Neon SQL Editor
-- Vérifier:
SELECT * FROM pg_policies WHERE schemaname = 'public';
SELECT * FROM information_schema.triggers WHERE trigger_schema = 'public';
```

---

## 📊 Monitoring Post-Déploiement

### KPIs à surveiller
- Taux de connexion réussie
- Temps de réponse API
- Erreurs RLS (audit_logs)
- Nombre de dossiers créés/jour
- Taux de complétion dossiers

### Outils
- Vercel Analytics (backend)
- Firebase Crashlytics (Android)
- Neon Dashboard (database)
- Sentry (error tracking)

---

## 🔄 Itération V1 (Post-Lancement)

### Sprint 1 (Semaine 1-2)
- Intégration Stripe complète
- Webhooks Stripe
- Liste paiements client

### Sprint 2 (Semaine 3-4)
- Push notifications
- Email notifications
- Dashboard statistiques admin

### Sprint 3 (Semaine 5-6)
- Dark mode
- Animations
- Onboarding tutorial

---

## ✅ Go/No-Go Decision

### Go si :
- [x] Sécurité RLS validée
- [x] Audit logs fonctionnels
- [x] Tests manuels passés
- [ ] Performance acceptable (< 2s API)
- [ ] UX testée avec 5 utilisateurs

### No-Go si :
- Performance > 5s
- Sécurité compromise
- Bugs critiques
- UX confuse

---

## 📞 Support Post-Lancement

### Support Level 1
- Répondre aux questions utilisateurs
- Diagnostiquer problèmes simples
- Escalation vers Level 2 si nécessaire

### Support Level 2
- Debug backend issues
- Fix bugs critiques
- Optimiser performance

### Support Level 3
- Architecture changes
- Security incidents
- Database migrations
