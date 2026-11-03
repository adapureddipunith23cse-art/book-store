package com.bookstore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @NotBlank(message = "Book ID is required")
    @Size(max = 30, message = "Book ID must be 30 characters or fewer")
    @Column(nullable = false, unique = true, length = 30)
    private String id;

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be 150 characters or fewer")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author must be 100 characters or fewer")
    @Column(nullable = false, length = 100)
    private String author;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private int quantity;

    public Book() {}

    public Book(String id, String title, String author, BigDecimal price, int quantity) {
        this.id = clean(id);
        this.title = clean(title);
        this.author = clean(author);
        this.price = price;
        this.quantity = quantity;
    }

    public Book(String id, String title, String author, double price, int quantity) {
        this(id, title, author, BigDecimal.valueOf(price), quantity);
    }

    private static String clean(String value) {
        return value == null ? null : value.trim();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = clean(id); }
    public void setTitle(String title) { this.title = clean(title); }
    public void setAuthor(String author) { this.author = clean(author); }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setPrice(double price) { this.price = BigDecimal.valueOf(price); }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "ID: " + id + ", Title: " + title + ", Author: " + author
                + ", Price: ₹" + price + ", Quantity: " + quantity;
    }
}
