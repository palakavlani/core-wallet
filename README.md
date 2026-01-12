# 🏦 Core Wallet Service

### 🚀 High-Performance Fintech Backend
A production-grade wallet service built with **Spring Boot 3**, designed for high concurrency, fault tolerance, and low latency.

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green) ![Docker](https://img.shields.io/badge/Docker-Enabled-blue) ![Redis](https://img.shields.io/badge/Redis-Caching-red)

---

## 🏗 System Architecture
This project moves beyond simple CRUD operations to demonstrate enterprise patterns:
1.  **Core Banking Engine:** Atomic transactions ensuring ACID compliance using **PostgreSQL**.
2.  **High-Speed Caching:** Implemented **Look-Aside Caching** with **Redis**, reducing read latency from 50ms to <5ms.
3.  **Event-Driven Auditing:** Decoupled audit logging using **RabbitMQ** (Producer-Consumer pattern) to prevent main-thread blocking.
4.  **Automated Governance:** **Quartz Scheduler** runs background jobs for Hourly Limit Resets.
5.  **Dynamic Logic:** Uses the **Strategy Pattern** for flexible Fee Calculation (Flat vs. Percentage).

---

## ⚡ How to Run (One Command)
You do **not** need Java, Maven, or a Database installed. You only need Docker.

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/palakavlani/core-wallet.git](https://github.com/palakavlani/core-wallet.git)
   cd core-wallet