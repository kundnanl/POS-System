package main.java.views;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import main.java.model.Customer;

public class CustomerDialog extends Dialog<Customer> {
    private TextField nameField = new TextField();
    private TextField emailField = new TextField();

    public CustomerDialog() {
        setTitle("Enter Customer Details");
        setHeaderText("Please enter customer details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Customer(nameField.getText(), emailField.getText());
            }
            return null;
        });
    }
}
