# 🎬 CineLog

CineLog is a [Letterboxd](https://letterboxd.com)-inspired movie logging application. It lets people discover movies, keep a personal diary of what they've watched, maintain a watchlist, and manage a simple public profile — all backed by [The Movie Database (TMDB)](https://www.themoviedb.org/) for movie data.

The project is a full-stack application with a **Spring Boot** REST API and an **Angular** single-page frontend.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Architecture Notes](#architecture-notes)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Environment Variables](#environment-variables)
- [API Overview](#api-overview)
---

## Features

- **Authentication** — registration, login, stateless JWT access tokens, refresh token rotation, and logout.
- **Discover** — browse and search movies sourced live from TMDB, with infinite scroll.
- **Movie Details** — backdrop, poster, overview, release date, plus actions to log the movie or add/remove it from your watchlist.
- **Diary** — log movies you've watched with a date, a 1–5 star rating, and an optional review; view and delete past entries.
- **Watchlist** — save movies to watch later and remove them once you're done.
- **Profile** — view your public profile (avatar, username, bio, member-since date) and edit your bio/avatar URL.

## Tech Stack

**Backend**
- Java 21 · Spring Boot 4.1
- Spring Data JPA · PostgreSQL
- Spring Security · JJWT (JWT access + refresh tokens)
- MapStruct · Lombok
- Spring Cache + Caffeine (caches TMDB responses)
- Maven

**Frontend**
- Angular 21 — standalone components, no NgModules
- Signals for application state
- Functional HTTP interceptors and route guards
- RxJS · TypeScript (strict mode)
- Vitest for unit tests

**External API**
- [TMDB API](https://developer.themoviedb.org/docs) for movie discovery, search, and details

## Project Structure

```text
CineLog/
├── backend/                      Spring Boot REST API
│   └── src/main/java/com/cinelog/
│       ├── config/                Security, CORS, caching, password encoding, RestClient config
│       ├── controller/            REST controllers
│       ├── service/                Business logic
│       ├── repository/            Spring Data JPA repositories
│       ├── entity/                  JPA entities
│       ├── enums/                  Role enum
│       ├── dto/                     Request/response DTOs (+ dto/tmdb for TMDB payloads)
│       ├── mapper/                MapStruct mappers
│       ├── security/               JWT service, filter, user principal/details
│       ├── integration/            TMDB HTTP client
│       └── exception/              Custom exceptions + global exception handler
│
└── frontend/                     Angular SPA
    └── src/app/
        ├── core/                    guards, interceptors, models, app-wide services (auth, tokens)
        ├── shared/                 reusable UI components, directives
        ├── layout/                 main layout + navbar (wraps authenticated pages)
        └── features/               auth, discover, movies, diary, watchlist, profile
```

## Architecture Notes

- **Layered backend** — Controller → Service → Repository, with MapStruct handling entity ↔ DTO mapping and a single `@RestControllerAdvice` translating domain exceptions into consistent JSON error responses.
- **TMDB is the source of truth for discovery.** The local `movies` table is *not* a mirror of TMDB — a movie is only persisted locally the first time a user interacts with it (logging it or adding it to a watchlist), and its cached metadata is refreshed if it's more than 7 days old. Search and Discover never write to the database.
- **Caching** — TMDB responses are cached with Caffeine (`movieDetails`: 24h/5000 entries, `movieSearch`: 1h/1000 entries) to reduce external API calls; the database itself is not part of the cache.
- **Stateless JWT auth** — short-lived access tokens (15 min) plus longer-lived refresh tokens (7 days) stored server-side and rotated on every refresh. Private endpoints read the authenticated user from the JWT (via `@AuthenticationPrincipal`) rather than trusting a client-supplied user id.
- **Frontend state** — Angular Signals drive auth state and feature state (diary entries, watchlist items, loading flags), with a functional interceptor pair handling Bearer token attachment and transparent access-token refresh on `401` responses (single in-flight refresh shared across concurrent requests).

## Getting Started

### Prerequisites

- [JDK 21](https://adoptium.net/)
- [PostgreSQL](https://www.postgresql.org/) (14+)
- [Node.js](https://nodejs.org/) 20+ and npm
- A free [TMDB API key](https://www.themoviedb.org/settings/api)

Maven doesn't need to be installed separately — the backend ships with the Maven Wrapper (`mvnw` / `mvnw.cmd`).

### Backend Setup

1. Create a PostgreSQL database for the project.
2. From `backend/`, provide the required configuration either as environment variables or by editing `src/main/resources/application.properties` directly for local development (see [Environment Variables](#environment-variables) below).
3. Run the application:

   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

   On Windows: `mvnw.cmd spring-boot:run`

4. The API starts on **http://localhost:8080**. Schema is created/updated automatically via `spring.jpa.hibernate.ddl-auto=update` — no manual migrations needed.

> The app fails fast with a clear error if required configuration (database credentials, TMDB key, JWT secret) is missing, rather than starting in a broken state.

### Frontend Setup

1. Install dependencies:

   ```bash
   cd frontend
   npm install
   ```

2. Confirm `src/environments/environment.ts` points at your running backend (defaults to `http://localhost:8080/api/v1`).
3. Start the dev server:

   ```bash
   npm start
   ```

4. The app is served at **http://localhost:4200**.

> The backend's CORS policy is configured for `http://localhost:4200` by default. If you serve the frontend from a different origin, update `corsConfigurationSource()` in `backend/.../config/SecurityConfig.java` to match.

## Environment Variables

The backend reads the following from the environment (see `application.properties` for how each is used):

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL for PostgreSQL, e.g. `jdbc:postgresql://localhost:5432/cinelog` |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `TMDB_API_KEY` | API key from The Movie Database |
| `JWT_SECRET_KEY` | Secret used to sign JWTs — must be a Base64 string decoding to **at least 256 bits** for HS256 |

## API Overview

All endpoints are prefixed with `/api/v1`. Endpoints marked 🔒 require a `Authorization: Bearer <accessToken>` header.

**Auth** — `/auth`
| Method | Path | Description |
|---|---|---|
| POST | `/auth/register` | Create a new account |
| POST | `/auth/login` | Exchange credentials for an access + refresh token |
| POST | `/auth/refresh` | Rotate a refresh token for a new token pair |
| POST | `/auth/logout` | 🔒 Invalidate a refresh token |

**Users** — `/users`
| Method | Path | Description |
|---|---|---|
| GET | `/users/me` | 🔒 Get the authenticated user's profile |
| GET | `/users/{userId}` | Get a user's public profile |
| PUT | `/users/profile` | 🔒 Update your bio / avatar URL |

**Movies** — `/movies`
| Method | Path | Description |
|---|---|---|
| GET | `/movies/{tmdbId}` | Get movie details (syncs to the local DB) |
| GET | `/movies/search?query&page` | Search TMDB by title |
| GET | `/movies/discover?page` | Browse movies (pagination-ready, filter-ready) |

**Diary** — `/diary`
| Method | Path | Description |
|---|---|---|
| GET | `/users/{userId}/diary` | View a user's diary |
| POST | `/diary` | 🔒 Log a movie you've watched |
| DELETE | `/diary/{entryId}` | 🔒 Delete a diary entry you own |

**Watchlist** — `/watchlist`
| Method | Path | Description |
|---|---|---|
| GET | `/users/{userId}/watchlist` | View a user's watchlist |
| POST | `/watchlist` | 🔒 Add a movie to your watchlist |
| DELETE | `/watchlist/{movieId}` | 🔒 Remove a movie from your watchlist |
