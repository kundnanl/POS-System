package main.java.views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
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

    private MainController mainController;

    private Connection connection;

    public void initialize() {
        
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/POS", "root",
                    "mysql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setFetchedProducts(ObservableList<Product> fetchedProducts) {
        productTableView.setItems(fetchedProducts);
        ;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public Product getSelectedProduct() {
        return productTableView.getSelectionModel().getSelectedItem();
        // Fetch product details from your products list or database using productId
    }

    public void updateCart(ObservableList<Product> cartProducts) {
        productTableView.getItems().clear();
        productTableView.getItems().addAll(cartProducts);
    }

    public void setAddToCartButtonHandler(Runnable handler) {
        addToCartButton.setOnAction(event -> handler.run());
    }

    public void setRemoveFromCartButtonHandler(Runnable handler) {
        removeFromCartButton.setOnAction(event -> handler.run());
    }

    public void setCheckoutButtonHandler(Runnable handler) {
        checkoutButton.setOnAction(event -> handler.run());
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

    public void updateCartFromDatabase() {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT product_id, quantity FROM cart");
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<Product> cartProducts = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String productId = resultSet.getString("product_id");
                int quantity = resultSet.getInt("quantity");

                Product product = MainController.getProductByIdFromList(productId); // Implement this method
                cartProducts.add(product);
            }

            updateCart(cartProducts);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
