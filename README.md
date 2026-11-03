# Bookhaven — Book Store Management System

A complete Java 25 + Spring Boot + H2 + HTML/CSS/JavaScript application. The browser UI calls the Java REST API; the API calls `BookStore.java`; `BookStore.java` saves changes through Spring Data JPA into the H2 database.

## 1. What you need

Install:
- **JDK 25** (make sure `java -version` shows 25)
- **Maven 3.9+** (make sure `mvn -version` works)
- IntelliJ IDEA or VS Code (either is fine)

Internet is needed the first time Maven downloads the project dependencies.

## 2. Open the project

1. Download and extract `book-store-full.zip`.
2. Open the extracted `book-store-full` folder in IntelliJ or VS Code.
3. Wait for Maven to import the dependencies.
4. Confirm that the project SDK / Java runtime is JDK 25.

## 3. Run the application

Open a terminal in the folder containing `pom.xml`, then run:

```bash
mvn spring-boot:run
```

Wait until Spring Boot reports that the application has started. Open:

**http://localhost:8080**

The sample books are inserted automatically the first time the database is empty:
- B101 — Java Programming
- B102 — Python Basics
- B103 — Clean Code

## 4. Try the UI

- **Add a book:** select “Add a book”, fill in all fields and save.
- **Edit:** choose Edit beside a book, change its details and save.
- **Delete:** choose Delete and confirm.
- **Sell:** choose Sell, select a quantity and confirm. Stock is reduced in the database.
- **Search:** search by title, author or book ID.
- **Sort:** sort by title, price or stock.

All changes go through the backend. Refreshing the page or restarting the app does not discard saved book records.

## 5. How the connection works

```text
Browser (index.html + app.js)
          |
          | HTTP request: /api/books
          v
BookController.java
          |
          v
BookStore.java       <-- business rules and validation
          |
          v
BookRepository.java
          |
          v
H2 database          <-- stores the books
```

For example, when you click **Sell**:
1. `app.js` sends a POST request to `/api/books/B101/purchase`.
2. `BookController.java` receives the request.
3. `BookStore.java` checks the ID, quantity and available stock.
4. `BookRepository.java` saves the reduced stock.
5. The API sends the purchase total and remaining stock to the UI.
6. The UI reloads the book list and shows the updated stock.

## 6. Run automated tests

Stop the running app, then execute:

```bash
mvn test
```

The integration tests cover adding/searching, duplicate IDs, update/delete, purchases, insufficient stock and invalid book details.

## 7. Database location

The H2 database is stored in the project's `data/` folder. Do not delete this folder if you want to keep your local book data.

Optional H2 database console:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/bookstoredb;AUTO_SERVER=TRUE`
- User: `sa`
- Password: leave blank

## 8. Useful API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/books` | List all books |
| GET | `/api/books?search=java` | Search books |
| GET | `/api/books/B101` | Get one book |
| POST | `/api/books` | Add a book |
| PUT | `/api/books/B101` | Update a book |
| DELETE | `/api/books/B101` | Delete a book |
| POST | `/api/books/B101/purchase` | Purchase copies |

Example add-book JSON:

```json
{
  "id": "B104",
  "title": "Learning SQL",
  "author": "Alan Beaulieu",
  "price": 599.00,
  "quantity": 8
}
```

Example purchase JSON:

```json
{
  "quantity": 2
}
```

## Notes

- The old console menu has been replaced by the web interface. `Main.java` now starts Spring Boot.
- Prices use `BigDecimal` in Java to avoid common floating-point money calculation issues.
- H2 is a lightweight local database suitable for learning and a small demo. It is not configured for public production deployment.
- The UI is served by Spring Boot itself, so you do not need to start a separate frontend server or configure CORS manually.
