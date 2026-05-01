# GO-LIVE Checklist - Misterdil
*Checklist finale avant déploiement en production*

---

## 🔧 Backend (Node.js + Vercel)

### Environment Variables
- [ ] `DATABASE_URL` - Neon PostgreSQL connection string
- [ ] `JWT_SECRET` - Clé secrète JWT (min 32 caractères, aléatoire)
- [ ] `STRIPE_SECRET_KEY` - Stripe secret key (optionnel pour MVP)
- [ ] `STRIPE_PUBLISHABLE_KEY` - Stripe publishable key (optionnel pour MVP)

**Commande pour générer JWT_SECRET :**
```bash
openssl rand -hex 32
```

### Vérifications
- [ ] `node_modules/` installé (`npm install`)
- [ ] Scripts package.json fonctionnels
- [ ] Pas de console.log en production
- [ ] Error handling robuste
- [ ] Rate limiting configuré (Vercel)
- [ ] CORS configuré pour le domaine de production

### Tests
- [ ] Test inscription/connexion
- [ ] Test création dossier
- [ ] Test messagerie
- [ ] Test RLS (tenter accès non autorisé)
- [ ] Test audit logs (vérifier table après actions)

---

## 📱 Frontend (Android)

### Build Configuration
- [ ] `build.gradle.kts` configuré pour release
- [ ] `proguard-rules.pro` activé
- [ ] Version code/version name incrémentés
- [ ] Keystore pour signature APK
- [ ] API URL de production configurée

### Configuration API
- [ ] `BASE_URL` pointe vers Vercel production
- [ ] Timeout réseau configuré
- [ ] Error handling UI
- [ ] Loading states
- [ ] Crashlytics/Sentry configuré

### Tests
- [ ] Test sur émulateur Android
- [ ] Test sur device physique
- [ ] Test flux complet utilisateur
- [ ] Test offline behavior
- [ ] Test permissions (fichiers, etc.)

### Build Release
```bash
cd app
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

---

## 🗄️ Database (Neon PostgreSQL)

### Schema
- [ ] Exécuter `schema.sql` complet dans Neon SQL Editor
- [ ] Vérifier tables créées (users, dossiers, conversations, messages, audit_logs)
- [ ] Vérifier indexes créés
- [ ] Vérifier triggers audit créés

### RLS Policies
- [ ] Vérifier RLS activé sur toutes les tables
- [ ] Vérifier DENY policies présentes
- [ ] Vérifier policies basées sur JWT claims
- [ ] Test RLS manuel (tenter accès non autorisé)

**Vérification SQL :**
```sql
-- Vérifier RLS activé
SELECT tablename, rowsecurity 
FROM pg_tables 
WHERE schemaname = 'public';

-- Vérifier policies
SELECT schemaname, tablename, policyname, permissive, roles, cmd, qual 
FROM pg_policies 
WHERE schemaname = 'public';

-- Vérifier triggers
SELECT trigger_name, event_object_table 
FROM information_schema.triggers 
WHERE trigger_schema = 'public';
```

### Connection
- [ ] Connection pooling configuré
- [ ] SSL mode activé
- [ ] Timeout configuré
- [ ] Backup automatique activé (Neon)

---

## 🚀 Vercel Deployment

### Configuration
- [ ] Repo GitHub connecté à Vercel
- [ ] Environment variables configurées dans Vercel Dashboard
- [ ] Build command : `npm install`
- [ ] Output directory : `.`
- [ ] Framework preset : Other
- [ ] Node.js version : 18+

### Domain
- [ ] Custom domain configuré (optionnel)
- [ ] HTTPS automatique
- [ ] DNS propagé

### Monitoring
- [ ] Vercel Analytics activé
- [ ] Log drain configuré (optionnel)
- [ ] Alertes configurées

---

## 🔒 Sécurité

### Backend
- [ ] JWT secret fort (pas en clair dans le code)
- [ ] Password hashing bcrypt
- [ ] SQL injection protégé (parameterized queries)
- [ ] XSS protégé (sanitization)
- [ ] Rate limiting actif
- [ ] CORS restreint

### Database
- [ ] RLS actif et testé
- [ ] DENY policies par défaut
- [ ] Audit logs fonctionnels
- [ ] Pas de données sensibles en clair

### Android
- [ ] API calls HTTPS only
- [ ] Pas de secrets hardcoded
- [ ] Certificate pinning (optionnel)
- [ ] Proguard/R8 activé

---

## 📊 Monitoring Post-Déploiement

### Outils
- [ ] Vercel Dashboard (backend)
- [ ] Neon Dashboard (database)
- [ ] Firebase Crashlytics (Android)
- [ ] Sentry (error tracking - optionnel)

### KPIs à surveiller
- [ ] Taux de connexion réussie
- [ ] Temps de réponse API (< 2s)
- [ ] Erreurs RLS (audit_logs)
- [ ] Nombre de dossiers créés/jour
- [ ] Taux de complétion dossiers
- [ ] Crash rate Android

---

## ✅ Go/No-Go Decision

### Go si :
- [ ] Tous les items ci-dessus sont cochés
- [ ] Tests manuels passés avec succès
- [ ] Performance acceptable (< 2s API)
- [ ] Sécurité validée
- [ ] 5 utilisateurs ont testé l'UX

### No-Go si :
- [ ] Performance > 5s
- [ ] Sécurité compromise
- [ ] Bugs critiques non résolus
- [ ] UX confuse
- [ ] RLS non fonctionnel

---

## 🚨 Rollback Plan

### Si problème critique détecté :
1. **Backend** : Revert dernier déploiement Vercel
2. **Database** : Restaurer backup Neon (si disponible)
3. **Android** : Communiquer aux utilisateurs de ne pas mettre à jour

### Communication incident :
- Préparer template email/message
- Canaux : email, in-app notification
- SLA : réponse sous 1h, résolution sous 24h

---

## 📞 Support Post-Lancement

### Support Level 1 (Premier contact)
- Répondre aux questions utilisateurs
- Diagnostiquer problèmes simples
- Escalation vers Level 2 si nécessaire
- Disponibilité : 9h-18h (heure locale)

### Support Level 2 (Backend/Database)
- Debug backend issues
- Fix bugs critiques
- Optimiser performance
- Disponibilité : 24/7 (on-call)

### Support Level 3 (Architecture/Sécurité)
- Architecture changes
- Security incidents
- Database migrations
- Disponibilité : selon urgence

---

## 📝 Notes de déploiement

**Date prévue :** ___________
**Responsable déploiement :** ___________
**Approbateur :** ___________

**Signature Go/No-Go :** ___________
