# Kafka Microservices Playground 🚀

A simple multi-module Spring Boot microservices system using **Apache Kafka** for event-driven communication.

Services communicate through the `orders` topic — place an order → process payment → reserve inventory → notify.

---

## 🧱 Architecture

```
order-service  --->  payment-service  --->  inventory-service  --->  notification-service
        \____________________ Kafka (orders topic) ____________________/
```

Each service is independently runnable and communicates asynchronously via Kafka.

---

## ⚙️ Prerequisites

Make sure you have installed:

* Java 17
* Docker
* Maven Wrapper (already included)

---

## 🐳 Start Kafka & Infrastructure

```sh
docker compose up -d
```

This will start:

* Kafka
* Zookeeper (if used)
* Required network

Wait ~10–15 seconds before starting services.

---

## ▶️ Run Individual Services

Run each microservice separately from project root.

```sh
.\mvnw -f order-service\pom.xml spring-boot:run
.\mvnw -f payment-service\pom.xml spring-boot:run
.\mvnw -f inventory-service\pom.xml spring-boot:run
```

## Authentication

Order Service uses **Auth0 JWT Authentication**.  
Every API request (except health check) **must include** a valid Bearer token in the header.

```
Authorization: Bearer YOUR_ACCESS_TOKEN
```

---

## Step 1 — Get Access Token from Auth0

Open this URL in your browser. Auth0 login page will appear.

```
https://dev-zcsjaanrqb4cgifd.eu.auth0.com/authorize?client_id=YOUR_CLIENT_ID&redirect_uri=YOUR_CALLBACK_URL&response_type=code&scope=openid%20profile%20email&audience=https://order-service/api
```

> ⚠️ `audience=https://order-service/api` is **mandatory**.  
> Without it you will receive an encrypted token that will be rejected.

---

## Step 2 — Exchange Code for Token

After login, Auth0 redirects to your callback URL with a `?code=xxxx` parameter.  
Exchange it for an access token:

```bash
curl -X POST https://dev-zcsjaanrqb4cgifd.eu.auth0.com/oauth/token \
  -H "Content-Type: application/json" \
  -d '{
    "grant_type": "authorization_code",
    "client_id": "YOUR_CLIENT_ID",
    "code": "CODE_FROM_CALLBACK",
    "redirect_uri": "YOUR_CALLBACK_URL",
    "code_verifier": "YOUR_CODE_VERIFIER"
  }'
```

**Windows cmd:**
```bash
curl -X POST https://dev-zcsjaanrqb4cgifd.eu.auth0.com/oauth/token ^
  -H "Content-Type: application/json" ^
  -d "{\"grant_type\":\"authorization_code\",\"client_id\":\"YOUR_CLIENT_ID\",\"code\":\"CODE_FROM_CALLBACK\",\"redirect_uri\":\"YOUR_CALLBACK_URL\",\"code_verifier\":\"YOUR_CODE_VERIFIER\"}"
```

**Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6ImF0K2p3dCJ9...",
  "token_type": "Bearer",
  "expires_in": 86400
}
```

> Copy `access_token` — use this in all API requests below.

---

## Auth0 Details

| Property | Value |
|---|---|
| Domain | `dev-zcsjaanrqb4cgifd.eu.auth0.com` |
| Audience | `https://order-service/api` |
| Scope | `openid profile email` |
| Token Type | `Bearer` |
| Token Expiry | 24 hours |

---

## API Endpoints

---

### Health Check

Authentication required.

```bash
curl -X GET http://localhost:8080/orders \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Windows cmd:**
```bash
curl -X GET http://localhost:8080/orders ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response — 200 OK:**
```
Service is running
```

---

### Place Order

Authentication required.

```bash
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "product": "Laptop",
    "quantity": 1,
    "price": 999.99
  }'
```

**Windows cmd:**
```bash
curl -X POST http://localhost:8080/orders ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" ^
  -H "Content-Type: application/json" ^
  -d "{\"product\":\"Laptop\",\"quantity\":1,\"price\":999.99}"
```

**Request Body:**

| Field | Type | Required | Description |
|---|---|---|---|
| product | String | ✅ | Product name |
| quantity | Integer | ✅ | Number of items |
| price | Double | ✅ | Price per unit |

**Response — 200 OK:**
```
Order placed for: user@example.com
```

---

## Error Responses

| Status | Reason | Fix |
|---|---|---|
| `401 Unauthorized` | No token in request | Add `Authorization: Bearer <token>` header |
| `401 invalid_token` | Token expired | Get new token from Auth0 |
| `401 invalid_token audience` | Wrong or missing audience | Make sure `audience=https://order-service/api` is in Auth URL |
| `401 at+jwt not allowed` | Wrong token type | Make sure you are using access token not ID token |

---

## Important Notes

```
1. Always include audience in Auth URL
   → audience=https://order-service/api

2. Use access_token NOT id_token
   → Only access_token works with this API

3. Token format must be RS256
   → If token has 5 parts it is encrypted and will be rejected

4. Never send client_secret in frontend code
   → Use PKCE flow without client secret
```

---

## Verify Your Token

Before calling the API, verify your token at **https://jwt.io**

Your token payload should contain:

```json
{
  "https://order-service/email": "your@email.com",
  "aud": "https://order-service/api",
  "iss": "https://dev-zcsjaanrqb4cgifd.eu.auth0.com/"
}
```

If `aud` is missing or different → token will be rejected with 401.

---