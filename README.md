# SudriBet — Application de Paris Sportifs Universitaires

> Projet ESME Sudria 2025-2026  
> Paris sportifs en temps réel sur les matchs inter-écoles FFSU

---

## Présentation du projet

SudriBet est une application Android de paris sportifs dédiée aux compétitions universitaires de la **FFSU** (Fédération Française du Sport Universitaire). Elle récupère automatiquement les matchs officiels depuis le site FFSU, permet de parier en monnaie virtuelle (**SudriCoins**), et résout les paris automatiquement dès que les vrais scores sont publiés.

---

## Architecture

```
┌──────────────────────────┐
│     App Android (Kotlin)  │
│   Retrofit2 + Gson        │
└────────────┬─────────────┘
             │ HTTPS / REST
             ▼
┌──────────────────────────────────────────────┐
│          SudriBet API — Railway              │
│  https://sublime-manifestation-production    │
│         -f0ae.up.railway.app                │
│                                              │
│  Node.js · Express · Cheerio · pdftotext    │
└────────────┬─────────────────────────────────┘
             │ Scraping HTTP + parsing PDF
             ▼
┌──────────────────────────┐
│   sport-u.com (FFSU)     │
│  Calendriers & résultats │
│  officiels en PDF        │
└──────────────────────────┘
```

### Fonctionnement de l'API
1. Scrape le site officiel FFSU toutes les heures
2. Télécharge les PDFs (calendriers, quarts de finale, résultats)
3. Extrait les matchs avec `pdftotext` + parser custom (dates FR, équipes, scores)
4. Distingue matchs à venir (`isUpcoming: true`) et résultats passés avec scores réels

### Endpoints
| Route | Description |
|---|---|
| `GET /matches?upcoming=true` | Matchs à venir (pour parier) |
| `GET /matches` | Tous les matchs + résultats |
| `GET /health` | Statut + cache |
| `POST /refresh` | Forcer un rechargement |

---

## Division du travail — Présentation jury

### Julian — Backend & Intégration API

**Thème : « Comment on récupère de vrais matchs universitaires ? »**

- Conception de l'architecture globale
- Backend Node.js + Express : scraping FFSU, parsing PDF, endpoints REST
- Déploiement en production sur Railway (Docker)
- Intégration API dans l'app Android (Retrofit2, `MatchApiService`, `MatchViewModel`)
- Système de résolution automatique des paris (`BetResolver`) : l'app appelle l'API au démarrage, compare l'équipe choisie avec le vrai score, et tranche Gagné/Perdu
- Bonus : version iOS SwiftUI (démo rapide)

**À montrer :**
- `curl https://.../matches?upcoming=true` → vrais matchs FFSU en JSON
- Logique de parsing PDF (extraire "ASU BORDEAUX vs NANTES SU — 30 avril 2026")
- Résolution d'un pari : le score arrive → le pari se résout seul

---

### Malo — Interface utilisateur & Expérience

**Thème : « L'expérience de l'utilisateur dans l'app »**

- Design général de l'application : thème sombre, charte graphique, cohérence visuelle
- Écrans Home, Profil, Classement
- Navigation entre les onglets (Bottom Navigation)
- Système de gamification : SudriCoins, missions quotidiennes, badges
- Chat communautaire
- Dark mode / Light mode

**À montrer :**
- Navigation fluide entre les onglets
- Écran Home avec solde SudriCoins et missions
- Classement général des parieurs

---

### Sacha — Fonctionnalités de Paris

**Thème : « Comment on parie et comment on gagne ? »**

- Écran principal : liste des matchs temps réel, filtres par sport, recherche
- Interface de pari : sélection équipe (A / Nul / B), saisie de la mise, calcul des gains en temps réel
- Historique des paris : Gagné / Perdu / En cours
- Notifications push pour les résultats
- Système de bonus quotidien (`DailyManager`)
- Écran profil avec statistiques personnelles

**À montrer :**
- Parcours complet : voir un match → choisir une équipe → entrer une mise → confirmer
- Gains potentiels qui se calculent en direct
- Onglet Historique avec les paris résolus

---

## Télécharger l'app lors de la présentation

### Android (pour tout le monde dans la salle)

Télécharger l'APK depuis :  
**👉 [github.com/Julian33110/sudribet_appli_mobile/releases/latest](https://github.com/Julian33110/sudribet_appli_mobile/releases/latest)**

Ou scanner le QR code affiché pendant la démo.

> Avant d'installer : **Paramètres → Sécurité → Sources inconnues** (à activer une fois)

Étapes (30 secondes) :
1. Scanner le QR code
2. Télécharger `SudriBet.apk`
3. Ouvrir → Installer → Lancer

### iOS (démo bonus)

Présenté directement sur iPhone par Julian. Pas de téléchargement nécessaire.

---

## Lancer en local

### API

```bash
cd sudribet-api
npm install
node index.js          # → http://localhost:3000
```

Prérequis : `brew install poppler` (macOS) ou `apt install poppler-utils` (Linux)

### Android

Ouvrir dans **Android Studio** → Run.  
Ajouter dans `local.properties` :
```
GEMINI_API_KEY=votre_clé
```

---

## Équipe

| Nom | Rôle |
|---|---|
| **Julian** | Backend API + Architecture + Intégration Android |
| **Malo** | UX/UI Android + Gamification |
| **Sacha** | Fonctionnalités Paris + Historique |

---

## Stack technique

| Couche | Technologies |
|---|---|
| API | Node.js, Express, Cheerio, Axios, pdftotext |
| Déploiement | Railway, Docker |
| Android | Kotlin, Retrofit2, Gson, Gemini AI, RecyclerView, SharedPreferences |
| Données | FFSU sport-u.com — PDFs officiels |
