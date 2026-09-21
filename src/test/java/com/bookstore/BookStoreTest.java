package com.bookstore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BookStoreTest {

    @Test
    void testAddAndSearchBook() {
        BookStore store = new BookStore();

        Book book = new Book(
                "B101",
                "Java Programming",
                "James Gosling",
                650.0,
                10
        );

        store.addBook(book);

        Book result = store.searchBook("B101");

        assertNotNull(result);
        assertEquals("Java Programming", result.getTitle());
        assertEquals(650.0, result.getPrice());
    }

    @Test
    void testSearchBookNotFound() {
        BookStore store = new BookStore();

        Book result = store.searchBook("B999");

        assertNull(result);
    }

    @Test
    void testUpdateBook() {
        BookStore store = new BookStore();

        store.addBook(new Book(
                "B101",
                "Java Programming",
                "James Gosling",
                650.0,
                10
        ));

        boolean updated = store.updateBook(
                "B101",
                "Advanced Java",
                "James Gosling",
                750.0,
                8
        );

        assertTrue(updated);

        Book book = store.searchBook("B101");

        assertEquals("Advanced Java", book.getTitle());
        assertEquals(750.0, book.getPrice());
        assertEquals(8, book.getQuantity());
    }

    @Test
    void testDeleteBook() {
        BookStore store = new BookStore();

        store.addBook(new Book(
                "B101",
                "Java Programming",
                "James Gosling",
                650.0,
                10
        ));

        boolean deleted = store.deleteBook("B101");

        assertTrue(deleted);
        assertNull(store.searchBook("B101"));
    }

    @Test
    void testBuyBook() {
        BookStore store = new BookStore();

        store.addBook(new Book(
                "B101",
                "Java Programming",
                "James Gosling",
                650.0,
                10
        ));

        double total = store.buyBook("B101", 2);

        assertEquals(1300.0, total);

        Book book = store.searchBook("B101");

        assertEquals(8, book.getQuantity());
    }

    @Test
    void testBuyBookInsufficientStock() {
        BookStore store = new BookStore();

        store.addBook(new Book(
                "B101",
                "Java Programming",
                "James Gosling",
                650.0,
                2
        ));

        double total = store.buyBook("B101", 5);

        assertEquals(-1, total);

        Book book = store.searchBook("B101");

        assertEquals(2, book.getQuantity());
    }
}