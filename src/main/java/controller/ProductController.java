package main.java.controller;


import javafx.collections.ObservableList;
import main.java.model.*;

public class ProductController {
    private ObservableList<Product> products;

    public ProductController(ObservableList<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }
}
