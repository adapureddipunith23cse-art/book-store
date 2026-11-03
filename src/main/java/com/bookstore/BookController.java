package com.bookstore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {
    private final BookStore store;

    public BookController(BookStore store) {
        this.store = store;
    }

    @GetMapping
    public List<Book> getBooks(@RequestParam(required = false) String search) {
        return store.searchBooks(search);
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable String id) {
        Book book = store.searchBook(id);
        if (book == null) throw new BookNotFoundException("Book '" + id + "' was not found.");
        return book;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@Valid @RequestBody Book book) {
        return store.addBook(book);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable String id, @Valid @RequestBody Book book) {
        return store.updateBook(id, book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable String id) {
        if (!store.deleteBook(id)) throw new BookNotFoundException("Book '" + id + "' was not found.");
    }

    @PostMapping("/{id}/purchase")
    public BookStore.PurchaseReceipt purchase(
            @PathVariable String id, @Valid @RequestBody PurchaseRequest request) {
        return store.buyBook(id, request.quantity());
    }

    public record PurchaseRequest(@Min(value = 1, message = "Quantity must be at least 1") int quantity) {}
}
