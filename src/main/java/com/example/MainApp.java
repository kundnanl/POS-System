package main.java.com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import main.java.model.Customer;
import main.java.model.Order;
import main.java.model.Product;
import main.java.views.CustomerDialog;
import main.java.views.MainView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.java.controller.MainController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class MainApp extends Application {
    private Connection connection;

    @Override
    public void start(Stage primaryStage) throws SQLException {
        // Show the customer dialog and obtain customer details

        try {
            Customer cust = new Customer("", "");
            System.out.println("Loading the fxml file");
            // Load the FXML file for the main view
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/MainView.fxml"));
            Parent root = loader.load();
            System.out.println(" fxml file loaded: " + loader.toString());

            // Set up the database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/POS", "root", "mysql");

            // Fetch the list of products from the database
            ObservableList<Product> productList = fetchProductsFromDatabase();

            // Create the MainController and pass necessary data
            MainController mainController = new MainController(productList, cust);
            MainView mainView = loader.getController();
            Order neworder = new Order(cust, mainView);

            // Set up the MainView
            mainView.setFetchedProducts(productList);
            mainView.setFetchedCart(neworder.getCartProducts());
            mainController.setMainView(mainView);

            // Set button handlers
            mainView.setAddToCartButtonHandler(() -> {
                Product selectedProduct = mainView.getSelectedProductFromProducts();
                if (selectedProduct != null) {
                    mainController.addToCart(selectedProduct);
                } else {
                    mainView.showErrorMessage("Please select a product to add to cart.");
                }
            });

            mainView.setClearCartButtonHandler(() -> {
                mainController.clearCart();
            });

            mainView.setRemoveFromCartButtonHandler(() -> {
                Product cartProduct = mainView.getSelectedProductFromCart();
                if (cartProduct != null) {
                    handleRemoveFromCart(mainController, mainView, cartProduct);
                } else {
                    mainView.showErrorMessage("Please select a product to remove from cart.");
                }
            });

            mainView.setCheckoutButtonHandler(() -> {
                boolean proceedWithCheckout = MainView.showCheckoutConfirmation();
                if (proceedWithCheckout) {
                    CustomerDialog customerDialog = new CustomerDialog();
                    Optional<Customer> customerResult = customerDialog.showAndWait();

                    if (customerResult.isPresent()) {
                        Customer customer = customerResult.get();
                        String customerInfo = customer.getName() + "\nEmail: " + customer.getEmail();

                        ObservableList<Product> cartItems = neworder.getCartProducts();
                        mainController.clearCart(); // Clear the cart after checkout
                        mainView.updateCartTableView(cartItems); // Update the cart table view
                        mainView.updateTotalLabels(); // Update the total labels
                        mainView.showCheckoutDialog(customerInfo, cartItems);
                    }
                }

            });

            // Set up the primary stage
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/main/java/resources/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("POS System");
            primaryStage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.print("Error Loading File");
        }
    }

    private ObservableList<Product> fetchProductsFromDatabase() throws SQLException {
        ObservableList<Product> productList = FXCollections.observableArrayList();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            double price = resultSet.getDouble("price");
            int quantity = resultSet.getInt("quantity");

            Product product = new Product(id, name, price, quantity);
            productList.add(product);
        }
        return productList;
    }

    private void handleRemoveFromCart(MainController mainController, MainView mainView, Product cartProduct) {
        Product product = mainView.getSelectedProductFromCart();
        if (product != null) {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Remove from Cart");
            dialog.setHeaderText("Enter quantity to remove from cart:");
            dialog.setContentText("Quantity:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    int quantityToRemove = Integer.parseInt(result.get());
                    if (quantityToRemove > 0 && quantityToRemove <= cartProduct.getQuantity()) {
                        mainController.removeFromCart(product, quantityToRemove);
                        System.out.println(
                                "Removed " + quantityToRemove + " of " + cartProduct.getName() + " from cart.");
                    } else if (quantityToRemove <= 0) {
                        mainView.showErrorMessage("Please enter a valid quantity to remove.");
                    } else {
                        mainView.showErrorMessage("You are trying to remove more than available in the cart.");
                    }
                } catch (NumberFormatException e) {
                    mainView.showErrorMessage("Please enter a valid number.");
                }
            }
        } else {
            mainView.showErrorMessage("Please select a product to remove from cart.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
