# Yoga App 🧘

Application full-stack de gestion de sessions de yoga, composée d'un back-end **Spring Boot** (Java 21) et d'un front-end **Angular 19**.

---

## Table des matières

1. [Prérequis](#prérequis)
2. [Structure du projet](#structure-du-projet)
3. [Installation et utilisation](#installation-et-utilisation)
   - [Back-end](#back-end)
   - [Front-end](#front-end)
4. [Lancer les tests](#lancer-les-tests)
   - [Tests back-end (JUnit / Spring Boot)](#tests-back-end-junit--spring-boot)
   - [Tests front-end unitaires (Jest)](#tests-front-end-unitaires-jest)
   - [Tests E2E front-end (Cypress)](#tests-e2e-front-end-cypress)
5. [Rapports de couverture](#rapports-de-couverture)
   - [Couverture back-end (JaCoCo)](#couverture-back-end-jacoco)
   - [Couverture front-end Jest](#couverture-front-end-jest)
   - [Couverture front-end Cypress (E2E)](#couverture-front-end-cypress-e2e)
6. [Compte administrateur par défaut](#compte-administrateur-par-défaut)

---

## Prérequis

| Outil | Version minimale |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9+ |
| Node.js | 18+ |
| npm | 9+ |
| Docker & Docker Compose | 24+ |

---

## Structure du projet

```
.
├── back/    # API REST Spring Boot
└── front/   # Application Angular
```

---

## Installation et utilisation

### Back-end

Le back-end utilise **Spring Boot Docker Compose** : au démarrage, la base de données MySQL est automatiquement lancée via Docker.

#### 1. Variables d'environnement

Créez un fichier `.env` dans le dossier `back/` (ou exportez les variables dans votre shell) :

```dotenv
DB_NAME=yoga
DB_USER=yoga_user
DB_PASSWORD=yoga_password
DB_HOST=localhost
DB_PORT=3306
TOKEN_SECRET=your_very_long_jwt_secret_at_least_64_characters_for_hs512_algo
```

> **Note :** `TOKEN_SECRET` doit faire au moins 64 caractères pour l'algorithme HS512.

#### 2. Démarrer l'application

```bash
cd back
mvn spring-boot:run
```

Spring Boot détecte le fichier `compose.yaml` et démarre automatiquement le conteneur MySQL. L'API est ensuite disponible sur **http://localhost:8080**.

#### 3. (Optionnel) Initialiser les données

Un script SQL d'insertion du compte administrateur est fourni :

```bash
# Se connecter à MySQL et exécuter :
back/src/main/resources/sql/insert_user.sql
```

---

### Front-end

#### 1. Installer les dépendances

```bash
cd front
npm install
```

#### 2. Démarrer l'application

```bash
npm start
# ou : ng serve
```

L'application est disponible sur **http://localhost:4200**.

> Le proxy est configuré dans `proxy.conf.json` pour rediriger les appels `/api` vers `http://localhost:8080`.

---

## Lancer les tests

### Tests back-end (JUnit / Spring Boot)

Les tests unitaires et d'intégration utilisent une base **H2 in-memory** (profil `test`). Aucune base de données externe n'est requise.

```bash
cd back

# Lancer tous les tests
mvn test

# Lancer un test spécifique
mvn test -Dtest=NomDeLaClasseTest

# Lancer les tests sans vérification de couverture
mvn test -Djacoco.skip=true
```

---

### Tests front-end unitaires (Jest)

```bash
cd front

# Lancer les tests une fois
npm test
# ou : npx jest

# Lancer les tests en mode watch (rechargement automatique)
npm run test:watch

# Lancer les tests avec collecte de couverture
npx jest --coverage
```

---

### Tests E2E front-end (Cypress)

> ⚠️ Les tests E2E nécessitent que le **back-end** et le **front-end** soient démarrés.

```bash
# Terminal 1 : démarrer le back-end
cd back && mvn spring-boot:run

# Terminal 2 : démarrer le front-end
cd front && npm start

# Terminal 3 : lancer Cypress
cd front

# Mode interactif (interface graphique)
npm run cypress:open

# Mode headless (CI / ligne de commande)
npm run cypress:run
# ou : npm run e2e:ci
```

---

## Rapports de couverture

### Couverture back-end (JaCoCo)

JaCoCo est configuré pour s'exécuter automatiquement lors de la phase `test` de Maven et génère un rapport HTML.

```bash
cd back
mvn test
```

Le rapport est généré dans :

```
back/target/site/jacoco/index.html
```

Ouvrez ce fichier dans votre navigateur pour consulter le rapport de couverture détaillé (par package, classe et méthode).

---

### Couverture front-end Jest

```bash
cd front

# Générer le rapport de couverture
npx jest --coverage
```

Les rapports sont générés dans :

```
front/coverage/jest/
├── lcov-report/index.html   # Rapport HTML interactif
├── lcov.info                # Fichier LCOV (compatible SonarQube, etc.)
└── coverage-final.json      # Données brutes de couverture
```

Ouvrez `front/coverage/jest/lcov-report/index.html` dans votre navigateur.


---

### Couverture front-end Cypress (E2E)

La couverture E2E est collectée via `@cypress/code-coverage` et `nyc` (Istanbul).

#### 1. Lancer les tests E2E avec collecte de couverture

```bash
cd front

npm run e2e:ci
```

#### 2. Générer le rapport de couverture E2E

```bash
cd front
npm run e2e:coverage
```

Cette commande génère les rapports dans :

```
front/coverage/
├── lcov.info                # Rapport LCOV
└── lcov-report/index.html   # Rapport HTML interactif
```

---

## Compte administrateur par défaut

Après l'exécution du script SQL d'initialisation, un compte administrateur est disponible :

| Champ | Valeur |
|---|---|
| Email | `yoga@studio.com` |
| Mot de passe | `test!1234` |

