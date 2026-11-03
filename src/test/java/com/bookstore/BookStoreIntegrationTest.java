package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookStoreIntegrationTest {
    @Autowired private BookStore store;
    @Autowired private BookRepository repository;
    @Autowired private MockMvc mockMvc;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void addsAndSearchesBookCaseInsensitively() {
        store.addBook(new Book("B101", "Java Basics", "A. Author", 500.0, 4));
        assertNotNull(store.searchBook("b101"));
        assertEquals(1, store.searchBooks("JAVA").size());
    }

    @Test
    void rejectsDuplicateIdIgnoringCase() {
        store.addBook(new Book("B101", "Java Basics", "A. Author", 500.0, 4));
        assertThrows(DuplicateBookException.class,
                () -> store.addBook(new Book("b101", "Other", "B. Author", 100.0, 1)));
    }

    @Test
    void updatesAndDeletesBook() {
        store.addBook(new Book("B101", "Java Basics", "A. Author", 500.0, 4));
        store.updateBook("B101", new Book("B101", "Advanced Java", "A. Author", 700.0, 6));
        assertEquals("Advanced Java", store.searchBook("B101").getTitle());
        assertTrue(store.deleteBook("B101"));
        assertNull(store.searchBook("B101"));
    }

    @Test
    void purchaseReducesStockAndReturnsExactTotal() {
        store.addBook(new Book("B101", "Java Basics", "A. Author", new BigDecimal("650.00"), 4));
        BookStore.PurchaseReceipt receipt = store.buyBook("B101", 2);
        assertEquals(new BigDecimal("1300.00"), receipt.total());
        assertEquals(2, store.searchBook("B101").getQuantity());
    }

    @Test
    void purchaseRejectsInvalidQuantityAndInsufficientStock() {
        store.addBook(new Book("B101", "Java Basics", "A. Author", 500.0, 2));
        assertThrows(IllegalArgumentException.class, () -> store.buyBook("B101", 0));
        assertThrows(InsufficientStockException.class, () -> store.buyBook("B101", 3));
        assertEquals(2, store.searchBook("B101").getQuantity());
    }

    @Test
    void rejectsInvalidBookData() {
        assertThrows(IllegalArgumentException.class,
                () -> store.addBook(new Book("B101", "Bad price", "A. Author", 0.0, 2)));
        assertThrows(IllegalArgumentException.class,
                () -> store.addBook(new Book("B102", "Negative stock", "A. Author", 10.0, -1)));
    }

        @Test
        void restApiSupportsCreateSearchUpdateAndPersistence() throws Exception {
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":"B201","title":"Testing Spring","author":"A. Author","price":425.50,"quantity":7}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("B201"))
            .andExpect(jsonPath("$.price").value(425.50));

        assertTrue(repository.findById("B201").isPresent());

        mockMvc.perform(get("/api/books").param("search", "spring"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("B201"));

        mockMvc.perform(put("/api/books/B201")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":"B201","title":"Advanced Spring Testing","author":"A. Author","price":500.00,"quantity":9}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Advanced Spring Testing"))
            .andExpect(jsonPath("$.quantity").value(9));

        assertEquals("Advanced Spring Testing", repository.findById("B201").orElseThrow().getTitle());
        }

        @Test
        void restApiPurchasesAndDeletesBooks() throws Exception {
        store.addBook(new Book("B202", "Clean Java", "A. Author", new BigDecimal("650.00"), 4));

        mockMvc.perform(post("/api/books/B202/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":2}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bookId").value("B202"))
            .andExpect(jsonPath("$.total").value(1300.00))
            .andExpect(jsonPath("$.remainingStock").value(2));

        assertEquals(2, repository.findById("B202").orElseThrow().getQuantity());

        mockMvc.perform(delete("/api/books/B202"))
            .andExpect(status().isNoContent());
        assertFalse(repository.existsById("B202"));
        }

        @Test
        void restApiReturnsErrorsForInvalidRequests() throws Exception {
        mockMvc.perform(get("/api/books/MISSING"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Book 'MISSING' was not found."));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":"B203","title":"","author":"A. Author","price":0,"quantity":-1}
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").exists());

        store.addBook(new Book("B204", "Limited Stock", "A. Author", 100.0, 1));
        mockMvc.perform(post("/api/books/B204/purchase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":2}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("Only 1 copies are available."));

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id":"b204","title":"Duplicate","author":"Another Author","price":100,"quantity":1}
                    """))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.error").value("A book with ID 'b204' already exists."));
        }
}
