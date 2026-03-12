# Coffee Storage and Inventory System

A Spring Boot web application designed for coffee warehouse management. This system allows tracking coffee stock levels, managing partner roasteries, and provides a secure interface for different user roles.

## Key Features

* **Inventory Tracking**: Management of coffee varieties, weight, pricing, and stock status (In Stock, Out of Stock, Ordered).
* **Roastery Management**: Dedicated module to manage roastery details including country of origin and contact information.
* **Role-Based Access Control**:
    * **ADMIN**: Full authority to create, update, and delete inventory and roasteries.
    * **USER**: Read-only access to browse inventory and apply filters.
* **Advanced Filtering and Sorting**: JavaScript-powered dynamic filtering and multi-column table sorting.
* **Audit Logging**: Backend logging system for administrative actions to ensure accountability.
* **Data Seeding**: Functionality to populate the database from JSON sources via a dedicated generation endpoint.

## Tech Stack

* **Backend**: Java 23, Spring Boot 3.2.2, Spring Security
* **Persistence**: Spring Data JPA, Hibernate, H2 Database (In-Memory)
* **Frontend**: Thymeleaf, Bootstrap 5, JavaScript (ES6+)
* **Build Tool**: Maven

## Project Structure

```text
src/main/java/cz/project_storage/
├── controller/    # Web Request Handlers
├── model/         # JPA Entities (Coffee, Roastery, User, AuditLog)
├── repository/    # Data Access Layer
├── service/       # Business Logic Layer
└── security/      # Security Configuration
