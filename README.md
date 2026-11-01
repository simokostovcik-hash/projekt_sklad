# Coffee Warehouse – Full-Stack E-Commerce Project

**A personal full-stack web development project built with React, Java, Spring Boot, and MySQL.**

Coffee Warehouse (*Kávový sklad*) is a web application designed to demonstrate the core functionality of an online coffee store, including product browsing, shopping cart management, checkout, and order processing.

I started this project to develop my practical programming skills and gain experience building an application that connects a modern frontend with a Java backend and a relational database.

The project is an ongoing learning experience through which I explore frontend development, REST APIs, database management, application architecture, and the challenges of building a complete web application.

## Live Demo

**[Open Coffee Warehouse](https://simokostovcik-hash.github.io/projekt_sklad/)**

The public version is a frontend-only demonstration hosted on GitHub Pages.

It uses simulated data and does not connect to the Spring Boot backend or MySQL database. No real orders or payments are processed.

*The demo link will become available after GitHub Pages has been successfully deployed.*

## Features

### Public Demo

The frontend demonstration includes:

- Coffee product catalogue and product filtering
- Shopping cart functionality
- Checkout process with simulated payment
- Demo order confirmation
- Downloadable text-based demo confirmation
- Responsive web interface

Demo orders are stored temporarily in the current page session and disappear when the page is refreshed. The checkout form does not submit personal information to a backend.

### Full-Stack Application

The repository also contains a separate Java Spring Boot backend and MySQL integration, developed to support the broader e-commerce application.

The backend project includes code related to:

- REST API endpoints
- Product and inventory management
- Order processing
- Database entities and repositories
- Application configuration and security

These backend features are separate from the public GitHub Pages demo and require a locally configured environment.

## Technologies Used

| Area | Technologies |
|---|---|
| Frontend | React, JavaScript, Vite, HTML, CSS |
| Backend | Java, Spring Boot |
| Database | MySQL |
| Development Tools | Git, GitHub, Maven, npm |
| Demo Hosting | GitHub Pages, GitHub Actions |

## Project Structure

```text
projekt_sklad/
├── frontend/          # React and Vite application
│   ├── public/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
├── src/               # Java Spring Boot source code
├── mysql/             # SQL scripts
├── pom.xml            # Maven configuration
└── README.md
```

## Running the Frontend Locally

Requirements: Node.js and npm.

```bash
cd frontend
npm ci
npm run dev
```

To test the production build:

```bash
npm run build
npm run preview
```

The public demo build is configured to use `VITE_PUBLIC_DEMO=true` in `frontend/.env.production`.

## Running the Full-Stack Application

The complete development environment requires Java, Maven, MySQL, and Node.js.

1. Configure and start a local MySQL database.
2. Review the Spring Boot database settings in `src/main/resources/application.properties`.
3. Start the Spring Boot application.
4. Open the `frontend` directory and run `npm ci` followed by `npm run dev`.

During local development, the Vite proxy forwards `/api` requests to `localhost:8080`.

The full-stack application is intended for a configured development environment. Its backend, administrative functions, and order-processing features are not exposed through the public demo.

## What I Am Learning

Through this project, I am gaining hands-on experience with:

- Building reusable React components and interactive interfaces
- Developing backend functionality with Java and Spring Boot
- Designing and working with relational databases
- Connecting frontend applications to REST APIs
- Managing source code and project updates with Git and GitHub
- Understanding the structure and responsibilities of full-stack applications

My goal is to continue improving the application, expand my technical knowledge, and develop the practical skills needed for a junior software development role.

## Demo Access and Backend

The public demo is available without registration or login.

Visitors can explore the coffee catalogue, manage a shopping cart, and test the checkout process using simulated data. No real payments or orders are processed.

The repository also contains the Java Spring Boot backend and MySQL integration developed for the full-stack application.

For security reasons, the backend and administrative features are not publicly accessible. The source code is available in this repository for anyone interested in reviewing the implementation.

The public demo focuses on showcasing the user interface and customer shopping experience, while the repository demonstrates the broader full-stack development work.

## Author

**Šimon Kostovčík**

Aspiring Junior Developer | IT Technician

[GitHub Profile](https://github.com/simokostovcik-hash) | [Project Repository](https://github.com/simokostovcik-hash/projekt_sklad)
