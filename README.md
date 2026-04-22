# SudriBet — Application Mobile Android de Paris Sportifs Étudiants

> Projet ESME Sudria 2025-2026  
> **Sacha Lathuillière · Julian Expert · Malo Greffier**  
> ESME / CFA SACEF

---

## Équipe & Répartition

| Membre | Domaine |
|---|---|
| 👤 **Julian Expert** | Architecture · Backend · Intégration API · BetResolver · EmailService |
| 👤 **Malo Greffier** | UX/UI Android · Toutes les Activities · Navigation · Gamification · Classement |
| 👤 **Sacha Lathuillière** | Fonctionnalités Paris · Historique · Missions · Notifications · Badges |

---

## 01 — Le Projet *(Toute l'équipe)*

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

## 02 — Analyse *(Toute l'équipe)*

### Valeur ajoutée
- Fédérer autour du sport · Stimuler la vie du campus · Alternative saine · Accessibilité · Reconnaissance

### Les chiffres *(sondage Microsoft Forms 08/2025 — étudiants ESME)*

| Stat | Valeur |
|---|---|
| Prêts à se déplacer pour assister aux compétitions s'ils étaient informés | **60%** |
| Déjà utilisateurs de sites de paris en ligne | **12%** |
| Utilisent leur smartphone quotidiennement | **100%** |

### Utilisateur cible — Maxime
19 ans · Étudiant en école d'ingénieur · Innovant, sportif, joueur · Veut se sentir intégré et participer aux événements de son école

---

## 03 — Vision & Stratégie Produit *(Toute l'équipe)*

| | |
|---|---|
| **Vision** | Augmenter de **50%** la présence des supporters aux matchs |
| **Target Group** | Étudiants de l'ESME, surtout les premières années |
| **Produit** | App mobile Android de paris fictifs en SudriCoins (monnaie virtuelle) |

**Partenaire clé — BDS IONIS :** Alimenter les données et promouvoir la solution. Objectif : que SudriBet devienne l'outil officiel du BDS.

---

## 04 — Modèle Conceptuel *(Toute l'équipe)*

### Cas d'utilisations
**Étudiant :** Consulter ses jetons, les matchs, le classement, l'historique · Parier · Se connecter / S'inscrire  
**Administrateur BDS :** Renseigner les résultats · Gérer utilisateurs et événements · Générer le classement final

### Architecture 3 couches

```
┌─────────────────────────────────────────────────────────────────┐
│  COUCHE PRÉSENTATION — ACTIVITIES + ADAPTERS        👤 Malo     │
│  LoginActivity  HomeActivity  MainActivity  HistoryActivity     │
│  ProfilActivity  ChatActivity  LeaderboardActivity              │
│  MatchDetailActivity                                            │
│  → Jetpack Compose · Android Views XML · Lottie Animations     │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  COUCHE MÉTIER — MANAGERS + VIEWMODEL              👤 Sacha     │
│  MatchViewModel  BetResolver  DailyManager  BadgeSystem         │
│  ActivityTransitions  EmailService  LocalNotificationHelper     │
│  → Kotlin 2.2.0 · Coroutines · Kotlin Flow · MVVM              │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│  COUCHE DONNÉES & SERVICES EXTERNES             👤 Julian       │
│  SharedPreferences (JSON)  Firebase Analytics                   │
│  Gemini API (SudriBot)  Retrofit + OkHttp  Gson                 │
│  → Stockage 100% local · Android 7.0+ (API 24) → Android 15    │
└─────────────────────────────────────────────────────────────────┘
```

---

## 05 — Méthode de Développement *(Toute l'équipe)*

**Méthodologie Agile (Scrum)** — sprints, daily standups, reviews  
**But :** livrer un MVP fonctionnel le plus vite possible

| Outil | Usage |
|---|---|
| Notion | Gestion de projet, backlog |
| Microsoft Teams | Communication équipe |
| Postman | Tests API |
| GitHub | Versioning |
| Android Studio | Développement |

---

## 06 — Story Mapping *(Toute l'équipe)*

| Découvrir l'app | Se Login | Parier |
|---|---|---|
| Sensibilisé par le BDS | Accéder à la page d'accueil | Vision globale des matchs |
| Sensibilisé via réseaux sociaux | Créer un compte personnel | Enregistrer des paris |
| | | Visionner son historique |
| | | Accéder au classement |

---

## 07 — Implémentation

### UI / Activities *(👤 Malo)*
- `LoginActivity` — écran de connexion / inscription
- `HomeActivity` — dashboard avec SudriCoins et missions quotidiennes
- `MainActivity` — liste des matchs (Jetpack Compose)
- `ProfilActivity` — profil avec badges et statistiques
- `LeaderboardActivity` — classement général
- `HistoryActivity` — historique des paris
- `ChatActivity` — SudriBot (IA intégrée)
- `MatchDetailActivity` — détail d'un match + simulateur de score

### Logique Métier *(👤 Sacha)*
- `MatchViewModel` — gestion des données matchs (Kotlin Flow + Coroutines)
- `BetResolver` — résolution automatique des paris (Gagné / Perdu)
- `DailyManager` — missions et bonus quotidiens
- `BadgeSystem` — attribution des 10 types de badges
- `LocalNotificationHelper` — notifications locales Android
- `ActivityTransitions` — navigation entre écrans

### Services Externes & Données *(👤 Julian)*
- `EmailService` — envoi d'email de confirmation à l'inscription
- `SharedPreferences (JSON)` — persistance locale via Gson
- `Firebase Analytics` — suivi des événements
- `Gemini API (SudriBot)` — assistant IA Gemini 2.0 Flash
- `Retrofit + OkHttp` — communication réseau

---

## 08 — Épopées Fonctionnelles

### Inscription et Connexion *(👤 Malo · Julian)*
- `Malo` — design de l'écran login et du profil
- `Julian` — logique d'inscription, EmailService, activation de compte par email

### Placer des Paris *(👤 Sacha · Malo)*
- `Sacha` — logique de mise, BetResolver, historique des paris, DailyManager
- `Malo` — interface de pari, filtrage par sport, affichage des cotes en temps réel

### Administration / Gestion *(👤 Julian)*
- Les administrateurs BDS valident les paris, ajoutent et modifient les matchs
- Export des résultats et données utilisateurs

---

## 09 — Modèle de Données *(👤 Julian · Sacha)*

### `data class Bet` *(Sacha)*
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

### `data class Match` *(Julian)*
```kotlin
data class Match(
    val id        : String,
    val equipeA   : String,
    val equipeB   : String,
    val coteA     : Double,
    val coteB     : Double,
    val heure     : String,           // "HH:MM"
    val categorie : String,           // "Football"|"Rugby"|"Basket"|"Handball"|"Volley"
    val isLive    : Boolean = false,
    val scoreA    : Int     = 0,
    val scoreB    : Int     = 0,
    val coteNul   : Double? = null    // nul disponible uniquement au football
)
```

**Persistance *(Julian) :*** 100% local via `SharedPreferences` · JSON (Gson) · Pas de BDD externe · Android 7.0+ (API 24)

---

## 10 — Tests *(👤 Sacha · Julian)*

Testé sur **Xiaomi 24094RAD4G — Android 15.0 (API 35, arm64-v8a)** — Build successful, **0 erreur**

```
POST /api/login  →  200 OK · 43ms · 513B
{ "success": true, "user": { "email": "...", "emailConfirmed": true } }
```

---

## 11 — Difficultés Rencontrées

| Difficulté | Problème | Solution | Porteur |
|---|---|---|---|
| **Compatibilité Android** | Comportements différents selon la version du téléphone | Détection de la version au lancement | 👤 Malo |
| **XML + Jetpack Compose** | Deux systèmes UI qui ne communiquent pas naturellement | `ComposeView` comme pont | 👤 Malo |
| **Sauvegarde locale** | Le téléphone ne stocke que du texte, pas des objets | Sérialisation JSON via Gson | 👤 Sacha |
| **Clé API Gemini** | Ne doit jamais être visible dans le code source | `local.properties` ignoré par Git, injectée à la compilation | 👤 Julian |

---

## 12 — Implémentations Futures

| Priorité | Fonctionnalité | Porteur |
|---|---|---|
| 🔴 | **API réelle du BDS** — matchs et résultats en temps réel | Julian |
| 🔴 | **Authentification Firebase** — connexion Google OAuth2 | Julian |
| 🟡 | **Classement multijoueur réel** — synchronisation via serveur | Malo |
| 🟢 | **Déploiement Google Play Store** | Toute l'équipe |

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
| **Min SDK** | Android 7.0 (API 24) |
| **Target SDK** | Android 15 (API 35) |

---

## Lancer le projet

```bash
git clone https://github.com/Julian33110/sudribet_appli_mobile.git
cd sudribet_appli_mobile
```

Ajouter dans `local.properties` :
```
GEMINI_API_KEY=votre_clé_ici
```

Ouvrir dans Android Studio → **Run**

---

*Projet réalisé dans le cadre du cursus ingénieur ESME Sudria — CFA SACEF 2025-2026*
