# Backlog Technique MVP - Misterdil

---

## MVP (Minimum Viable Product)
*Ce qu'il faut absolument pour un lancement fonctionnel*

### 🔐 Sécurité & Authentification
- [x] Authentification JWT
- [x] Rôles Admin/User
- [x] Middleware `withAuth`
- [x] Guards middleware (guardAdmin, guardOwner, guardConversationMember)
- [x] Matrice des permissions documentée
- [x] RLS PostgreSQL policies
- [ ] Configuration JWT roles mapping dans Neon
- [ ] Tests de sécurité (tentative d'accès non autorisé)

### 👤 Gestion Utilisateurs
- [x] Inscription/Connexion
- [x] Profil utilisateur (nom, email, photo)
- [ ] Modification profil utilisateur
- [ ] Réinitialisation mot de passe
- [ ] Seed admin accounts (2 comptes fixes)

### 📁 Dossiers (Core Feature)
- [x] Création dossier (formulaire dynamique)
- [x] Liste dossiers client
- [x] Détails dossier
- [x] Verrouillage dossier soumis
- [x] Lien dossier ↔ conversation
- [ ] Modification dossier (admin)
- [ ] Suppression dossier (admin)
- [ ] Changement statut dossier (admin)
- [ ] Templates formulaires backend JSON (au lieu de hardcoded)

### 💬 Messagerie
- [x] Liste conversations
- [x] Chat 1-1
- [x] Message automatique après création dossier
- [x] Partage de fichiers
- [ ] Notifications messages
- [ ] Marquer messages comme lus
- [ ] Historique complet

### 💳 Paiements
- [x] Demande de paiement (admin)
- [x] Carte paiement côté client
- [ ] Intégration Stripe complète
- [ ] Liste paiements client
- [ ] Reçu PDF
- [ ] Webhooks Stripe pour statut

### 🎨 UI/UX
- [x] Bottom navigation sans labels
- [x] Accueil client avec sélection admin
- [x] Dashboard admin avec liste clients
- [x] Filtres admin (statut/type)
- [ ] Dark mode
- [ ] Animations transitions
- [ ] Loading states cohérents
- [ ] Gestion erreurs utilisateur-friendly

### 📱 Mobile
- [x] Android app (Compose)
- [ ] iOS app (React Native ou Flutter)
- [ ] Responsive design

### 🗄️ Backend
- [x] API REST dossiers
- [x] API conversations/messages
- [x] API auth
- [ ] API paiements
- [ ] Rate limiting
- [ ] Logging
- [ ] Monitoring (Sentry)

---

## V1 (Post-MVP)
*Améliorations à ajouter après le lancement*

### 🚀 Fonctionnalités avancées
- [ ] Formulaires dynamiques backend (JSON schema)
- [ ] Validation champs en temps réel
- [ ] Sauvegarde automatique brouillons
- [ ] Historique modifications dossier
- [ ] Commentaires internes admin
- [ ] Tags/labels dossiers
- [ ] Recherche avancée dossiers

### 📊 Analytics & Reporting
- [ ] Dashboard statistiques admin
- [ ] Rapports dossiers
- [ ] KPIs (temps de traitement, taux de complétion)
- [ ] Export CSV/Excel

### 🔔 Notifications Push
- [ ] Firebase Cloud Messaging
- [ ] Notifications en temps réel
- [ ] Préférences notifications

### 🤝 Collaboration
- [ ] Multi-admins sur même dossier
- [ ] Assignation automatique dossiers
- [ ] Escalation dossiers
- [ ] Notes partagées

### 🎨 UX Premium
- [ ] Onboarding tutorial
- [ ] FAQ intégrée
- [ ] Chatbot support
- [ ] Vidéos explicatives

---

## Priorités Réalistes (Timeline)

### Sprint 1 (Semaine 1-2) - MVP Core
1. Configuration RLS Neon + JWT mapping
2. Tests sécurité
3. Seed admin accounts endpoint
4. Modification profil utilisateur

### Sprint 2 (Semaine 3-4) - Dossiers Admin
1. Modification dossier admin
2. Changement statut dossier
3. Templates formulaires backend JSON
4. Historique modifications

### Sprint 3 (Semaine 5-6) - Paiements Complets
1. Intégration Stripe complète
2. Liste paiements client
3. Reçu PDF
4. Webhooks Stripe

### Sprint 4 (Semaine 7-8) - Polish
1. Dark mode
2. Animations
3. Loading states
4. Gestion erreurs
5. Tests E2E

---

## Dépendances Techniques

### Backend
- Node.js 18+
- Neon PostgreSQL (RLS enabled)
- Vercel (hosting)
- Stripe (paiements)
- JWT (auth)

### Frontend (Android)
- Kotlin
- Jetpack Compose
- Retrofit
- Room (local DB)
- Coroutines/Flow

### Outils
- Git (version control)
- GitHub (CI/CD)
- Sentry (monitoring)

---

## Risques & Mitigations

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|--------|------------|
| RLS Neon non configuré | Moyen | Critique | Tests pré-production |
| Stripe integration complexe | Moyen | Haut | Documentation Stripe |
| Performance Android | Faible | Moyen | Optimisations Room + caching |
| Sécurité JWT | Faible | Critique | Guards + RLS double couche |
