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
```

Getting Started
Prerequisites
JDK 23 (or compatible version 21+)

Maven

Installation and Execution
Clone the repository:
git clone [https://github.com/simokostovcik-hash/projekt_sklad.git](https://github.com/simokostovcik-hash/projekt_sklad.git)

Build and run the application:
mvn spring-boot:run

Access the application at: http://localhost:8080
Role      Username   Password
Admin     admin      admin123
User      user       user123

Data Generation
To populate the database with sample data for testing purposes, log in as an Admin and navigate to the Generate link. This executes the generation logic within the StorageController, which parses predefined entries from the data.json resource into the H2 database.

Created as a professional storage management solution.
