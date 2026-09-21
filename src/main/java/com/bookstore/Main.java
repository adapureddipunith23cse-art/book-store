package com.bookstore;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        BookStore store = new BookStore();

        // Sample books
        store.addBook(
                new Book("B101", "Java Programming",
                        "James Gosling", 650, 10));

        store.addBook(
                new Book("B102", "Python Basics",
                        "Mark Lutz", 550, 5));

        int choice;

        do {

            System.out.println("\n====================================");
            System.out.println("     BOOK STORE MANAGEMENT SYSTEM");
            System.out.println("====================================");

            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Buy Book");
            System.out.println("7. Exit");

            System.out.print("\nEnter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter Book ID: ");
                    String id = scanner.nextLine();

                    System.out.print("Enter Title: ");
                    String title = scanner.nextLine();

                    System.out.print("Enter Author: ");
                    String author = scanner.nextLine();

                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();

                    System.out.print("Enter Quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();

                    Book book = new Book(
                            id,
                            title,
                            author,
                            price,
                            quantity
                    );

                    store.addBook(book);

                    System.out.println("Book added successfully!");

                    break;

                case 2:

                    System.out.println("\n------ ALL BOOKS ------");

                    for (Book b : store.getAllBooks()) {
                        System.out.println(b);
                    }

                    break;

                case 3:

                    System.out.print("Enter Book ID: ");
                    String searchId = scanner.nextLine();

                    Book foundBook = store.searchBook(searchId);

                    if (foundBook != null) {
                        System.out.println("\nBook Found!");
                        System.out.println(foundBook);
                    } else {
                        System.out.println("Book not found.");
                    }

                    break;

                case 4:

                    System.out.print("Enter Book ID: ");
                    String updateId = scanner.nextLine();

                    System.out.print("Enter New Title: ");
                    String newTitle = scanner.nextLine();

                    System.out.print("Enter New Author: ");
                    String newAuthor = scanner.nextLine();

                    System.out.print("Enter New Price: ");
                    double newPrice = scanner.nextDouble();

                    System.out.print("Enter New Quantity: ");
                    int newQuantity = scanner.nextInt();
                    scanner.nextLine();

                    boolean updated = store.updateBook(
                            updateId,
                            newTitle,
                            newAuthor,
                            newPrice,
                            newQuantity
                    );

                    if (updated) {
                        System.out.println("Book updated successfully!");
                    } else {
                        System.out.println("Book not found.");
                    }

                    break;

                case 5:

                    System.out.print("Enter Book ID: ");
                    String deleteId = scanner.nextLine();

                    boolean deleted = store.deleteBook(deleteId);

                    if (deleted) {
                        System.out.println("Book deleted successfully!");
                    } else {
                        System.out.println("Book not found.");
                    }

                    break;

                case 6:

                    System.out.print("Enter Book ID: ");
                    String buyId = scanner.nextLine();

                    System.out.print("Enter Quantity: ");
                    int buyQuantity = scanner.nextInt();
                    scanner.nextLine();

                    double total = store.buyBook(
                            buyId,
                            buyQuantity
                    );

                    if (total >= 0) {

                        System.out.println(
                                "Purchase successful!"
                        );

                        System.out.println(
                                "Total Price: ₹" + total
                        );

                    } else {

                        System.out.println(
                                "Purchase failed. Check book ID or stock."
                        );
                    }

                    break;

                case 7:

                    System.out.println(
                            "Thank you for using Book Store!"
                    );

                    break;

                default:

                    System.out.println(
                            "Invalid choice!"
                    );
            }

        } while (choice != 7);

        scanner.close();
    }
}   