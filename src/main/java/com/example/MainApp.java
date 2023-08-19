package main.java.com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.model.Customer;
import main.java.model.Product;
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

public class MainApp extends Application {
    private Connection connection;

        @Override
    public void start(Stage primaryStage) throws SQLException {
        try {
            System.out.println("Loading the fxml file");
            // Load the FXML file for the main view
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/MainView.fxml"));
            Parent root = loader.load();
            System.out.println(" fxml file loaded: " + loader.toString());

            // Create the list of products
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/POS", "root",
                        "mysql");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM product");
            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<Product> productList = FXCollections.observableArrayList();

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                // Fetch product details from your products list or database using productId
                Product product = new Product(name, price, quantity);
                productList.add(product);
            }
            System.out.println("Fetched Products:");
            for (Product product : productList) {
                System.out.println(product);
            }
            
            // Create a customer
            Customer customer = new Customer("John Doe","");

            // Get the MainView controller
            // Create the MainController and pass the MainView and the list of products to
            // it
            MainController mainController = new MainController(productList, customer);
            MainView mainView = loader.getController(); // Assuming you can access the MainView instance
            mainView.setFetchedProducts(productList); // Set the fetched products
            mainController.setMainView(mainView);
            mainView.setAddToCartButtonHandler(() -> {
                Product selectedProduct = mainView.getSelectedProduct();
                if (selectedProduct != null) {
                    mainController.addToCart(selectedProduct);
                } else {
                    mainView.showErrorMessage("Please select a product to add to cart.");
                }
            });
                        
            // Set up the primary stage
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/main/java/resources/styles.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.setTitle("POS System");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.print("Error Loading File");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}