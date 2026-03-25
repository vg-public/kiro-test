# ShopWire API — Spring Boot Back-End

REST API for the ShopWire eCommerce platform. Richardson Maturity Level 2, Java 17, Spring Boot 3.x, PostgreSQL.

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17 (set `JAVA_HOME`) |
| Maven | 3.9+ |
| PostgreSQL | 14+ |

## Environment Variables

```bash
export POSTGRES_PASSWORD=your_db_password
export JWT_SECRET=your_256bit_secret_key_at_least_32_chars
```

## Database Setup

Run the DDL from the project root:

```bash
psql -U myuser -d mydb -f work/schema.sql
psql -U myuser -d mydb -f work/schema_dml.sql   # optional seed data
```

## Running the Application

```bash
cd back-end
./mvnw spring-boot:run
```

The API starts on **http://localhost:4000/v1**.

## Running Tests

```bash
./mvnw test
```

Coverage report is generated at `target/site/jacoco/index.html`. The build enforces ≥ 80% line coverage.

## Building a JAR

```bash
./mvnw package -DskipTests
java -jar target/shopwire-api-1.0.0.jar
```

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /v1/auth/register | — | Register new user |
| POST | /v1/auth/login | — | Login |
| POST | /v1/auth/refresh | cookie | Refresh access token |
| POST | /v1/auth/logout | Bearer | Logout |
| GET | /v1/auth/verify-email?token= | — | Verify email |
| POST | /v1/auth/forgot-password | — | Request password reset |
| POST | /v1/auth/reset-password | — | Reset password |
| GET | /v1/users/me | Bearer | Get profile |
| PATCH | /v1/users/me | Bearer | Update profile |
| GET | /v1/users/me/addresses | Bearer | List addresses |
| POST | /v1/users/me/addresses | Bearer | Add address |
| PUT | /v1/users/me/addresses/{id} | Bearer | Update address |
| DELETE | /v1/users/me/addresses/{id} | Bearer | Delete address |
| GET | /v1/categories | — | Category tree |
| GET | /v1/products | — | List/filter products |
| GET | /v1/products/{id} | — | Product detail |
| GET | /v1/products/{id}/reviews | — | Product reviews |
| POST | /v1/products/{id}/reviews | Bearer | Submit review |
| GET | /v1/search?q= | — | Full-text search |
| GET | /v1/search/suggestions?q= | — | Autocomplete |
| GET | /v1/cart | Bearer | Get cart |
| POST | /v1/cart | Bearer | Add item to cart |
| PATCH | /v1/cart/items/{id} | Bearer | Update item quantity |
| DELETE | /v1/cart/items/{id} | Bearer | Remove item |
| POST | /v1/cart/merge | Bearer | Merge guest cart |
| GET | /v1/orders | Bearer | List orders |
| POST | /v1/orders | Bearer | Place order |
| GET | /v1/orders/{id} | Bearer | Order detail |
| POST | /v1/orders/{id}/cancel | Bearer | Cancel order |
| POST | /v1/coupons/validate | Bearer | Validate coupon |
| GET | /v1/wishlist | Bearer | Get wishlist |
| POST | /v1/wishlist | Bearer | Add to wishlist |
| DELETE | /v1/wishlist/{variantId} | Bearer | Remove from wishlist |

## Authentication

All protected endpoints require an `Authorization: Bearer <access_token>` header.

Access tokens expire in 15 minutes (900 seconds). Use `POST /v1/auth/refresh` with the `refresh_token` httpOnly cookie to get a new access token.

## Error Format

All errors follow the API contract schema:

```json
{
  "code": "NOT_FOUND",
  "message": "Product not found"
}
```

## Project Structure

```
src/main/java/dev/shopwire/api/
├── ShopWireApplication.java
├── config/          SecurityConfig, JwtConfig
├── controller/      REST controllers (one per feature group)
├── dto/             Request/Response records
├── entity/          JPA entities (SW_ table mapping)
├── exception/       GlobalExceptionHandler, ApiException
├── repository/      Spring Data JPA repositories
├── security/        JwtAuthFilter, ShopWireUserDetailsService, SecurityUtils
└── service/         Business logic services + DtoMapper
```
