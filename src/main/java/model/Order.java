package main.java.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.views.MainView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private Customer customer;
    private Map<Product, Integer> productQuantityMap;
    private Connection connection;

    public Order(Customer customer) {
        this.customer = customer;
        this.productQuantityMap = new HashMap<>();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/POS", "root", "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public ObservableList<Product> getProducts() {
        return FXCollections.observableArrayList(productQuantityMap.keySet());
    }

    public void addProduct(Product product, int quantity) {
    int currentQuantity = productQuantityMap.getOrDefault(product, 0);
    productQuantityMap.put(product, currentQuantity + quantity);

    try {
        // Check if the product is already in the cart
        PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM cart WHERE product_id = ?");
        checkStatement.setString(1, product.getId().toString());
        ResultSet resultSet = checkStatement.executeQuery();

        if (resultSet.next()) {
            int existingQuantity = resultSet.getInt("quantity");
            quantity += existingQuantity;

            // Update the existing cart item
            PreparedStatement updateStatement = connection
                    .prepareStatement("UPDATE cart SET quantity = ? WHERE product_id = ?");
            updateStatement.setInt(1, quantity);
            updateStatement.setString(2, product.getId().toString());
            updateStatement.executeUpdate();
        } else {
            // Insert the new cart item
            PreparedStatement insertStatement = connection
                    .prepareStatement("INSERT INTO cart (product_id, quantity) VALUES (?, ?)");
            insertStatement.setString(1, product.getId().toString());
            insertStatement.setInt(2, quantity);
            insertStatement.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    public void removeProduct(Product product, int quantity) {
        int currentQuantity = productQuantityMap.getOrDefault(product, 0);
        int newQuantity = Math.max(currentQuantity - quantity, 0);
        if (newQuantity == 0) {
            productQuantityMap.remove(product);
        } else {
            productQuantityMap.put(product, newQuantity);
        }
    }

    public int getProductQuantity(Product product) {
        return productQuantityMap.getOrDefault(product, 0);
    }

    public void clear() {
        productQuantityMap.clear();
    }
}
