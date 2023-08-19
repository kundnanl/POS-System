package main.java.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import main.java.model.Customer;
import main.java.model.Order;
import main.java.model.Product;
import main.java.views.MainView;

public class MainController {
    private Connection connection;
    private static MainView mainView;
    private static ObservableList<Product> products;
    private static Order currentOrder;
    private Customer customer;
    private ObservableList<Product> cartProducts = FXCollections.observableArrayList();

    public MainController() {

    }

    public MainController(ObservableList<Product> products, Customer customer) {
        MainController.products = products;
        this.customer = customer;
        currentOrder = new Order(customer);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/POS", "root", "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/MainView.fxml"));
        try {
            loader.load();
            mainView = loader.getController();
            mainView.setMainController(this);

            mainView.updateCart(currentOrder.getProducts());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public ObservableList<Product> getCartProducts() {
        return cartProducts;
    }

    public void addToCart(Product product) {
        currentOrder.addProduct(product, 1); 
        mainView.updateCartTableView(currentOrder.getProducts());
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
        try {
            PreparedStatement deleteCartItemsStatement = connection.prepareStatement("DELETE FROM cart");
            deleteCartItemsStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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