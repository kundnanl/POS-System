package main.java.utils;

import java.text.DecimalFormat;

public class Utils {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.00");

    public static String formatCurrency(double amount) {
        return "$" + DECIMAL_FORMAT.format(amount);
    }
}