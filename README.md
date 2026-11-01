# Coffee Warehouse (Kávový sklad)

### Full-Stack E-Commerce Application | React · Java · Spring Boot · MySQL

Coffee Warehouse is my personal full-stack development project focused on building a web application for an online coffee store.

The project combines a React frontend, a Java Spring Boot backend, and a MySQL database. It is designed around common e-commerce functionality, including browsing products, managing a shopping cart, placing orders, and working with inventory data.

I started developing this application to improve my programming skills and gain practical experience with the technologies and challenges involved in full-stack web development.

The project is still evolving as I continue learning, improving existing features, and experimenting with new functionality.

---

## Live Demo

**[Open Coffee Warehouse – Public Demo](https://simokostovcik-hash.github.io/projekt_sklad/)**

*The link will become available after the GitHub Pages deployment is completed.*

The public demo is designed to let visitors explore the customer-facing application without registration or login.

It runs entirely in the browser using simulated data. The Spring Boot backend and MySQL database are not connected to the public version.

No real orders are placed, no payments are processed, and no checkout information is sent to the backend.

## Features

### Public Frontend Demo

The public demonstration is designed to showcase:

- Coffee product catalogue
- Product browsing and filtering
- Shopping cart management
- Checkout process
- Simulated payment
- Demo order confirmation
- Downloadable text-based demo confirmation
- Responsive user interface

The demo uses temporary data stored in the current page session. Test orders are not permanently saved and disappear when the page is refreshed.

Visitors can explore the application without creating an account.

### Full-Stack Development

In addition to the public frontend demo, the repository contains the backend implementation developed using Java and Spring Boot.

The backend includes components for:

- REST API communication
- Product and inventory management
- Order processing
- Database entities and repositories
- Application configuration
- Authentication and security configuration
- Administrative functionality

These features are part of the broader development project and are not publicly accessible through GitHub Pages.

---

## Technology Stack

| Area | Technologies |
|---|---|
| Frontend | React, JavaScript, HTML, CSS |
| Build Tool | Vite |
| Backend | Java, Spring Boot |
| Database | MySQL |
| Backend Build | Maven |
| Version Control | Git, GitHub |
| Public Demo Hosting | GitHub Pages, GitHub Actions |

## Project Structure

```text
projekt_sklad/
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── api/
│   │   ├── assets/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── mysql/
├── pom.xml
└── README.md
```

The `frontend` directory contains the React application.

The `src` directory contains the Java Spring Boot backend, while `mysql` contains SQL scripts related to the database.

## Running the Frontend Locally

**Requirements:** Node.js and npm.

Clone the repository:

```bash
git clone https://github.com/simokostovcik-hash/projekt_sklad.git
cd projekt_sklad/frontend
```

Install dependencies:

```bash
npm ci
```

Start the development server:

```bash
npm run dev
```

Vite will display the local development URL in the terminal.

### Testing the Public Demo Build

The public demo configuration uses `VITE_PUBLIC_DEMO=true` in `frontend/.env.production`.

To build and preview the production version:

```bash
npm run build
npm run preview
```

The production demo is designed to work without connecting to the Java backend.

## Running the Full-Stack Application Locally

**Requirements:**

- Java
- Maven
- MySQL
- Node.js and npm

To run the full-stack development version:

1. Set up and start a local MySQL database.
2. Review the SQL scripts in the `mysql` directory.
3. Configure the database connection in the Spring Boot application settings.
4. Start the Spring Boot backend.
5. Start the React frontend using `npm run dev`.

The Vite development proxy is configured to forward `/api` requests to `localhost:8080`.

The full-stack version requires an appropriately configured local environment and is separate from the public demo.

Do not publish real database passwords, API keys, or other credentials in the repository.

---

## Demo Access and Security

The public demo does not require registration or login.

Visitors can browse the application and try the shopping experience using simulated data.

For security reasons, the backend and administrative functionality are not exposed through the public GitHub Pages demo.

The repository contains the backend source code for anyone interested in reviewing the application's architecture and implementation.

The public demo is intended to demonstrate the user interface and customer experience, while the source code demonstrates the broader full-stack development work.

## What I Am Learning

This project helps me develop practical experience in several areas of software development:

- Creating interactive user interfaces with React
- Working with JavaScript and frontend application state
- Developing REST APIs using Java and Spring Boot
- Working with relational databases and SQL
- Connecting frontend and backend components
- Understanding authentication and application security
- Organizing and maintaining a larger codebase
- Using Git and GitHub for version control
- Preparing applications for deployment

I use this project as an opportunity to apply what I learn, solve technical problems, and improve my understanding of how real web applications are designed and developed.

## Future Development

As I continue working on Coffee Warehouse, I would like to focus on:

- Improving the user interface and overall usability
- Refactoring and organizing the existing code
- Expanding automated testing
- Improving backend security and configuration
- Refining database operations and API integration
- Exploring secure deployment options for the complete full-stack application

These are development goals rather than features guaranteed to be available in the current version.

## Important Notes

Coffee Warehouse is a personal educational and portfolio project, not a commercial online store.

The public demo does not process real payments or orders. Visitors should use fictional information when testing checkout functionality.

Some product images may be loaded from external services such as Unsplash.

The backend is not publicly hosted, and its administrative functionality is not available to public demo visitors.

## Author

**Šimon Kostovčík**

Aspiring Junior Software Developer | IT Technician

I am interested in software development, computer hardware, and learning new technologies. Coffee Warehouse is one of the projects through which I am building practical experience and improving my development skills.

**GitHub:** https://github.com/simokostovcik-hash

**Repository:** https://github.com/simokostovcik-hash/projekt_sklad
