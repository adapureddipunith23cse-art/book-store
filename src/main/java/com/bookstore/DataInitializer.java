package com.bookstore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    private final BookRepository repository;

    public DataInitializer(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.save(new Book("B101", "Java Programming", "James Gosling",
                    new BigDecimal("650.00"), 10));
            repository.save(new Book("B102", "Python Basics", "Mark Lutz",
                    new BigDecimal("550.00"), 5));
            repository.save(new Book("B103", "Clean Code", "Robert C. Martin",
                    new BigDecimal("799.00"), 3));
        }
    }
}
