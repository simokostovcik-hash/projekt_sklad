# Coffee Shop – Inventory Management & E-Commerce System

A full-stack coffee management and e-commerce application built with **Spring Boot, React, and H2 Database**.

The project combines a warehouse and inventory management system with a modern customer-facing coffee shop. Administrators can manage coffee products, roasteries, inventory, users, and orders, while customers can browse coffee products and place orders through a React frontend.

## Key Features

### Coffee Shop – React Frontend

- **Modern E-Commerce Interface** – Responsive coffee shop built with React and Vite.
- **Coffee Product Catalog** – Browse available coffee products and view product information.
- **Shopping Cart** – Select coffee products and manage items before checkout.
- **Order Placement** – Submit customer orders through the Spring Boot REST API.
- **Backend Integration** – React communicates with the Java backend using HTTP requests.

### Administration & Inventory Management

- **Inventory Tracking** – Manage coffee varieties, stock quantities, prices, and availability.
- **Roastery Management** – Manage partner roasteries and their information.
- **Order Management** – View and process orders from the coffee shop.
- **Role-Based Access Control** – Secure access using Spring Security.
- **Admin Dashboard** – Overview of important warehouse and business information.
- **Filtering and Sorting** – Browse and organize inventory records.
- **Audit Logging** – Record administrative activities.
- **Data Initialization** – Support for populating the database with initial data.

## Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java, Spring Boot, Spring Security |
| REST API | Spring Web |
| Database | H2 Database |
| Persistence | Spring Data JPA, Hibernate |
| Customer Frontend | React, Vite, JavaScript |
| Admin Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Build Tools | Maven, npm |

## Project Structure

```text
projekt_sklad/
├── frontend/                       # React customer e-shop
│   ├── public/                     # Static assets
│   ├── src/
│   │   ├── api/                    # Backend API communication
│   │   ├── assets/                 # Images and other assets
│   │   ├── App.jsx                 # Main React application
│   │   ├── App.css                 # Application styles
│   │   └── main.jsx                # React entry point
│   ├── package.json
│   └── vite.config.js
│
├── src/
│   └── main/
│       ├── java/cz/project_storage/
│       │   ├── config/             # Application configuration
│       │   ├── controller/         # Web and REST controllers
│       │   ├── model/              # JPA entities
│       │   ├── repository/         # Database access
│       │   └── service/            # Business logic
│       │
│       └── resources/
│           ├── templates/         # Thymeleaf admin pages
│           ├── static/            # CSS and static resources
│           └── application.properties
│
├── data/                           # Local H2 database files
├── pom.xml                         # Maven configuration
└── README.md
```

## Getting Started

### Prerequisites

Before running the project, install:

- **Java JDK 21+** (use the version configured in `pom.xml`)
- **Maven** or the project's Maven wrapper, if available
- **Node.js and npm**
- **Git**

### 1. Clone the Repository

```bash
git clone https://github.com/simokostovcik-hash/projekt_sklad.git
cd projekt_sklad
```

### 2. Start the Spring Boot Backend

Open a terminal in the project root and run:

```bash
mvn spring-boot:run
```

The backend runs at:

**http://localhost:8080**

The Spring Boot application provides the administration interface, database access, and REST API used by the React frontend.

### 3. Start the React Frontend

Open a second terminal and run:

```bash
cd frontend
npm install
npm run dev
```

The React coffee shop is available at:

**http://localhost:5173**

> Both the Spring Boot backend and React frontend must be running for the complete application to work locally.

## Application Interfaces

| Interface | URL | Description |
|---|---|---|
| React Coffee Shop | http://localhost:5173 | Customer-facing e-commerce application |
| Spring Boot Backend | http://localhost:8080 | Administration and backend services |

## REST API

The application includes REST controllers for communication between React and Spring Boot.

Main components include:

- `CoffeeApiController` – Coffee product data.
- `ShopOrderApiController` – Customer order processing.
- `StorageController` – Warehouse and inventory functionality.

These controllers connect the frontend with the application's business logic and database.

## Database

The project uses **H2 Database** with Spring Data JPA and Hibernate.

Database configuration is located in:

```text
src/main/resources/application.properties
```

The project includes a `data/` directory for local database files.

**Important:** Existing database files may contain application data. Back them up before resetting, replacing, or deleting the database.

## Security

Spring Security provides authentication and role-based authorization for the administration interface.

The application supports administrative functionality for managing inventory, roasteries, and orders, with access restrictions based on user roles.

For security reasons, production credentials should be configured separately and should not be committed to the repository.

## Development

### Backend

```bash
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm run dev
```

### Frontend Production Build

```bash
cd frontend
npm run build
```

## Project Documentation

Additional documentation and development notes are available in the repository, including:

- `FRONTEND_ZMENY.md`
- `NAVOD_K_PROJEKTU.md`
- `NAVOD_OPRAVY.md`
- `OPRAVA_OBJEDNAVEK.md`
- `UPRAVY_README.md`

## Repository

**GitHub:** https://github.com/simokostovcik-hash/projekt_sklad

The `main` branch contains the current version of the Spring Boot inventory management system and React Coffee Shop frontend.
