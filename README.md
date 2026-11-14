# Dispatch Load Balancer

A Spring Boot REST API that intelligently assigns delivery orders to a fleet of vehicles while considering vehicle capacity, order priority, and travel distance.

The application prioritizes HIGH-priority orders and uses the Haversine formula to calculate geographical distances and minimize total delivery travel.

## Live Demo

- **Live Application:** https://dispatch-load-balancer-e7d5.onrender.com/
- **Swagger UI (API Documentation):** https://dispatch-load-balancer-e7d5.onrender.com/swagger-ui/index.html#/dispatch-controller/getPlan

> **Note:** The free hosting service may put the application to sleep when idle. The first request after inactivity may take approximately 50 seconds.
>
> The application currently uses an in-memory H2 database. Data is not permanent and may be lost when the application restarts. Add orders and vehicles before generating a dispatch plan.

## Project Links

- **GitHub Repository:** https://github.com/Vipinkushuk/Dispatch-Load-Balancer
- **LinkedIn:** https://www.linkedin.com/in/vipin-kumar-5217a1218/
- **Live API:** https://dispatch-load-balancer-e7d5.onrender.com/

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4 | Backend framework |
| Spring Data JPA | Database operations |
| H2 Database | In-memory database |
| Bean Validation | Request validation |
| Springdoc OpenAPI | Swagger API documentation |
| Maven | Build and dependency management |
| JUnit 5 | Unit and integration testing |
| Docker | Containerization |

## Getting Started

### Prerequisites

- Java 17
- Maven (optional, as the project includes Maven Wrapper)
- Git (optional)

### Run Locally

**1. Clone the repository**

```bash
git clone https://github.com/Vipinkushuk/Dispatch-Load-Balancer.git
cd Dispatch-Load-Balancer
```

**2. Start the application**

Windows (PowerShell):

```powershell
.\mvnw.cmd spring-boot:run
```

Mac/Linux:

```bash
./mvnw spring-boot:run
```

**3. Access the application**

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

### Run Tests

Windows:

```powershell
.\mvnw.cmd test
```

Mac/Linux:

```bash
./mvnw test
```

## API Documentation

The application provides the following REST endpoints:

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/dispatch/orders` | Add or update delivery orders |
| POST | `/api/dispatch/vehicles` | Add or update vehicle details |
| GET | `/api/dispatch/plan` | Generate the dispatch plan |

### API Usage

Use Swagger UI to explore the endpoints, provide JSON requests, and view responses.

**Swagger URL:** https://dispatch-load-balancer-e7d5.onrender.com/swagger-ui/index.html#/dispatch-controller/getPlan

## Dispatch Algorithm

The application uses a Greedy Nearest Vehicle Strategy to assign orders.

The assignment process follows these rules:

1. **Priority first:** Orders are sorted by priority (HIGH → MEDIUM → LOW).
2. **Heavier orders first:** Orders with greater weight are considered first within the same priority.
3. **Nearest vehicle:** Each order is assigned to the nearest vehicle with sufficient remaining capacity.
4. **Capacity validation:** A vehicle cannot exceed its maximum load capacity.
5. **Distance calculation:** The Haversine formula calculates geographical distance between locations.
6. **Tie-breaking:** If two vehicles are equally close, the vehicle with more remaining capacity is selected.
7. **Unassigned orders:** Orders that cannot be assigned are returned with a reason.

### Important Assumptions

- A vehicle moves to its last assigned delivery location.
- Total distance is calculated using individual delivery legs.
- Return-to-depot distance is not included.
- Existing order IDs and vehicle IDs are updated instead of duplicated (upsert).
- The algorithm is a greedy heuristic, not a mathematically optimal solution.

## Project Architecture

The application follows a layered architecture:

- **Controller Layer:** Handles incoming HTTP requests and API responses.
- **Service Layer:** Contains business logic and dispatch operations.
- **Repository Layer:** Handles database interactions using Spring Data JPA.
- **Strategy Pattern:** Keeps the vehicle assignment algorithm separate and extensible through `AssignmentStrategy`.
- **DTOs:** Separate API request/response objects from database entities.
- **Global Exception Handler:** Provides consistent error responses.

## Testing

The project includes tests covering:

- Haversine distance calculation accuracy.
- Order priority sorting.
- Vehicle capacity restrictions.
- Nearest vehicle selection.
- Unassignable orders.
- API request validation and error handling.

Run all tests using:

```bash
.\mvnw.cmd test
```

## Docker

Build the Docker image:

```bash
docker build -t dispatch-load-balancer .
```

Run the container:

```bash
docker run -p 8080:8080 dispatch-load-balancer
```

Access Swagger UI:

http://localhost:8080/swagger-ui/index.html

---

**Project:** Dispatch Load Balancer  
**Purpose:** Intelligent delivery order assignment and vehicle dispatch optimization.

**Developer:** [Vipin Kumar](https://www.linkedin.com/in/vipin-kumar-5217a1218/)

**GitHub:** https://github.com/Vipinkushuk/Dispatch-Load-Balancer