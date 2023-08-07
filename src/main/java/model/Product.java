package main.java.model;

import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.id = UUID.randomUUID(); // Generating a unique ID for each product
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters for the fields

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Additional Methods

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
        } else {
            System.out.println("Not enough quantity available.");
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
