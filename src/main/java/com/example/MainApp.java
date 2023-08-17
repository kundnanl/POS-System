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
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("Loading the fxml file");
            // Load the FXML file for the main view
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/java/views/MainView.fxml"));
            Parent root = loader.load();
            System.out.println(" fxml file loaded: " + loader.toString());

            // Create the list of products
            List<Product> productList = new ArrayList<>();
            productList.add(new Product("Product 1", 10.0, 100));
            productList.add(new Product("Product 2", 20.0, 50));
            // Add more products as needed

            // Create an ObservableList from the list of products
            ObservableList<Product> observableProductList = FXCollections.observableArrayList(productList);

            // Create a customer
            Customer customer = new Customer("John Doe");

            // Get the MainView controller
            // Create the MainController and pass the MainView and the list of products to it
            MainController mainController = new MainController(observableProductList, customer);

            // Set up the primary stage
            Scene scene = new Scene(root, 800, 600);
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
