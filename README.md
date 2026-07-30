<p align="center">
  <img src="Subastas-app/subastas-client/src/assets/images/logo-minimalista.png" alt="BidSpot Banner" width="300">
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

## 📱 Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/90dd9b62-b5f7-4b50-b8fa-f8afb7c4889c" width="220" />
  <img src="https://github.com/user-attachments/assets/f3ac4c54-796c-45be-87ea-41bacafd5c61" width="220" />
  
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/6a8827e6-b6d0-4a39-a690-4cae19ef1998" width="220" />
  <img src="https://github.com/user-attachments/assets/ca16e010-aaed-4a31-9e08-efe078f5f060" width="220" />
  <img src="https://github.com/user-attachments/assets/7d232b9d-2a44-4c7e-b5c8-b737752d58f0" width="220" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/5198aad7-4efb-4aae-afa5-72231d6b0a26" width="220" />
  <img src="https://github.com/user-attachments/assets/470d9176-06cd-46a7-843e-222c6be1d887" width="220" />
  <img src="https://github.com/user-attachments/assets/259aad36-6425-4c4a-81df-152daae1d220" width="220" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/3ab38731-2e9c-47da-b906-ec9c366d674f" width="220" />
  <img src="https://github.com/user-attachments/assets/e8781147-f8dd-4123-8d33-c7b2f6ec7c50" width="220" />
  <img src="https://github.com/user-attachments/assets/75d3f219-ebdc-44dd-8dd4-3bbb20f93fab" width="220" />
</p>

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
