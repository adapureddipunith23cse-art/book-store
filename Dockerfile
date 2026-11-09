
# Stage 1: Build the application
FROM eclipse-temurin:25-jdk AS build

# Install Maven
RUN apt-get update \
    && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package

# Stage 2: Run the application
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/book-store-2.0.0.jar app.jar

EXPOSE 8081

VOLUME ["/app/data"]

ENTRYPOINT ["java", "-jar", "app.jar"]
