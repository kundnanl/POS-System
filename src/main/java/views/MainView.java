package main.java.views;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.controller.CheckoutDialogController;
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
    public TableView<Product> cartTableView;
    @FXML
    private TableColumn<Product, String> cartProductNameColumn;
    @FXML
    private TableColumn<Product, Double> cartProductPriceColumn;
    @FXML
    private TableColumn<Product, Integer> cartProductQuantityColumn;
    @FXML
    private Label totalItemsLabel;
    @FXML
    private Label subTotalLabel;
    @FXML
    private Label totalWithTaxesLabel;

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
    }

    public void setFetchedCart(ObservableList<Product> fetchedProducts) {
        cartTableView.setItems(fetchedProducts);
    }

    public void setMainController(MainController mainController) {
    }

    public void updateCart(ObservableList<Product> cartProducts) {
        cartTableView.getItems().clear();
    }

    public void setCheckoutButtonHandler(Runnable handler) {
        checkoutButton.setOnAction(event -> {
            handler.run();
            performCheckout();
        });
    }

    public void updateTotalLabels() {
        ObservableList<Product> cartProducts = cartTableView.getItems();
        MainController mainController = new MainController();

        int totalItems = mainController.getTotalItems(cartProducts);
        double subTotal = mainController.getSubTotal(cartProducts);
        double totalWithTaxes = mainController.getTotalWithTaxes(cartProducts);

        totalItemsLabel.setText(String.valueOf(totalItems));
        subTotalLabel.setText(String.format("%.2f", subTotal));
        totalWithTaxesLabel.setText(String.format("%.2f", totalWithTaxes));
    }

    private void performCheckout() {
            cartTableView.getItems().clear();
            updateTotalLabels();
    }

    public void setAddToCartButtonHandler(Runnable handler) {
        addToCartButton.setOnAction(event -> {
            Product selectedProduct = productTableView.getSelectionModel().getSelectedItem();
            handler.run();
            updateTotalLabels();
            System.out.println("Product added to cart: " + selectedProduct.getName());
        });
    }

    public void setRemoveFromCartButtonHandler(Runnable handler) {
        removeFromCartButton.setOnAction(event -> {
            Product cartProduct = cartTableView.getSelectionModel().getSelectedItem();
            if (cartProduct != null) {
                handler.run();
                updateTotalLabels();
            } else {
                showErrorMessage("Please select a product to remove from cart.");
            }
        });
    }

    public void setClearCartButtonHandler(Runnable handler) {
        clearCartButton.setOnAction(event -> {
            handler.run();
        });
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

    public Product getSelectedProductFromCart() {
        return cartTableView.getSelectionModel().getSelectedItem();
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

    public static boolean showCheckoutConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Checkout Confirmation");
        alert.setHeaderText("Are you sure you want to check out?");
        alert.setContentText("This will generate a bill and clear the cart.");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

public void showCheckoutDialog(String customerInfo, ObservableList<Product> cartItems) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/CheckoutDialog.fxml"));
        DialogPane dialogPane = loader.load();
        
        // Get the controller from the loader
        CheckoutDialogController controller = loader.getController();
        controller.initializeData(customerInfo, cartItems);
        // Create a new Dialog and set the content
        Dialog<Void> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public void clearCartTableView() {
        cartTableView.getItems().clear();
    }

}