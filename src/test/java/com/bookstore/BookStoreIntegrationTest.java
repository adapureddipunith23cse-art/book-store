package com.bookstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookStoreIntegrationTest {
    @Autowired private BookStore store;
    @Autowired private BookRepository repository;

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
}
