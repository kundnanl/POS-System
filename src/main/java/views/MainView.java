package main.java.views;

import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.controller.MainController;
import main.java.model.*;

public class MainView {
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> productIdColumn;
    @FXML
    private TableColumn<Product, String> productNameColumn;
    @FXML
    private TableColumn<Product, Double> productPriceColumn;
    @FXML
    private TableColumn<Product, Integer> productQuantityColumn;
    @FXML
    private Button addToCartButton;
    @FXML
    private Button removeFromCartButton;
    @FXML
    private Button checkoutButton;
    @FXML
    private Button clearCartButton;
    @FXML
    private Button exitButton;
    @FXML
    private TableView<Product> cartTableView;
    @FXML
    private TableColumn<Product, String> cartProductNameColumn;
    @FXML
    private TableColumn<Product, Double> cartProductPriceColumn;
    @FXML
    private TableColumn<Product, Integer> cartProductQuantityColumn;

    public void initialize() {

        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        cartProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cartProductPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartProductQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        try {
            DriverManager.getConnection("jdbc:mysql://localhost:3306/POS", "root",
                    "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        productTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Selected product: " + newValue);
            }
        });

    }

    public void setFetchedProducts(ObservableList<Product> fetchedProducts) {
        productTableView.setItems(fetchedProducts);
        ;
    }

    public void setMainController(MainController mainController) {
    }

    public Product getSelectedProduct() {
        return productTableView.getSelectionModel().getSelectedItem();
    }

    public void updateCart(ObservableList<Product> cartProducts) {
        productTableView.getItems().clear();
        productTableView.getItems().addAll(cartProducts);
    }

    public void setRemoveFromCartButtonHandler(Runnable handler) {
        removeFromCartButton.setOnAction(event -> handler.run());
    }

    public void setCheckoutButtonHandler(Runnable handler) {
        checkoutButton.setOnAction(event -> handler.run());
    }

    public void setAddToCartButtonHandler(Runnable handler) {
        addToCartButton.setOnAction(event -> {
            Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                handler.run();
                System.out.println("Product added to cart: " + selectedProduct.getName());
            } else {
                showErrorMessage("Please select a product to add to cart.");
            }
        });
    }

    public void setClearCartButtonHandler(Runnable handler) {
        clearCartButton.setOnAction(event -> handler.run());
    }

    public void setExitButtonHandler(Runnable handler) {
        exitButton.setOnAction(event -> handler.run());
    }

    public void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Product getSelectedProductFromProducts() {
        return productTableView.getSelectionModel().getSelectedItem();
    }

    public void setCartProducts(ObservableList<Product> cartProducts) {
        cartTableView.setItems(cartProducts);
    }

    public void updateCartTableView(ObservableList<Product> cartProducts) {
        cartTableView.setItems(cartProducts);
    }
    public void updateProductTableView(ObservableList<Product> productProducts) {
        productTableView.setItems(productProducts);
    }
    
}
