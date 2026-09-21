package com.bookstore;

import java.util.ArrayList;
import java.util.List;

public class BookStore {

    private List<Book> books = new ArrayList<>();

    // Add a book
    public void addBook(Book book) {
        books.add(book);
    }

    // Get all books
    public List<Book> getAllBooks() {
        return books;
    }

    // Search book by ID
    public Book searchBook(String id) {

        for (Book book : books) {

            if (book.getId().equalsIgnoreCase(id)) {
                return book;
            }
        }

        return null;
    }

    // Update book
    public boolean updateBook(
            String id,
            String title,
            String author,
            double price,
            int quantity) {

        Book book = searchBook(id);

        if (book != null) {

            book.setTitle(title);
            book.setAuthor(author);
            book.setPrice(price);
            book.setQuantity(quantity);

            return true;
        }

        return false;
    }

    // Delete book
    public boolean deleteBook(String id) {

        Book book = searchBook(id);

        if (book != null) {
            books.remove(book);
            return true;
        }

        return false;
    }

    // Buy book
    public double buyBook(String id, int quantity) {

        Book book = searchBook(id);

        if (book == null) {
            return -1;
        }

        if (quantity <= 0) {
            return -1;
        }

        if (quantity > book.getQuantity()) {
            return -1;
        }

        double total = book.getPrice() * quantity;

        book.setQuantity(book.getQuantity() - quantity);

        return total;
    }
}   