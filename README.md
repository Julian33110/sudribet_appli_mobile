# SudriBet — Application de Paris Sportifs Universitaires

> Projet ESME Sudria 2025-2026  
> Paris sportifs en temps réel sur les matchs inter-écoles FFSU

---

## Présentation du projet

SudriBet est une application mobile de paris sportifs dédiée aux compétitions universitaires de la **FFSU** (Fédération Française du Sport Universitaire). Elle récupère automatiquement les matchs officiels, permet de parier en monnaie virtuelle (**SudriCoins**), et résout les paris automatiquement dès que les résultats sont publiés.

### Plateformes
| Plateforme | Technologie | Statut |
|---|---|---|
| Android | Kotlin + Jetpack | ✅ Disponible |
| iOS | SwiftUI | ✅ Disponible |
| API Backend | Node.js + Railway | ✅ En production |

---

## Architecture technique

```
┌─────────────────────────────────────────────┐
│              Applications mobiles            │
│   Android (Kotlin)    │    iOS (SwiftUI)     │
└──────────────┬──────────────────────────────┘
               │ HTTPS / REST
               ▼
┌─────────────────────────────────────────────┐
│           SudriBet API (Railway)             │
│   https://sublime-manifestation-production   │
│          -f0ae.up.railway.app               │
│                                              │
│  Node.js + Express + Cheerio + pdftotext    │
└──────────────┬──────────────────────────────┘
               │ Scraping HTTP + PDF
               ▼
┌─────────────────────────────────────────────┐
│         sport-u.com (FFSU officiel)          │
│   PDFs calendriers & résultats par sport    │
└─────────────────────────────────────────────┘
```

### Fonctionnement de l'API
1. L'API scrape le site officiel FFSU toutes les heures
2. Elle télécharge les PDFs (calendriers, quarts de finale, résultats)
3. Elle extrait les matchs avec `pdftotext` et un parser custom
4. Elle expose les matchs à venir (`isUpcoming: true`) et les résultats passés

### Endpoints principaux
| Méthode | Route | Description |
|---|---|---|
| GET | `/matches?upcoming=true` | Matchs à venir (pour parier) |
| GET | `/matches` | Tous les matchs + résultats |
| GET | `/matches/:id` | Détail d'un match |
| GET | `/sports` | Liste des sports disponibles |
| GET | `/health` | Statut de l'API |
| POST | `/refresh` | Forcer un rechargement des données |

---

## Division du travail — Présentation jury

### Julian — Backend & Architecture

**Thème : « Comment l'application récupère de vrais matchs ? »**

- Conception de l'architecture globale du projet (API + Mobile)
- Développement du backend Node.js + Express
- Scraping automatique du site FFSU (Cheerio) et parsing des PDFs (pdftotext)
- Système de détection des matchs à venir vs résultats passés
- Déploiement en production sur Railway avec Docker
- Intégration API dans l'application Android (Retrofit2)
- Système de résolution automatique des paris (`BetResolver`) : dès qu'un match se termine, les paris sont résolus en comparant l'équipe choisie avec le vrai score FFSU

**Points clés à montrer :**
- Live demo : `GET /matches?upcoming=true` → vrais matchs FFSU du 30 avril au 21 mai
- Rafraîchissement automatique toutes les heures (cache + Railway)
- Expliquer comment `pdftotext` extrait les données des PDFs officiels FFSU

---

### Malo — Application iOS

**Thème : « L'expérience utilisateur sur iPhone »**

- Développement complet de l'application iOS en SwiftUI
- Thème visuel dark (#00FF88 — vert néon sur fond sombre)
- Écran de connexion et inscription (`LoginView`)
- Interface de paris (`BettingView`) avec affichage des cotes
- Navigation multi-onglets (`MainTabView`)
- Compatibilité iPhone (iOS 26.3+)
- Adaptation du design pour les standards Apple (Human Interface Guidelines)

**Points clés à montrer :**
- Navigation fluide entre les écrans
- Cohérence visuelle avec la version Android
- Expérience utilisateur spécifique iOS (gestures, animations SwiftUI)

---

### Sacha — Application Android

**Thème : « Les fonctionnalités de l'app Android »**

- Développement de l'application Android en Kotlin
- Interface principale : liste des matchs temps réel, filtres par sport, recherche
- Système de gamification : **SudriCoins**, missions quotidiennes, classement global
- Écran profil avec avatar, statistiques et historique
- Chat communautaire entre parieurs
- Bonus quotidien (`DailyManager`) et badges de progression
- Notifications push pour les résultats des paris
- Dark mode / Light mode

**Points clés à montrer :**
- Placer un pari en direct : sélectionner match → choisir équipe → saisir mise → confirmer
- Voir les gains potentiels calculés en temps réel selon la cote
- Historique des paris avec statut Gagné / Perdu / En cours
- Classement général des parieurs

---

## Télécharger l'application lors de la présentation

### Android

**Scanner le QR code** ou télécharger directement depuis :  
👉 [GitHub Releases — SudriBet.apk](https://github.com/Julian33110/sudribet_appli_mobile/releases/latest)

> ⚠️ Avant d'installer : aller dans **Paramètres → Sécurité → Sources inconnues** et activer l'option.

Étapes d'installation :
1. Scanner le QR code affiché pendant la démo
2. Télécharger `SudriBet.apk` (17 Mo)
3. Ouvrir le fichier → **Installer**
4. Lancer SudriBet

### iOS

1. Installer **TestFlight** depuis l'App Store
2. Scanner le QR code iOS ou ouvrir le lien d'invitation
3. Accepter → **Installer SudriBet**

> Si TestFlight n'est pas disponible, l'app iOS sera démontrée en direct sur iPhone pendant la présentation.

---

## Lancer le projet en local (développement)

### API Backend

```bash
cd sudribet-api
npm install
node index.js
# → http://localhost:3000
```

Prérequis : `pdftotext` installé
- macOS : `brew install poppler`
- Linux : `apt install poppler-utils`

### Application Android

Ouvrir le dossier dans **Android Studio**, puis :
- Build → Run `app`

Créer `local.properties` à la racine avec :
```
GEMINI_API_KEY=votre_clé_gemini
```

### Application iOS

Ouvrir `SudriBet.xcodeproj` dans **Xcode**, sélectionner un iPhone, puis `⌘R`.

---

## Équipe

| Nom | Rôle | GitHub |
|---|---|---|
| Julian | Backend API + Architecture + Intégration | [@Julian33110](https://github.com/Julian33110) |
| Malo | Application iOS (SwiftUI) | [@malWakanda](https://github.com/malWakanda) |
| Sacha | Application Android (Kotlin) | — |

---

## Technologies

| Composant | Stack |
|---|---|
| API | Node.js, Express, Cheerio, Axios, pdftotext (poppler) |
| Déploiement | Railway, Docker |
| Android | Kotlin, Retrofit2, Gson, Gemini AI SDK, RecyclerView, SharedPreferences |
| iOS | SwiftUI, URLSession |
| Données | FFSU sport-u.com — scraping PDF automatique |
