# customer-docker-compose-postgres

Simple Spring Boot + PostgreSQL POC using Docker Compose and a persistent Docker volume.

## Project Structure

customer-docker-compose-postgres
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── src/main
    ├── java/com/dj/dockercompose
    │   ├── CustomerDockerComposePostgresApplication.java
    │   ├── controller/CustomerController.java
    │   ├── entity/Customer.java
    │   └── repository/CustomerRepository.java
    └── resources
        ├── application.yaml
        └── data.sql

## Technology

- Java 25
- Spring Boot 4.x
- Spring Web
- Spring Data JPA
- PostgreSQL 17
- Docker
- Docker Compose
- Docker Named Volume

## Architecture

Spring Boot App
      |
      | postgres:5432
      v
PostgreSQL Container
      |
      v
postgres-data Docker Volume

Docker Compose runs two containers:

customer-docker-compose-app
customer-docker-compose-postgres


PostgreSQL data is stored in the named volume:

postgres-data


## Database Configuration

Database : customer-docker-compose-db
Username : postgres
Password : postgres
Port     : 5432

Spring Boot connects using:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres:5432/customer-docker-compose-db
    username: postgres
    password: postgres
```

`postgres` is the Docker Compose service name.

## Docker Compose

```yaml
services:
  postgres:
    image: postgres:17
    container_name: customer-docker-compose-postgres
    environment:
      POSTGRES_DB: customer-docker-compose-db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  app:
    build: .
    container_name: customer-docker-compose-app
    ports:
      - "8080:8080"
    depends_on:
      - postgres

volumes:
  postgres-data:
```

## Build

First create the Spring Boot JAR:

```cmd
mvn clean package -DskipTests
```

## Start Application

```cmd
docker compose up --build
```
## REST API

### Get Customers

GET http://localhost:8080/api/customers

```cmd
curl http://localhost:8080/api/customers
```

### Create Customer

```cmd
curl -X POST http://localhost:8080/api/customers -H "Content-Type: application/json" -d "{\"name\":\"Rahul Dravid\",\"email\":\"rahul.dravid@test.com\"}"
```

## Initial Data

`data.sql` contains:

```sql
INSERT INTO customers (name, email)
VALUES ('Rahul Dravid', 'rahul.dravid@test.com');

INSERT INTO customers (name, email)
VALUES ('Sachin Tendulkar', 'sachin.tendulkar@test.com');

INSERT INTO customers (name, email)
VALUES ('VVS Laxman', 'vvs.laxman@test.com');
```

The application uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    defer-datasource-initialization: true

  sql:
    init:
      mode: always
```

`defer-datasource-initialization: true` ensures that Hibernate creates the table before `data.sql` runs.

Note: `mode: always` means `data.sql` runs on application startup, so these records can be inserted again after restarts.

## Check PostgreSQL from Command Line

Connect directly to PostgreSQL:

```cmd
docker exec -it customer-docker-compose-postgres psql -U postgres -d customer-docker-compose-db
```

Inside PostgreSQL:

```sql
\dt
```

View customer data:

```sql
SELECT * FROM customers;
```

View table structure:

```sql
\d customers
```

Exit:

```sql
\q
```

## Docker Volume

PostgreSQL uses:

```text
postgres-data:/var/lib/postgresql/data
```

This keeps database data when the PostgreSQL container is stopped or recreated normally.

### Stop containers

```cmd
docker compose down
```

The named volume remains.

### Delete containers AND database volume

```cmd
docker compose down -v
```

Warning: this deletes the PostgreSQL data stored in the volume.

## Useful Commands

```cmd
docker compose ps
docker compose logs app
docker compose logs postgres
docker compose down
docker compose up --build
docker volume ls
docker volume inspect customer-docker-compose-postgres_postgres-data
```