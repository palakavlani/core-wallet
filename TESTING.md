# 📘 Core Wallet Service - Testing & Verification Guide

**Repository:** [https://github.com/palakavlani/core-wallet](https://www.google.com/search?q=https://github.com/palakavlani/core-wallet)

This guide outlines how to spin up the infrastructure, execute the core banking flows (Register -> Deposit -> Transfer), and verify the backend architecture (caching and async messaging).

---

## 🚀 1. Quick Start (Infrastructure)

You do not need Java or Postgres installed. Docker handles the entire environment.

1. **Clone the repository:**
```bash
git clone https://github.com/palakavlani/core-wallet.git
cd core-wallet

```


2. **Start the Application Stack:**
```bash
docker-compose up --build

```


*Wait until you see `Started CoreWalletApplication` in the logs.*

---

## 🧪 2. API Testing Workflow

*Note: The API is secured. You must Register and Login to obtain a JWT Token for wallet operations.*

### Step A: Register Users

Create two users to test money transfers.

**Endpoint:** `POST /api/v1/auth/register`

**User 1 (Sender - Standard):**

```json
{
    "username": "bob",
    "password": "password123",
    "email": "bob@test.com",
    "userType": "STANDARD"
}

```

**User 2 (Receiver - VIP):**

```json
{
    "username": "richie",
    "password": "password123",
    "email": "richie@test.com",
    "userType": "VIP"
}

```

### Step B: Login & Authenticate

**Endpoint:** `POST /api/v1/auth/login`

**Request:**

```json
{
    "username": "bob",
    "password": "password123"
}

```

**Response:** You will receive a `token`. **Copy this token.**
*Use this token in the `Authorization` header (`Bearer <token>`) for all following requests.*

### Step C: Check Initial Balance

**Endpoint:** `GET /api/v1/wallet`

* **Header:** `Authorization: Bearer <your_token>`
* **Expected Result:** Balance should be `0.00` (New wallets start empty).

### Step D: Deposit Funds (Self-Load)

**Endpoint:** `POST /api/v1/wallet/deposit`

* **Header:** `Authorization: Bearer <your_token>`

**Request:**

```json
{
    "amount": 1000.00
}

```

### Step E: Transfer Money (P2P)

Transfer money from "bob" (you) to "richie".
*Note: Self-transfer is blocked by validation logic.*

**Endpoint:** `POST /api/v1/wallet/transfer`

* **Header:** `Authorization: Bearer <your_token>`

**Request:**

```json
{
    "toUsername": "richie",
    "amount": 10.00
}

```

---

## ⚙️ 3. Architecture Verification (The "Under the Hood" Stuff)

This project uses **Redis** for read-scaling and **RabbitMQ** for decoupled auditing. Here is how to prove they are working.

### ✅ Verifying Redis Caching (Performance)

The system caches user wallet data to reduce database load.

1. **Check logs:** Run `docker compose logs -f ledger_app`.
2. **Trigger a Read:** Call `GET /api/v1/wallet` (Check Balance).
* **First Call:** Logs will show `Fetching from Database`.
* **Second Call:** Logs will show `Fetching from Cache`.


3. **Result:** The second call will be significantly faster (verify latency in Postman).

### ✅ Verifying Async Auditing (RabbitMQ)

When a transfer happens, the transaction is committed immediately, but the Audit Log is processed asynchronously to prevent blocking the main thread.

1. **Keep the logs open:** `docker compose logs -f ledger_app`
2. **Perform a Transfer:** (Step E above).
3. **Watch the Logs:** You will see a specific log entry arriving from the Message Queue:
> `[AuditListener] Received transaction audit: User bob transferred 10.00 to richie`


4. **Proof:** This confirms the Producer (Wallet Service) successfully sent the message to RabbitMQ, and the Consumer (Audit Service) picked it up.

---

### 📂 Postman Collection

For convenience, a ready-to-use Postman Collection (`core-wallet.postman_collection.json`) is included in the root of the repository. Import it to skip manual JSON setup.