# SudriBet — Application Mobile Android de Paris Sportifs Étudiants

> Projet ESME Sudria 2025-2026  
> Sacha Lathuillière · Julian Expert · Malo Greffier  
> ESME / CFA SACEF

---

## 01 — Le Projet

**SudriBet** est une application mobile Android de paris sportifs dédiée aux compétitions universitaires inter-écoles (ESME, EPITA, IPSA, CentraleSupélec…).

### Nos missions
- Permettre aux élèves de **parier** sur les matchs en argent fictif (SudriCoins)
- Proposer une expérience mobile native avec **IA intégrée** (SudriBot)
- **Centraliser** les informations liées aux sports de l'école

### Nos objectifs
| Objectif | Description |
|---|---|
| **Engouement** | Générer de l'intérêt autour des événements sportifs |
| **Esprit de compétition** | Créer une rivalité saine et renforcer le sentiment d'appartenance |
| **Inclusion** | Permettre à chaque étudiant de se sentir intégré |

---

## 02 — Analyse

### Valeur ajoutée
- Fédérer autour du sport
- Stimuler la vie du campus
- Alternative saine aux paris réels
- Accessibilité pour tous les étudiants
- Reconnaissance via un système de récompenses

### Les chiffres *(sondage Microsoft Forms 08/2025 — étudiants ESME)*

| Stat | Valeur |
|---|---|
| Prêts à se déplacer pour assister aux compétitions sportifs s'ils étaient informés | **60%** |
| Déjà utilisateurs de sites de paris en ligne | **12%** |
| Utilisent leur smartphone quotidiennement | **100%** |

### Utilisateur cible — Maxime
- **Poste :** Étudiant en école d'ingénieur
- **Âge :** 19 ans
- **Objectifs :** Profiter au maximum des opportunités offertes par son école
- **Préoccupations :** Se sentir intégré et participer à des événements
- **Profil :** Innovant, sportif, joueur, ouverture d'esprit

---

## 03 — Vision & Stratégie Produit

| | |
|---|---|
| **Vision** | Augmenter de **50%** la présence des supporters aux matchs |
| **Target Group** | Les étudiants de l'ESME, surtout les premières années |
| **Besoins étudiants** | Centralisation des infos (scores, calendriers), accès mobile natif aux matchs en temps réel |
| **Besoins BDS** | Diffusion et promotion des événements sportifs |
| **Produit** | Application mobile Android de paris fictifs en SudriCoins (monnaie virtuelle) |

### Partenaire clé — BDS IONIS
- Assurer l'alimentation des données et la promotion de la solution
- **Business Goal :** Augmenter la participation aux événements sportifs
- **Pérennité :** Que SudriBet devienne l'outil officiel du BDS

---

## 04 — Modèle Conceptuel

### Cas d'utilisations (Étudiant)
- Consulter ses jetons, les matchs, le classement, l'historique des paris
- Parier sur un match
- Se connecter / S'inscrire

### Cas d'utilisations (Administrateur BDS)
- Renseigner les résultats
- Gérer la communication, les utilisateurs, les événements sportifs
- Générer le classement final

### Architecture 3 couches

```
┌─────────────────────────────────────────────────────────────────┐
│  COUCHE PRÉSENTATION — ACTIVITIES + ADAPTERS                    │
│  LoginActivity  HomeActivity  MainActivity  HistoryActivity     │
│  ProfilActivity  ChatActivity  LeaderboardActivity              │
│  MatchDetailActivity                                            │
│  → Jetpack Compose · Android Views XML · Lottie Animations     │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  COUCHE MÉTIER — MANAGERS + VIEWMODEL                           │
│  MatchViewModel  BetResolver  DailyManager  BadgeSystem         │
│  ActivityTransitions  EmailService  LocalNotificationHelper     │
│  → Kotlin 2.2.0 · Coroutines · Kotlin Flow · MVVM              │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  COUCHE DONNÉES & SERVICES EXTERNES                             │
│  SharedPreferences (JSON)  Firebase Analytics                   │
│  Gemini API (SudriBot)  Retrofit + OkHttp  Gson                 │
│  → Stockage 100% local · Android 7.0+ (API 24) → Android 15    │
└─────────────────────────────────────────────────────────────────┘
```

---

## 05 — Méthode de Développement

**Méthodologie Agile (Scrum)**
- Organisation de l'équipe en sprints
- Rituels du projet : daily standups, reviews
- **But :** livrer un MVP fonctionnel le plus vite possible

### Outils utilisés
| Outil | Usage |
|---|---|
| Notion | Gestion de projet, backlog |
| Microsoft Teams | Communication équipe |
| Postman | Tests API |
| GitHub | Versioning |
| VS Code / Android Studio | Développement |
| Arc | Navigation & documentation |

---

## 06 — Story Mapping

| Découvrir l'app | Se Login | Parier |
|---|---|---|
| Être sensibilisé par le BDS | Accéder à la page d'accueil | Avoir une vision globale des matchs |
| Être sensibilisé via les réseaux sociaux | Pouvoir créer un compte personnel | Enregistrer des paris |
| | | Visionner son historique |
| | | Accéder au classement |

---

## 07 — Implémentation

### UI / Activities
- `LoginActivity` — écran de connexion / inscription
- `HomeActivity` — dashboard principal avec SudriCoins et missions
- `MainActivity` — liste des matchs (Jetpack Compose)
- `ProfilActivity` — profil avec badges et statistiques
- `LeaderboardActivity` — classement général
- `HistoryActivity` — historique des paris
- `ChatActivity` — SudriBot (IA intégrée)
- `MatchDetailActivity` — détail d'un match avec simulateur de score

### Logique Métier
- `MatchViewModel` — gestion des données matchs (Kotlin Flow + Coroutines)
- `BetResolver` — résolution automatique des paris (Gagné / Perdu)
- `DailyManager` — missions et bonus quotidiens
- `BadgeSystem` — attribution des 10 types de badges
- `ActivityTransitions` — navigation entre écrans
- `EmailService` — envoi d'email de confirmation à l'inscription
- `LocalNotificationHelper` — notifications locales Android

### Services Externes
- `SharedPreferences (JSON)` — persistance locale via Gson
- `Firebase Analytics` — suivi des événements
- `Gemini API (SudriBot)` — assistant IA Gemini 2.0 Flash
- `Retrofit + OkHttp` — communication réseau

---

## 08 — Épopées Fonctionnelles

### Épopée : Inscription et Connexion
**Connexion Étudiant** — Se connecter avec ses identifiants pour un accès sécurisé et facile  
**Activation du Compte** — Activer son compte via une confirmation par email

### Épopée : Placer des Paris
**Prise en compte de la mise :**
- Parier des points virtuels (SudriCoins)
- Recevoir la confirmation du pari
- Récupérer les récompenses si victoire

**Possibilités d'analyse :**
- Voir la liste complète des matchs
- Filtrage par sport (Football, Rugby, Basket, Handball, Volley)
- Historique des matchs et des paris

### Épopée : Administration / Gestion
**Ajouter / Modifier des matchs :** Les administrateurs BDS valident les paris, ajoutent de nouveaux matchs, modifient les détails des événements  
**Exporter les résultats :** Générer et exporter les résultats des matchs et les données utilisateurs

---

## 09 — Modèle de Données

### `data class Bet`
```kotlin
data class Bet(
    val id              : String,  // UUID unique
    val description     : String,  // ex : "ESME vs EPITA (1), CentraleSupélec vs ESME (X)"
    val totalCote       : Double,  // cote combinée (produit des cotes individuelles)
    val mise            : Double,  // montant misé en SudriCoins
    val gainsPotentiels : Double,  // totalCote × mise
    val date            : String,  // format "dd/MM HH:mm"
    val status          : String   // "En cours" | "Gagné" | "Perdu"
)
```

### `data class Match`
```kotlin
data class Match(
    val id        : String,
    val equipeA   : String,           // équipe domicile
    val equipeB   : String,           // équipe extérieure
    val coteA     : Double,           // cote victoire A
    val coteB     : Double,           // cote victoire B
    val heure     : String,           // "HH:MM"
    val categorie : String,           // "Football"|"Rugby"|"Basket"|"Handball"|"Volley"
    val isLive    : Boolean = false,  // score mis à jour toutes les 3 secondes
    val scoreA    : Int     = 0,
    val scoreB    : Int     = 0,
    val coteNul   : Double? = null    // nul disponible uniquement au football
)
```

### Persistance locale
- Stockage **100% local** via `SharedPreferences`
- Paris sérialisés en **JSON** (Gson)
- Solde stocké en `Float`
- Pas de base de données externe
- Compatible **Android 7.0+ (API 24)**

---

## 10 — Tests

Tests réalisés sur **Xiaomi 24094RAD4G — Android 15.0 (API 35, arm64-v8a)**  
Build successful, **0 erreur**

Communication Front/Back testée via Postman :
```
POST /api/login
→ 200 OK · 43ms · 513B
{ "success": true, "user": { "email": "...", "emailConfirmed": true } }
```

---

## 11 — Difficultés Rencontrées

| Difficulté | Problème | Solution |
|---|---|---|
| **Compatibilité Android** | Certaines fonctions changent de comportement selon la version du téléphone | Détection de la version Android au lancement, utilisation de la bonne méthode selon le modèle |
| **Mélange de deux technologies UI** | XML classique et Jetpack Compose ne communiquent pas naturellement | Utilisation d'un composant pont (`ComposeView`) pour intégrer Compose dans une page XML |
| **Sauvegarde des données en local** | Le téléphone ne peut stocker que du texte simple, pas des objets complexes | Sérialisation des paris en JSON avant sauvegarde, désérialisation à la lecture (Gson) |
| **Sécuriser la clé API de l'IA** | La clé Gemini ne doit jamais être visible dans le code source | Stockage dans `local.properties` ignoré par Git, injection automatique à la compilation |

---

## 12 — Implémentations Futures

| Priorité | Fonctionnalité | Description |
|---|---|---|
| 🔴 | **API réelle du BDS** | Connecter l'app aux vrais matchs et résultats du BDS IONIS en temps réel |
| 🔴 | **Authentification sécurisée** | Migration vers Firebase Authentication (connexion Google OAuth2) |
| 🟡 | **Classement multijoueur réel** | Synchronisation des scores entre tous les utilisateurs via un vrai serveur |
| 🟢 | **Déploiement Play Store** | Publication sur le Google Play Store pour tous les étudiants ESME |

---

## Stack Technique

| Couche | Technologies |
|---|---|
| **Langage** | Kotlin 2.2.0 |
| **UI** | Jetpack Compose + Android Views XML + Lottie |
| **Architecture** | MVVM (ViewModel + Kotlin Flow + Coroutines) |
| **Réseau** | Retrofit 2 + OkHttp |
| **Stockage** | SharedPreferences + Gson (JSON) |
| **IA** | Google Gemini 2.0 Flash (SudriBot) |
| **Analytics** | Firebase Analytics |
| **Notifications** | LocalNotificationHelper (Android natif) |
| **Email** | JavaMail / EmailService |
| **Min SDK** | Android 7.0 (API 24) |
| **Target SDK** | Android 15 (API 35) |

---

## Lancer le projet

### Prérequis
- Android Studio Hedgehog ou supérieur
- JDK 17+
- Clé API Google Gemini

### Installation
```bash
git clone https://github.com/Julian33110/sudribet_appli_mobile.git
cd sudribet_appli_mobile
```

Ajouter dans `local.properties` :
```
GEMINI_API_KEY=votre_clé_ici
```

Puis ouvrir le projet dans Android Studio et **Run**.

---

## Équipe

| Nom | Rôle |
|---|---|
| **Julian Expert** | Architecture · Intégration API · Backend · Système de résolution des paris |
| **Malo Greffier** | UX/UI Android · Gamification · Navigation · Profil · Classement |
| **Sacha Lathuillière** | Fonctionnalités Paris · Historique · Notifications · Missions quotidiennes |

---

*Projet réalisé dans le cadre du cursus ingénieur ESME Sudria — CFA SACEF 2025-2026*
