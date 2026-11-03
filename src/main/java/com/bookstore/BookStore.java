package com.bookstore;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class BookStore {
    private final BookRepository repository;

    public BookStore(BookRepository repository) {
        this.repository = repository;
    }

    public Book addBook(Book book) {
        validateBook(book);
        String id = book.getId().trim();
        if (repository.findAll().stream().anyMatch(existing -> existing.getId().equalsIgnoreCase(id))) {
            throw new DuplicateBookException("A book with ID '" + id + "' already exists.");
        }
        book.setId(id);
        return repository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) return getAllBooks();
        String term = query.trim().toLowerCase(Locale.ROOT);
        return getAllBooks().stream()
                .filter(book -> book.getId().toLowerCase(Locale.ROOT).contains(term)
                        || book.getTitle().toLowerCase(Locale.ROOT).contains(term)
                        || book.getAuthor().toLowerCase(Locale.ROOT).contains(term))
                .toList();
    }

    @Transactional(readOnly = true)
    public Book searchBook(String id) {
        if (id == null || id.isBlank()) return null;
        String normalizedId = id.trim();
        return repository.findAll().stream()
                .filter(book -> book.getId().equalsIgnoreCase(normalizedId))
                .findFirst()
                .orElse(null);
    }

    public Book updateBook(String id, Book changes) {
        validateBook(changes);
        Book existing = repository.findById(requireId(id))
                .orElseThrow(() -> new BookNotFoundException("Book '" + id + "' was not found."));
        existing.setTitle(changes.getTitle());
        existing.setAuthor(changes.getAuthor());
        existing.setPrice(changes.getPrice());
        existing.setQuantity(changes.getQuantity());
        return repository.save(existing);
    }

    public void updateBook(String id, String title, String author, double price, int quantity) {
        updateBook(id, new Book(id, title, author, price, quantity));
    }

    public boolean deleteBook(String id) {
        String safeId = requireId(id);
        if (!repository.existsById(safeId)) return false;
        repository.deleteById(safeId);
        return true;
    }

    public PurchaseReceipt buyBook(String id, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Purchase quantity must be at least 1.");
        Book book = repository.findById(requireId(id))
                .orElseThrow(() -> new BookNotFoundException("Book '" + id + "' was not found."));
        if (quantity > book.getQuantity()) {
            throw new InsufficientStockException(
                    "Only " + book.getQuantity() + " copies are available.");
        }
        BigDecimal total = book.getPrice().multiply(BigDecimal.valueOf(quantity));
        book.setQuantity(book.getQuantity() - quantity);
        repository.save(book);
        return new PurchaseReceipt(book.getId(), book.getTitle(), quantity, total, book.getQuantity());
    }

    private static String requireId(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Book ID is required.");
        return id.trim();
    }

    private static void validateBook(Book book) {
        if (book == null) throw new IllegalArgumentException("Book details are required.");
        if (book.getId() == null || book.getId().isBlank()) throw new IllegalArgumentException("Book ID is required.");
        if (book.getTitle() == null || book.getTitle().isBlank()) throw new IllegalArgumentException("Title is required.");
        if (book.getAuthor() == null || book.getAuthor().isBlank()) throw new IllegalArgumentException("Author is required.");
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Price must be greater than zero.");
        if (book.getQuantity() < 0) throw new IllegalArgumentException("Stock cannot be negative.");
    }

    public record PurchaseReceipt(String bookId, String title, int quantity,
                                  BigDecimal total, int remainingStock) {}
}
