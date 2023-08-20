package main.java.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.model.Product;

public class CheckoutDialogController {
    @FXML
    private Label customerInfoLabel;
    @FXML
    private TableView<Product> cartItemsTableView;
    @FXML
    private TableColumn<Product, String> itemNameColumn;
    @FXML
    private TableColumn<Product, Double> itemPriceColumn;
    @FXML
    private TableColumn<Product, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<Product, Double> itemTotalColumn;
    @FXML
    private Label totalItemsLabel;
    @FXML
    private Label subTotalLabel;
    @FXML
    private Label totalWithTaxesLabel;

    public void initializeData(String customerInfo, ObservableList<Product> cartItems) {
        customerInfoLabel.setText(customerInfo);
        cartItemsTableView.setItems(cartItems);

        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        itemTotalColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            double total = product.getPrice() * product.getQuantity();
            return new SimpleDoubleProperty(total).asObject();
        });

        // Set up the total items label
        int totalItems = cartItemsTableView.getItems().size();
        totalItemsLabel.setText(Integer.toString(totalItems));

        // Calculate and display the subtotal
        MainController mainController = new MainController(); // Create or access your MainController instance
        double subTotal = mainController.getSubTotal(cartItemsTableView.getItems());
        subTotalLabel.setText(String.format("$%.2f", subTotal));

        // Calculate and display the total with taxes
        double totalWithTaxes = mainController.getTotalWithTaxes(cartItemsTableView.getItems());
        totalWithTaxesLabel.setText(String.format("$%.2f", totalWithTaxes));

    }
}
