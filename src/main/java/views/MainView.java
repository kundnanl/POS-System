package main.java.views;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setProducts(ObservableList<Product> products) {
        productTableView.setItems(products);
    }

    public Product getSelectedProduct() {
        return productTableView.getSelectionModel().getSelectedItem();
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
}
