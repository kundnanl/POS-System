package main.java.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import main.java.model.Customer;
import main.java.model.Order;
import main.java.model.Product;
import main.java.views.MainView;

public class MainController {
    private Connection connection;
    private MainView mainView;
    private static ObservableList<Product> products;
    private Order currentOrder;
    private Customer customer;

    public MainController() {

    }

    public MainController(ObservableList<Product> products, Customer customer) {
        this.products = products;
        this.customer = customer;
        currentOrder = new Order(customer);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/POS", "root", "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/MainView.fxml"));
        try {
            Parent root = loader.load();
            mainView = loader.getController(); // Initialize mainView
            mainView.setMainController(this); // Set the controller in MainView

            mainView.updateCart(currentOrder.getProducts());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Connection getConnection() {
        return connection;
    }

    public void addToCart(Product product, int quantity) {
        if (quantity <= 0) {
            mainView.showErrorMessage("Invalid quantity.");
            return;
        }

        int availableQuantity = product.getQuantity();
        if (quantity > availableQuantity) {
            mainView.showErrorMessage("Not enough quantity available.");
            return;
        }

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

        // Update the cart view in your MainView
        mainView.updateCartFromDatabase();
    }
    // Rest of your method

    public void removeFromCart(Product product, int quantity) {
        if (quantity <= 0) {
            mainView.showErrorMessage("Invalid quantity.");
            return;
        }

        try {
            PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM cart WHERE product_id = ?");
            checkStatement.setString(1, product.getId().toString());
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                int existingQuantity = resultSet.getInt("quantity");
                if (quantity >= existingQuantity) {
                    // Remove the cart item completely if quantity is equal or more
                    PreparedStatement deleteStatement = connection
                            .prepareStatement("DELETE FROM cart WHERE product_id = ?");
                    deleteStatement.setString(1, product.getId().toString());
                    deleteStatement.executeUpdate();
                } else {
                    // Update the existing cart item
                    PreparedStatement updateStatement = connection
                            .prepareStatement("UPDATE cart SET quantity = ? WHERE product_id = ?");
                    updateStatement.setInt(1, existingQuantity - quantity);
                    updateStatement.setString(2, product.getId().toString());
                    updateStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Update the cart view in your MainView
        mainView.updateCartFromDatabase();
    }

    public void checkout() {
        if (currentOrder.getProducts().isEmpty()) {
            mainView.showErrorMessage("Cart is empty. Please add products before checking out.");
            return;
        }

        currentOrder = new Order(customer);
        mainView.updateCart(currentOrder.getProducts());
    }

    public void clearCart() {
        // Clear the cart in the database
        try {
            PreparedStatement deleteCartItemsStatement = connection.prepareStatement("DELETE FROM cart");
            deleteCartItemsStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Clear the cart in the application
        currentOrder.clear();
        mainView.updateCart(currentOrder.getProducts());
    }

    public void exit(Stage primaryStage) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        primaryStage.close();
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;

    }

    public static Product getProductByIdFromList(String productId) {
        for (Product product : products) {
            if (product.getId().toString().equals(productId)) {
                return product;
            }
        }
        return null; // Handle the case when the product is not found
    }
}