package main.java.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private Customer customer;
    private Map<Product, Integer> productQuantityMap;

    public Order(Customer customer) {
        this.customer = customer;
        this.productQuantityMap = new HashMap<>();
    }

    public Customer getCustomer() {
        return customer;
    }

    public ObservableList<Product> getProducts() {
        return FXCollections.observableArrayList(productQuantityMap.keySet());
    }

    public void addProduct(Product product, int quantity) {
        int currentQuantity = productQuantityMap.getOrDefault(product, 0);
        productQuantityMap.put(product, currentQuantity + quantity);
    }

    public void removeProduct(Product product, int quantity) {
        int currentQuantity = productQuantityMap.getOrDefault(product, 0);
        int newQuantity = Math.max(currentQuantity - quantity, 0);
        if (newQuantity == 0) {
            productQuantityMap.remove(product);
        } else {
            productQuantityMap.put(product, newQuantity);
        }
    }

    public int getProductQuantity(Product product) {
        return productQuantityMap.getOrDefault(product, 0);
    }

    public void clear() {
        productQuantityMap.clear();
    }
}
