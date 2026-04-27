Coffee Storage and Inventory System
A Spring Boot web application designed for coffee warehouse management and e-commerce operations. The system enables the tracking of stock levels, management of partner roasteries, and a complete workflow from order placement to PDF invoice generation.

Key Features
Inventory Tracking: Real-time management of coffee varieties, weight, pricing, and stock status.

Roastery Management: Dedicated module for managing roastery details including country of origin and contact information.

Order Management System:

Shopping Cart: Interactive management of items and quantities.

Checkout Workflow: Advanced form capturing shipping addresses and company billing details (ID/VAT).

Order History: Comprehensive list of orders for both users and administrators.

PDF Invoice Generation: Automatic creation of professional invoices using the OpenPDF (iText) library, including shipping/billing addresses and itemized totals.

Role-Based Access Control (RBAC):

ADMIN: Full authority for CRUD operations (Inventory, Roasteries, Orders) and audit logging.

USER: Browse inventory, place orders, and access personal order history.

Dynamic User Interface: JavaScript-powered table filtering and sorting, utilizing Bootstrap 5 modals for a modern user experience.

Tech Stack
Backend: Java 21/23, Spring Boot 3.2.2, Spring Security

Persistence: Spring Data JPA, Hibernate, H2 Database

Frontend: Thymeleaf, Bootstrap 5, JavaScript (ES6+)

PDF Engine: OpenPDF (LibrePDF)

Build Tool: Maven

## Project Structure

src/main/java/cz/project_storage/
├── controller/    # Web Request Handlers (Storage, User, Order)
├── model/         # JPA Entities (Coffee, Roastery, User, Order, OrderItem, AuditLog)
├── repository/    # Data Access Layer (JPA Repositories)
├── service/       # Business Logic Layer (InvoiceService, Security Services)
└── security/      # Security Configuration

Getting Started
Prerequisites
JDK 21 or higher (tested on JDK 23)

Maven

Installation and Execution
Clone the repository:

Bash
git clone https://github.com/simokostovcik-hash/projekt_sklad.git
Database Configuration:
The application uses H2 by default. Configuration can be found in src/main/resources/application.properties:

Properties
spring.jpa.hibernate.ddl-auto=update
Build and run the application:

Bash
mvn spring-boot:run
Access the application:

URL: http://localhost:8080

Admin Credentials: admin / admin123

Data Initialization
To quickly populate the database with sample data for testing:

Log in as an Admin.

Navigate to the Generate link (or go to /test/generate-data).

This executes logic within the controller that parses data.json and seeds the H2 database with sample coffee and roastery entries.

Invoicing Logic
The system utilizes a dedicated InvoiceService to generate A4 PDF documents. The service automatically detects if an order is a "Company Order" and adjusts the invoice header to include:

Full Name and Shipping Address.

Company Name, ID (IČO), and VAT (DIČ) where applicable.

Itemized table of purchased products.

Calculated Grand Total in CZK.
