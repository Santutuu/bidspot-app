<p align="center">
  <img src="Subastas-app/subastas-client/assets/images/banner-logo.png">
</p>

# Description

A mobile auction platform built with React Native and Spring Boot.

The system allows registered users to participate in real-time auctions, submit items for future auctions, and manage all post-auction operations after an item has been awarded.

The project models the complete auction lifecycle, including:

- user registration and verification;
- payment method setup;
- item submission and inspection;
- insurance policy request and review;
- catalog and lot creation;
- concurrent real-time bidding;
- automatic lot closing;
- item award;
- payment confirmation;
- shipping or pickup;
- delivery tracking.

The application was designed around real-world business rules, featuring domain-driven validations, concurrency control, transactional operations, and persistent storage of the main business events.

---

# Screenshots

*Coming soon.*

---

# Architecture

The project is divided into two applications:


## API REST

- Java 17
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- PostgreSQL
- WebSocket
- Maven

The backend is organized into the following layers:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Client

- React Native
- Expo
- Expo Router
- TypeScript
- Axios
- Expo SecureStore

The frontend includes:

- file-based routing with Expo Router;
- authentication context for session management;
- secure JWT storage using Expo SecureStore;
- custom hooks for API communication;
- real-time bid updates;
- persistent registration progress across app sessions.
