package main.java.model;

import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private String email;

    public Customer(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
