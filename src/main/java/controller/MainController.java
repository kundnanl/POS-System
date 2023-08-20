package main.java.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
        mainView = new MainView();
        currentOrder = new Order(customer, mainView);

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
            mainView.updateCart(currentOrder.getCartProducts());
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
        mainView.updateCartTableView(currentOrder.getCartProducts());
        Order newOrder = new Order(customer, mainView);
        mainView.updateProductTableView(newOrder.getProductProducts());

    }

    public void removeFromCart(Product product, int quantityToRemove) {
        currentOrder.removeProduct(product, quantityToRemove);
        mainView.updateCartTableView(currentOrder.getCartProducts());
    }

    public void checkout() {
        if (currentOrder.getCartProducts().isEmpty()) {
            performCheckout();
            mainView.showErrorMessage("Cart is empty. Please add products before checking out.");
        }

        currentOrder = new Order(customer, mainView);
        mainView.updateCart(currentOrder.getCartProducts());
    }

    public void clearCart() {
        currentOrder.clearCart();
        mainView.updateCart(currentOrder.getCartProducts());
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
        MainController.mainView = mainView;
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
        return null;
    }

    // Method to calculate total items in the cartTableView
    public int getTotalItems(ObservableList<Product> cartProducts) {
        int totalItems = 0;
        for (Product product : cartProducts) {
            totalItems += product.getQuantity();
        }
        return totalItems;
    }

    private void generateBillAndClearCart() {
        System.out.println("Bill Generated..");
        // Create the content for the bill
        String customerInfo = "Customer Information:\n" +
                              "Name: " + customer.getName() + "\n" +
                              "Email: " + customer.getEmail();
    
        ObservableList<Product> cartItems = mainView.cartTableView.getItems();
    
        // Show the checkout dialog
        mainView.showCheckoutDialog(customerInfo, cartItems);
    
        // Clear the cartTableView and update total labels
        mainView.clearCartTableView();
        mainView.updateTotalLabels();
    
        // Clear the cartProducts list in the controller as well
        cartProducts.clear();
    }
    

    private void performCheckout() {
        if (MainView.showCheckoutConfirmation()) {    
            generateBillAndClearCart();
        }
    }
    
    // Method to calculate sub-total without taxes from cartTableView
    public double getSubTotal(ObservableList<Product> cartProducts) {
        double subTotal = 0;
        for (Product product : cartProducts) {
            subTotal += product.getPrice() * product.getQuantity();
        }
        return subTotal;
    }

    // Method to calculate total with taxes from cartTableView
    public double getTotalWithTaxes(ObservableList<Product> cartProducts) {
        double taxRate = 1.13; 
        double subTotal = getSubTotal(cartProducts);
        double taxes = subTotal * taxRate;
        return taxes;
    }
}