package main.java.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.views.MainView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private Customer customer;
    private Map<Product, Integer> productQuantityMap;
    private Connection connection;
    public Order(Customer customer, MainView mainView) {
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

    public ObservableList<Product> getCartProducts() {
        ObservableList<Product> cartProducts = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM cart");

            while (resultSet.next()) {
                String productId = resultSet.getString("product_id");
                int quantity = resultSet.getInt("quantity");

                // Fetch the product details from the 'products' table using the productId
                Product product = getProductFromDatabase(productId);

                // Set the quantity of the product based on the cart entry
                product.setQuantity(quantity);

                cartProducts.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cartProducts;
    }

    public ObservableList<Product> getProductProducts() {
        ObservableList<Product> productProducts = FXCollections.observableArrayList();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                productProducts.add(new Product(id, name, price, quantity));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Product product : productProducts) {
            System.out.println(product);
        }
        return productProducts;
    }

    // Fetches the product details from the 'products' table based on productId
    private Product getProductFromDatabase(String productId) {
        Product product = null;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product WHERE id = ?");
            preparedStatement.setString(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                product = new Product(id, name, price, quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    private int getAvailableQuantityFromDatabase(Product product) {
        int availableQuantity = 0;

        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT quantity FROM product WHERE id = ?");
            preparedStatement.setString(1, product.getId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                availableQuantity = resultSet.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return availableQuantity;
    }

    public void addProduct(Product product, int quantity) {
        int currentQuantity = getAvailableQuantityFromDatabase(product); // Fetch current available quantity

        if (currentQuantity > 0) {
            try {
                // Update product quantity in the database
                PreparedStatement updateProductQuantity = connection.prepareStatement(
                        "UPDATE product SET quantity = ? WHERE id = ?");
                updateProductQuantity.setInt(1, currentQuantity - 1);
                updateProductQuantity.setString(2, product.getId().toString());
                updateProductQuantity.executeUpdate();

                // Check if the product is already in the cart
                PreparedStatement checkStatement = connection.prepareStatement(
                        "SELECT * FROM cart WHERE product_id = ?");
                checkStatement.setString(1, product.getId().toString());
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    int existingQuantity = resultSet.getInt("quantity");
                    quantity += existingQuantity;

                    // Update the existing cart item
                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE cart SET quantity = ? WHERE product_id = ?");
                    updateStatement.setInt(1, quantity);
                    updateStatement.setString(2, product.getId().toString());
                    updateStatement.executeUpdate();
                } else {
                    // Insert the new cart item
                    PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO cart (product_id, quantity) VALUES (?, ?)");
                    insertStatement.setString(1, product.getId().toString());
                    insertStatement.setInt(2, quantity);
                    insertStatement.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Product is out of stock.");
        }
    }

    public void removeProduct(Product product, int quantityToRemove) {
        int availablequantity = getProductQuantity(product);
        System.out.println("Available Quantity: " + availablequantity);

        if (quantityToRemove == availablequantity) {
            productQuantityMap.remove(product);

            try {
                // Remove the product from the cart
                PreparedStatement deleteStatement = connection
                        .prepareStatement("DELETE FROM cart WHERE product_id = ?");
                deleteStatement.setString(1, product.getId().toString());
                deleteStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            productQuantityMap.put(product, quantityToRemove);

            try {
                // Update the quantity of the product in the cart
                PreparedStatement updateStatement = connection
                        .prepareStatement("UPDATE cart SET quantity = ? WHERE product_id = ?");
                updateStatement.setInt(1, availablequantity - quantityToRemove);
                updateStatement.setString(2, product.getId().toString());
                updateStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getProductQuantity(Product product) {
        int cartQuantity = productQuantityMap.getOrDefault(product, 0);
        int dbQuantity = 0;

        try {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT quantity FROM cart WHERE product_id = ?");
            preparedStatement.setString(1, product.getId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                dbQuantity = resultSet.getInt("quantity");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Math.max(cartQuantity, dbQuantity);
    }
    
    public void clearCart() {
        productQuantityMap.clear();
        
        try {
            // Clear the cart table in the database
            PreparedStatement clearCartStatement = connection.prepareStatement("DELETE FROM cart");
            clearCartStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void clear() {
        productQuantityMap.clear();
    }
}
