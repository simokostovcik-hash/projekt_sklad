# Coffee Shop – Inventory Management & E-Commerce System

A full-stack coffee inventory management and e-commerce application built with **Spring Boot, React, and MySQL**.

The project combines warehouse management with a customer-facing online coffee shop. Administrators can manage coffee products, stock levels, roasteries, users, and orders, while customers can browse products, add them to a shopping cart, and place orders through the React frontend.

## Key Features

### Coffee Shop – React Frontend

- **Responsive E-Commerce Interface** – Online coffee shop built with React and Vite.
- **Coffee Product Catalog** – Browse coffee products with descriptions, prices, and images.
- **Shopping Cart** – Add products, adjust quantities, and review orders before checkout.
- **Customer Checkout** – Submit orders with customer and delivery information.
- **REST API Integration** – Communicate with the Spring Boot backend using HTTP requests.

### Administration & Inventory Management

- **Coffee Inventory** – Manage coffee varieties, prices, stock quantities, and availability.
- **Shop Visibility** – Control which coffee products appear in the online store.
- **Roastery Management** – Manage coffee roasteries.
- **Order Management** – View customer orders, purchased items, totals, and order statuses.
- **Customer Information** – Display customer names from submitted orders.
- **PDF Invoices** – Generate PDF documents for orders.
- **Role-Based Access Control** – Authentication and authorization using Spring Security.
- **Admin Dashboard** – Overview of inventory and business information.
- **Coffee Tags** – Categorize coffee products using tags.
- **Audit Logging** – Record administrative activities.

## Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java, Spring Boot, Spring Security |
| REST API | Spring Web |
| Database | MySQL |
| Persistence | Spring Data JPA, Hibernate |
| Customer Frontend | React, Vite, JavaScript |
| Admin Frontend | Thymeleaf, HTML, CSS, Bootstrap |
| Build Tools | Maven, npm |
| Database Administration | MySQL Workbench |

## Project Structure

```text
projekt_sklad/
├── frontend/                         # React customer e-shop
│   ├── public/
│   ├── src/
│   │   ├── api/                      # Backend API communication
│   │   ├── assets/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
│
├── mysql/
│   ├── 01_sjednotit_coffee.sql       # Coffee table consolidation
│   └── 02_volitelne_odstraneni_archivu.sql
│
├── src/
│   └── main/
│       ├── java/cz/project_storage/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       │
│       └── resources/
│           ├── templates/           # Thymeleaf administration
│           ├── static/              # CSS and static resources
│           ├── application.properties
│           └── application-mysql.properties
│
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

Before running the application, install:

- **Java JDK 17 or newer** (the current Maven build targets Java 17).
- **MySQL Server** and optionally MySQL Workbench.
- **Node.js and npm**.
- **IntelliJ IDEA** or another Java IDE.
- **Git**.

Maven can be run through IntelliJ IDEA's integrated Maven tool window.

### 1. Clone the Repository

```bash
git clone https://github.com/simokostovcik-hash/projekt_sklad.git
cd projekt_sklad
```

### 2. Configure MySQL

The application uses MySQL for persistent storage.

The development database used during the migration is:

```text
projekt_sklad_migrace
```

Review the database connection settings in:

```text
src/main/resources/application.properties
src/main/resources/application-mysql.properties
```

Configure the database URL, username, and password for your local MySQL installation.

The repository also includes SQL scripts in `mysql/` for database consolidation. These scripts document schema changes and should not be executed blindly against an existing database.

**Important:** The repository does not automatically provide a populated MySQL database. A compatible schema and required data must be available before starting the application.

### 3. Start the Spring Boot Backend

Open the project in IntelliJ IDEA and run the Spring Boot application.

Alternatively, if Maven is installed and available in your terminal:

```bash
mvn spring-boot:run
```

Backend URL:

**http://localhost:8080**

The backend provides the administration interface, database access, and REST API for the React frontend.

### 4. Start the React Frontend

Open a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Customer e-shop URL:

**http://localhost:5173**

Both applications must be running for the complete local e-commerce workflow.

## Application Interfaces

| Interface | URL | Description |
|---|---|---|
| React Coffee Shop | http://localhost:5173 | Customer-facing online shop |
| Spring Boot Backend | http://localhost:8080 | Backend application and REST API |
| Inventory Management | http://localhost:8080/coffee/all | Coffee inventory administration |
| Order Management | http://localhost:8080/orders/all | Order history and administration |
| Admin Dashboard | http://localhost:8080/dashboard | Inventory and business overview |

Administrative pages require appropriate authentication and permissions.

## REST API

The application includes Spring Boot controllers for communication between the frontend and backend.

Important components:

- `CoffeeApiController` – Provides coffee product data to the React frontend.
- `ShopOrderApiController` – Handles customer orders submitted from the e-shop.
- `StorageController` – Handles inventory, order administration, and other server-rendered pages.

The React frontend communicates with the backend through HTTP requests.

## Database Architecture

The application uses **MySQL**, **Spring Data JPA**, and **Hibernate**.

### Unified Coffee Entity

The inventory and e-commerce catalog share a single `Coffee` entity mapped to the `coffee` table.

This avoids maintaining separate coffee product records for the warehouse and online shop.

Important coffee fields include:

- Product name, type, price, and stock quantity.
- Description and image URL.
- Roast date.
- Shop visibility (`VISIBLE_IN_SHOP`).
- Roastery and tag relationships.

### Orders

Customer orders are stored in the `orders` table, with purchased products recorded in `order_item`.

The application supports:

- Customer names and contact details.
- Delivery information.
- Order status and total amount.
- Multiple purchased items.
- References to coffee products.
- Stored purchase prices for order items.

Historical order information should be preserved when coffee product prices or visibility change.

### Database Migration Scripts

The `mysql/` directory contains:

| Script | Purpose |
|---|---|
| `01_sjednotit_coffee.sql` | Consolidation of coffee data and related database structures |
| `02_volitelne_odstraneni_archivu.sql` | Optional cleanup of archived database structures |

**Do not rerun migration or cleanup scripts on an existing database without reviewing their contents and current schema.**

Hibernate schema validation is used in the development configuration, so the database schema must match the Java entity mappings.

## Security

Spring Security provides authentication and role-based access control.

Administrative features are restricted according to user permissions. Customer-facing ordering is handled through the shop's REST API.

Database passwords, API keys, and other credentials should not be committed to GitHub. Use appropriate environment-specific configuration for deployment.

## Development

### Backend Build

In IntelliJ IDEA:

```text
Maven → projekt_sklad → Lifecycle → compile
```

Alternatively:

```bash
mvn compile
```

### Run Backend

```bash
mvn spring-boot:run
```

### Run Frontend

```bash
cd frontend
npm run dev
```

### Frontend Production Build

```bash
cd frontend
npm run build
```

## Current Development Status

The project includes:

- A unified coffee inventory and shop product model.
- MySQL-backed persistence.
- A React customer storefront.
- Customer checkout and order submission.
- Thymeleaf-based inventory and order administration.
- Customer information displayed in the order list.
- PDF invoice generation.
- Database migration scripts.

The Java backend has been successfully compiled using Maven.

## Repository

**GitHub:** https://github.com/simokostovcik-hash/projekt_sklad

**Primary branch:** `main`

**Key development commit:** `0af2afb` – `44. Unification of Coffee entity and fixing orders`

The `main` branch contains the current Spring Boot inventory management system and React Coffee Shop frontend.

**Note:** Git stores project source code and tracked files, not the live contents of the MySQL database.
