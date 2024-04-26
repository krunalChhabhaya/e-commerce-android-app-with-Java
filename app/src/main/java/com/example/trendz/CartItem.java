package com.example.trendz;

import java.io.Serializable;
import java.text.DecimalFormat;

public class CartItem implements Serializable {
    private String productName;
    private String size;
    private int quantity;
    private double price;
    private double totalPrice;
    private String imageFileName;
    private String userID;

    public CartItem() {
        // Required empty constructor for Firebase deserialization
    }
    public CartItem(String productName, String size, int quantity, double price, String imageFileName, String userID) {
        this.productName = productName;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.imageFileName = imageFileName;
        this.userID = userID;
        calculateTotalPrice();
    }
    public String getProductName() {
        return productName;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double calculateTotalPrice() {
        double totalPrice = quantity * price;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedTotalPrice = decimalFormat.format(totalPrice);

        this.totalPrice = Double.parseDouble(formattedTotalPrice);

        return this.totalPrice;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "productName='" + productName + '\'' +
                ", size='" + size + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
