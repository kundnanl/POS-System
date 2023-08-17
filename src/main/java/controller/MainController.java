package main.java.controller;

import java.io.IOException;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import main.java.model.Customer;
import main.java.model.Order;
import main.java.model.Product;
import main.java.views.MainView;

public class MainController {
    private MainView mainView;
    private ObservableList<Product> products;
    private Order currentOrder;
    private Customer customer;

    public MainController() {

    }

    public MainController(ObservableList<Product> products, Customer customer) {
        this.products = products;
        this.customer = customer;
        currentOrder = new Order(customer);

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/MainView.fxml"));
    try {
        Parent root = loader.load();
        mainView = loader.getController(); // Initialize mainView
        mainView.setMainController(this); // Set the controller in MainView
        mainView.setProducts(products);

        mainView.updateCart(currentOrder.getProducts());
    } catch (IOException e) {
        e.printStackTrace();
    }
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

        currentOrder.addProduct(product, quantity);
        mainView.updateCart(currentOrder.getProducts());
    }

    public void removeFromCart(Product product, int quantity) {
        if (quantity <= 0) {
            mainView.showErrorMessage("Invalid quantity.");
            return;
        }

        int orderedQuantity = currentOrder.getProductQuantity(product);
        if (quantity > orderedQuantity) {
            mainView.showErrorMessage("Quantity exceeds the number of products in the cart.");
            return;
        }

        currentOrder.removeProduct(product, quantity);
        mainView.updateCart(currentOrder.getProducts());
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
        currentOrder.clear();
        mainView.updateCart(currentOrder.getProducts());
    }

    public void exit(Stage primaryStage) {
        primaryStage.close();
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;

    }
}