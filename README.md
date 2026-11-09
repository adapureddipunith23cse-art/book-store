# Bookhaven – Book Store Management System

A full-stack Book Store Management Web Application built using **Java, Spring Boot, H2 Database, HTML, CSS, JavaScript, and Bootstrap**. The project also includes automated testing, Docker containerization, and Jenkins-based CI/CD.

## Features

- Add new books.
- View all available books.
- Update existing book details.
- Delete book records.
- Store book information in an H2 database.
- REST API integration between frontend and backend.
- Automated application testing using JUnit.
- Docker-based application deployment.
- Persistent database storage using Docker volumes.
- Automated build and deployment using Jenkins.

## Technology Stack

| Category | Technologies |
|---|---|
| Frontend | HTML, CSS, JavaScript, Bootstrap |
| Backend | Java 25, Spring Boot 3.4.5 |
| Database | H2 Database |
| ORM | Spring Data JPA, Hibernate |
| Build Tool | Apache Maven 3.9.16 |
| Testing | JUnit 5 |
| Containerization | Docker, Docker Compose |
| CI/CD | Jenkins |
| Version Control | Git, GitHub |

## Application Architecture

```text
              User
               |
               v
        Frontend Interface
      (HTML, CSS, JavaScript)
               |
               v
        Spring Boot Backend
               |
               v
          REST APIs
               |
               v
           H2 Database
```

## Project Structure

```text
book-store-full/
│
├── src/
│   ├── main/
│   │   ├── java/                 # Backend Java source code
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/           # Frontend files
│   │
│   └── test/
│       ├── java/                 # Automated test classes
│       └── resources/            # Test configuration
│
├── data/                         # Local H2 database files
├── target/                       # Maven build output
│
├── Dockerfile                    # Docker image instructions
├── compose.yaml                  # Docker Compose configuration
├── .dockerignore                 # Docker build exclusions
├── pom.xml                       # Maven configuration
└── README.md                     # Project documentation
```

## Running the Application Locally

### Prerequisites

Install the following:

- Java JDK 25
- Apache Maven 3.9.16
- Git

### 1. Clone the repository

```bash
git clone https://github.com/adapureddipunith23cse-art/book-store.git
cd book-store
```

### 2. Check Java and Maven

```bash
java -version
mvn -version
```

### 3. Build and test the project

```bash
mvn clean test package
```

This command:
- Cleans previous build files.
- Compiles the application.
- Runs automated tests.
- Packages the application into a JAR file.

Generated JAR:

```text
target/book-store-2.0.0.jar
```

### 4. Run the application

```bash
java -jar target/book-store-2.0.0.jar
```

Open:

```text
http://localhost:8081
```

If port 8081 is already occupied, use another port:

```bash
java -jar target/book-store-2.0.0.jar --server.port=8082
```

## Database Configuration

The application uses an H2 file-based database.

Database configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:h2:file:./data/bookstoredb;AUTO_SERVER=TRUE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

The database stores book information and preserves records between application restarts.

The project also uses a separate in-memory H2 database for automated tests. This prevents test execution from modifying the main application database.

## Automated Testing with JUnit

JUnit is used to test the application functionality.

Test class:

```text
BookStoreIntegrationTest
```

Run tests:

```bash
mvn test
```

The last verified successful test run reported:

```text
Tests run: 9
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

## Docker Containerization

Docker is used to package and run the application inside a container.

### Dockerfile

The project uses a multi-stage Docker build.

**Build stage:**
- Uses a Java 25 JDK image.
- Installs Maven.
- Copies the project source code.
- Builds the application JAR.

**Runtime stage:**
- Uses a Java 25 JRE image.
- Copies the generated JAR.
- Starts the Spring Boot application.

This separates the build environment from the runtime environment.

### Docker Compose

The `compose.yaml` file manages the application container, port mapping, persistent storage, and restart policy.

```yaml
services:
  bookstore:
    build: .
    container_name: bookstore-app
    ports:
      - "8081:8081"
    volumes:
      - bookstore-data:/app/data
    restart: unless-stopped

volumes:
  bookstore-data:
    external: true
    name: book-store-full_bookstore-data
```

### Understanding the Configuration

| Configuration | Purpose |
|---|---|
| `build: .` | Builds the Docker image using the Dockerfile |
| `container_name` | Sets the container name |
| `8081:8081` | Maps the host port to the container port |
| `volumes` | Provides persistent database storage |
| `restart: unless-stopped` | Restarts the container unless manually stopped |
| `external: true` | Uses an existing Docker volume |

### Important: Persistent Database Storage

The project uses this existing Docker volume:

```text
book-store-full_bookstore-data
```

It is mounted inside the container at:

```text
/app/data
```

This keeps database files separate from the container. Replacing the container does not remove the stored book records as long as the volume is preserved.

**Important:** Do not run `docker compose down -v` or remove the database volume. These actions can delete persistent database data.

### Build and Run with Docker Compose

First, make sure Docker Desktop is running.

Create the named volume if it does not already exist:

```bash
docker volume create book-store-full_bookstore-data
```

Build the image and start the application:

```bash
docker compose up -d --build
```

Open the application:

```text
http://localhost:8081
```

### Useful Docker Commands

| Command | Purpose |
|---|---|
| `docker ps` | View running containers |
| `docker ps -a` | View all containers |
| `docker images` | View Docker images |
| `docker volume ls` | View Docker volumes |
| `docker logs bookstore-app` | View application logs |
| `docker start bookstore-app` | Start the container |
| `docker stop bookstore-app` | Stop the container |
| `docker restart bookstore-app` | Restart the container |

## Jenkins CI/CD Pipeline

Jenkins automates the application's build, testing, Docker image creation, and deployment.

### Jenkins Configuration

| Item | Details |
|---|---|
| Jenkins Job | `BookStore-Maven-Jenkins` |
| Repository | `https://github.com/adapureddipunith23cse-art/book-store` |
| Branch | `main` |
| Build Tool | Maven |
| Testing | JUnit |
| Deployment | Docker Compose |

### Jenkins Workflow

```text
       GitHub Repository
               |
               v
        Jenkins Checkout
               |
               v
       Maven Build and Test
               |
               v
        Docker Image Build
               |
               v
       Docker Compose Deploy
               |
               v
       Running Application
```

### Three Jenkins Build Steps

**Step 1: Maven Build and Test**

```bat
set "JAVA_HOME=C:\Users\punit\.jdk\jdk-25\jdk-25.0.2"
set "PATH=%JAVA_HOME%\bin;C:\Program Files\apache-maven-3.9.16\bin;%PATH%"

call mvn clean test package
if errorlevel 1 exit /b 1
```

This compiles the project, runs JUnit tests, and generates the JAR file.

**Step 2: Docker Image Build**

The project uses the Docker Compose executable to build the image.

```bat
@echo off
set "COMPOSE_PATH=C:\Users\punit\AppData\Local\Programs\DockerDesktop\resources\cli-plugins\docker-compose.exe"

"%COMPOSE_PATH%" -f compose.yaml build
if errorlevel 1 exit /b 1
```

**Step 3: Application Deployment**

```bat
@echo off
set "COMPOSE_PATH=C:\Users\punit\AppData\Local\Programs\DockerDesktop\resources\cli-plugins\docker-compose.exe"

"%COMPOSE_PATH%" -f compose.yaml up -d
if errorlevel 1 exit /b 1
```

These commands reflect the current Windows Jenkins configuration. Update local executable paths if Jenkins or Docker Desktop is installed elsewhere.

### How CI/CD Works

1. Jenkins retrieves the latest source code from GitHub.
2. Maven compiles the application and runs tests.
3. If tests pass, Docker builds the application image.
4. Docker Compose starts or updates the application container.
5. Jenkins displays the final build status.

## Application URLs

| Component | URL |
|---|---|
| Book Store Website | http://localhost:8081 |
| Books REST API | http://localhost:8081/api/books |
| H2 Database Console | http://localhost:8081/h2-console |
| Jenkins Dashboard | http://localhost:8080 |

## Git and GitHub Commands

Check the current status:

```bash
git status
```

Stage changes:

```bash
git add .
```

Commit changes:

```bash
git commit -m "Update project documentation"
```

Push changes:

```bash
git push origin main
```

Pull the latest changes:

```bash
git pull origin main
```

## Future Improvements

- Add user authentication and authorization.
- Introduce role-based access control.
- Add pagination and advanced book search.
- Improve application security.
- Add additional automated tests.
- Extend the CI/CD pipeline with code quality checks.

## Author

**Adapureddi Punith**

B.Tech – Computer Science and Engineering

GitHub: [punithadapureddi](https://github.com/adapureddipunith23cse-art)

## License

This project is developed for academic and learning purposes.