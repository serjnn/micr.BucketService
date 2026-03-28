# Bucket Service

The **Bucket Service** is a microservice responsible for managing user shopping carts (buckets) in an e-commerce ecosystem. It provides functionality to add, remove, and retrieve products for a specific client, while also supporting transactional consistency through SAGA compensation logic.

## 🚀 Main Job

The primary responsibility of this service is to maintain a persistent state of items that a user (client) has added to their shopping cart. It acts as a bridge between the user's session and the **Product Service**, ensuring that the bucket contains up-to-date product information and handles the restoration of items in case of order failures.

### Key Features:
- **Bucket Management:** Create and maintain shopping buckets for unique clients.
- **Product Integration:** Fetches detailed product information (name, description, price, category) by communicating with the **Product Service**.
- **SAGA Support:** Implements compensation logic (`/restore`) to put items back into the user's bucket if an order process fails.
- **Persistent Storage:** Uses PostgreSQL to store bucket states, with Flyway for database migrations.
- **Service Discovery:** Integrates with Netflix Eureka for dynamic service lookup.

---

## 🌍 Interactions with Outer World

### Inbound (APIs)
The service exposes a RESTful API for both client-facing operations and internal system compensations.
- **Base Path:** `/api/v1/buckets`

### Outbound (Service-to-Service)
The Bucket Service communicates with other microservices:
- **Product Service:**
  - **Endpoint:** `POST http://product/api/v1/products/by-ids`
  - **Purpose:** Retrieves full product details for a list of IDs to enrich the bucket view.  - **Discovery:** Uses Eureka to resolve the `product` service instance.

### SAGA Orchestration
- Participates in SAGA patterns (likely orchestrated by an Order Service).
- Provides a "Compensating Transaction" endpoint (`/restore`) to roll back changes and restore bucket state after a failed checkout.

---

## 🛠 Technology Stack
- **Java 17**
- **Spring Boot 3.3.3**
- **Spring Data JDBC** (PostgreSQL)
- **Flyway** (Database migrations)
- **Spring Cloud Netflix Eureka Client** (Service discovery)
- **SpringDoc OpenAPI (Swagger)** (API documentation)
- **Testcontainers** (Integration testing with PostgreSQL)

---

## 📖 API Documentation

### Bucket Operations

#### `GET /{clientId}`
Retrieves all items in the client's bucket with full product details.
- **Response:** `List<CompleteProductDto>`

#### `POST /{clientId}/products/{productId}`
Adds one unit of a specific product to the client's bucket.
- **Parameters:** `clientId` (Long), `productId` (Long)

#### `DELETE /{clientId}/products/{productId}`
Decrements the quantity of a product or removes it from the bucket if the quantity reaches zero.
- **Parameters:** `clientId` (Long), `productId` (Long)

#### `DELETE /{clientId}`
Clears the entire bucket for the specified client.
- **Parameters:** `clientId` (Long)

### SAGA / Internal Operations

#### `POST /restore`
Restores items to a client's bucket from a previously failed order.
- **Request Body:** `OrderDto` (contains `clientId` and `List<BucketItemRestoredDto>`)

---

## 🚦 Getting Started

### Prerequisites
- Docker (for PostgreSQL or Testcontainers)
- JDK 17
- Maven

### Running the Application
```bash
mvn clean install
mvn spring-boot:run
```

### Swagger UI
Once running, you can explore the API at:
`http://localhost:7001/swagger-ui.html`
