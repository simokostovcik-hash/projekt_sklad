# Coffee Storage and Inventory System

A Spring Boot web application designed for coffee warehouse management and e-commerce operations. The system enables the tracking of stock levels, management of partner roasteries, and a complete workflow from order placement to PDF invoice generation.

## Key Features

* **Inventory Tracking**: Real-time management of coffee varieties, weight, pricing, and stock status.
* **Roastery Management**: Dedicated module for managing roastery details including country of origin and contact information.
* **Order Management System**:
    * **Shopping Cart**: Interactive management of items and quantities.
    * **Checkout Workflow**: Advanced form capturing shipping addresses and company billing details (ID/VAT).
    * **Order History**: Comprehensive list of orders for both users and administrators.
* **PDF Invoice Generation**: Automatic creation of professional invoices using the OpenPDF (iText) library, including shipping/billing addresses and itemized totals.
* **Role-Based Access Control (RBAC)**:
    * **ADMIN**: Full authority for CRUD operations (Inventory, Roasteries, Orders) and audit logging.
    * **USER**: Browse inventory, place orders, and access personal order history.
* **Dynamic User Interface**: JavaScript-powered table filtering and sorting, utilizing Bootstrap 5 modals for a modern user experience.

## Tech Stack

* **Backend**: Java 21+, Spring Boot 3.2.2, Spring Security
* **Persistence**: Spring Data JPA, Hibernate, PostgreSQL
* **Frontend**: Thymeleaf, Bootstrap 5, JavaScript (ES6+)
* **PDF Engine**: OpenPDF (LibrePDF)
* **Build Tool**: Maven

## Project Structure

src/main/java/cz/projekt_sklad/
├── controller/    # Web Request Handlers (Storage, User, Order)
├── model/         # JPA Entities (Coffee, Roastery, User, Order, OrderItem, AuditLog)
├── repository/    # Data Access Layer (JPA Repositories)
├── service/       # Business Logic Layer (InvoiceService, Security Services)
└── security/      # Security Configuration

## Getting Started

### Prerequisites
* **JDK 21** or higher (tested on JDK 23)
* **Maven**
* **PostgreSQL Server** (locally or via Docker)

### Installation and Execution
1. **Clone the repository**:
   git clone https://github.com/simokostovcik-hash/projekt_sklad.git

2. **Database Configuration**:
   The application uses PostgreSQL. Ensure you have a database created (default name: coffee_db). Database connection settings (URL, username, password) can be found and modified in src/main/resources/application.properties.

3. **Build and run the application**:
   Choose one of the following methods to ensure a successful start:
    * **Option A (IntelliJ - Recommended)**: Open ProjektSkladApplication.java and click the green arrow next to the main method.
    * **Option B (Maven Panel)**: In the Maven tab, run spring-boot:run with the parameter -DskipTests to bypass template test conflicts.
    * **Option C (Terminal)**: mvn spring-boot:run -DskipTests

4. **Access the application**:
    * URL: http://localhost:8080
    * **Admin Login Credentials**: admin / admin123
    * **User Credentials**: test / test

## Data Initialization
To quickly populate the database with sample data for testing:
1. Log in as an **Admin**.
2. Navigate to the **Generate** link (or go to /test/generate-data).
3. This executes logic within the controller that parses data.json and seeds the PostgreSQL database with sample coffee and roastery entries.

## Invoicing Logic
The system utilizes a dedicated InvoiceService to generate A4 PDF documents. The service automatically detects if an order is a "Company Order" and adjusts the invoice header to include:
* Full Name and Shipping Address.
* Company Name, ID (IČO), and VAT (DIČ) where applicable.
* Itemized table of purchased products.
* Calculated Grand Total in CZK.