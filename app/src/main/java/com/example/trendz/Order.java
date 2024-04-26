package com.example.trendz;
import java.util.Map;
import java.util.List;

public class Order {
    private String fullName;
    private String address;
    private String cardNumber;
    private String securityCode;
    private String validUntil;
    private String totalPayableAmount;
    private String phoneNumber;
    private String city;
    private String province;
    private String postalCode;
    private List<CartItem> cartItems;
    private String userID;

    public Order() {
        // Required for Firebase deserialization
    }

    public Order(String fullName, String address, String cardNumber, String securityCode, String validUntil, String totalPayableAmount, String phoneNumber, String city, String province, String postalCode, List<CartItem> cartItems, String userID) {
        this.fullName = fullName;
        this.address = address;
        this.cardNumber = cardNumber;
        this.securityCode = securityCode;
        this.validUntil = validUntil;
        this.totalPayableAmount = totalPayableAmount;
        this.phoneNumber = phoneNumber;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.cartItems = cartItems;
        this.userID = userID;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(String totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}