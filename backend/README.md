# Misterdil Backend

API REST — Node.js serverless sur **Vercel** + **Neon** PostgreSQL.

## Déploiement

### 1. Neon (base de données)
1. Créer un compte sur [neon.tech](https://neon.tech)
2. Créer un nouveau projet `misterdil`
3. Aller dans **SQL Editor** et coller le contenu de `schema.sql` → Exécuter
4. Copier la **Connection string** depuis Dashboard > Connection Details

### 2. Vercel (API)
1. Installer Vercel CLI : `npm i -g vercel`
2. Dans ce dossier : `vercel`
3. Configurer les variables d'environnement dans le dashboard Vercel (ou via CLI) :

```bash
vercel env add DATABASE_URL
vercel env add JWT_SECRET
vercel env add STRIPE_SECRET_KEY
vercel env add STRIPE_PUBLISHABLE_KEY
```

4. Déployer en production : `vercel --prod`
5. Copier l'URL de déploiement (ex: `https://misterdil-backend.vercel.app`)

### 3. Android — Mettre à jour la BASE_URL
Dans `DossierApiService.kt`, remplacer :
```kotlin
const val BASE_URL = "https://misterdil-backend.vercel.app/api/v1/"
```

Dans `MisterdilApplication.kt`, remplacer la clé Stripe :
```kotlin
PaymentConfiguration.init(applicationContext, "pk_test_votre_cle_ici")
```

## Endpoints

| Méthode | Route | Auth | Description |
|---------|-------|------|-------------|
| POST | `/api/v1/auth/login` | ❌ | Connexion |
| POST | `/api/v1/auth/logout` | ✅ | Déconnexion |
| GET | `/api/v1/dossiers` | ✅ | Liste des dossiers |
| GET | `/api/v1/dossiers/:id` | ✅ | Détail dossier |
| GET | `/api/v1/conversations` | ✅ | Liste des conversations |
| GET | `/api/v1/conversations/:id/messages` | ✅ | Messages d'une conversation |
| POST | `/api/v1/conversations/:id/messages` | ✅ | Envoyer un message |
| POST | `/api/v1/payments/create-intent` | ✅ | Créer un PaymentIntent Stripe |

## Compte démo
- Email : `demo@misterdil.com`
- Mot de passe : `test1234`
