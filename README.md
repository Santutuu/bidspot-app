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

# Screenshots

## 🏠 Home & Auction Catalog

<p align="center">
  <img src="https://github.com/user-attachments/assets/90dd9b62-b5f7-4b50-b8fa-f8afb7c4889c" height="540" alt="BidSpot home screen"/>
  <img src="https://github.com/user-attachments/assets/f3ac4c54-796c-45be-87ea-41bacafd5c61" height="540" alt="Auction catalog"/>
</p>

The home screen provides access to auction categories and recommended auctions. Users can also open scheduled auctions and browse their complete lot catalogs.

---

## 📂 Browse by Category

<p align="center">
  <img src="https://github.com/user-attachments/assets/c89315c6-59c6-4432-8282-81ce7db865e9" height="540" alt="Auctions filtered by category"/>
</p>

Users can browse live and scheduled auctions filtered by product category.

---

## 📝 Publish an Item

<p align="center">
  <img src="https://github.com/user-attachments/assets/6a8827e6-b6d0-4a39-a690-4cae19ef1998" height="540" alt="Seller payout account"/>
  <img src="https://github.com/user-attachments/assets/ca16e010-aaed-4a31-9e08-efe078f5f060" height="540" alt="Product submission form"/>
</p>

Sellers select the payout account where auction proceeds will be deposited and complete the product submission form with images, title, description, and ownership confirmation.

---

## 🔔 Auction Notifications

<p align="center">
  <img src="https://github.com/user-attachments/assets/7d232b9d-2a44-4c7e-b5c8-b737752d58f0" height="540" alt="Auction award notification"/>
</p>

Users receive notifications when they win an auction or when an action is required to continue the purchase process.

---

## 📦 Seller Item Tracking

<p align="center">
  <img src="https://github.com/user-attachments/assets/5198aad7-4efb-4aae-afa5-72231d6b0a26" height="540" alt="Seller item tracking"/>
</p>

Sellers can track submitted items, review their current status, check inspection instructions, and see any pending actions.

---

## 💎 Auction Overview

<p align="center">
  <img src="https://github.com/user-attachments/assets/470d9176-06cd-46a7-843e-222c6be1d887" height="540" alt="Live auction overview"/>
</p>

The auction overview displays its status, date, location, category, auctioneer, currency, and an image gallery for the current lot.

---

## 🔎 Lot Details

<p align="center">
  <img src="https://github.com/user-attachments/assets/259aad36-6425-4c4a-81df-152daae1d220" height="540" alt="Auction lot details"/>
</p>

Each lot includes a complete image gallery, product description, current price, bidding status, and the form used to enter a new bid.

---

## 💰 Live Bidding

<p align="center">
  <img src="https://github.com/user-attachments/assets/3ab38731-2e9c-47da-b906-ec9c366d674f" height="540" alt="Bid form"/>
  <img src="https://github.com/user-attachments/assets/e8781147-f8dd-4123-8d33-c7b2f6ec7c50" height="540" alt="Bid confirmation"/>
  <img src="https://github.com/user-attachments/assets/75d3f219-ebdc-44dd-8dd4-3bbb20f93fab" height="540" alt="Upcoming auction lots"/>
</p>

Users can enter a bid, review it through a confirmation dialog before submission, and preview the upcoming lots in the live auction.

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
