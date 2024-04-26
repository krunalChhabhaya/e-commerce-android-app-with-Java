package com.example.trendz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.TextView;
import java.util.ArrayList;
import android.util.Log;
import androidx.core.util.PatternsCompat;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;

import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import com.google.firebase.firestore.DocumentSnapshot;

public class CheckoutActivity extends AppCompatActivity {
    private EditText fullNameEditText;
    private EditText addressEditText;
    private EditText cardNumberEditText;
    private EditText securityCodeEditText;
    private EditText validUntilEditText;
    private EditText phoneNumberEditText;
    private EditText cityEditText;
    private AutoCompleteTextView provinceAutoCompleteTextView;
    private EditText postalCodeEditText;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);

        db = FirebaseFirestore.getInstance();

        provinceAutoCompleteTextView = findViewById(R.id.provinceAutoCompleteTextView);

        String[] canadianProvinces = {
                "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador",
                "Nova Scotia", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Northwest Territories",
                "Nunavut", "Yukon"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                canadianProvinces
        );

        provinceAutoCompleteTextView.setAdapter(adapter);

        provinceAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProvince = (String) parent.getItemAtPosition(position);
        });

        Intent cartIntent = getIntent();
        if (cartIntent != null) {
            String totalPayableAmount = cartIntent.getStringExtra("TOTAL_PAYABLE_AMOUNT");

            TextView totalPayableTextView = findViewById(R.id.totalPayableTextView);
            totalPayableTextView.setText(totalPayableAmount);

            fullNameEditText = findViewById(R.id.fullNameEditText);
            addressEditText = findViewById(R.id.addressEditText);
            cardNumberEditText = findViewById(R.id.cardNumberEditText);
            securityCodeEditText = findViewById(R.id.securityCodeEditText);
            validUntilEditText = findViewById(R.id.validUntilEditText);
            phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
            cityEditText = findViewById(R.id.cityEditText);
            provinceAutoCompleteTextView = findViewById(R.id.provinceAutoCompleteTextView);
            postalCodeEditText = findViewById(R.id.postalCodeEditText);

            findViewById(R.id.checkoutButton).setOnClickListener(view -> {
                if (validateForm()) {
                    saveOrderDetailsToFirestore(totalPayableAmount);
                }
            });
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (!isValidFullName(fullNameEditText.getText().toString().trim())) {
            setInvalidField(fullNameEditText);
            isValid = false;
        } else {
            setValidField(fullNameEditText);
        }

        if (!isValidAddress(addressEditText.getText().toString().trim())) {
            setInvalidField(addressEditText);
            isValid = false;
        } else {
            setValidField(addressEditText);
        }

        if (!isValidCardNumber(cardNumberEditText.getText().toString().trim())) {
            setInvalidField(cardNumberEditText);
            isValid = false;
        } else {
            setValidField(cardNumberEditText);
        }

        if (!isValidSecurityCode(securityCodeEditText.getText().toString().trim())) {
            setInvalidField(securityCodeEditText);
            isValid = false;
        } else {
            setValidField(securityCodeEditText);
        }

        if (!isValidValidUntil(validUntilEditText.getText().toString().trim())) {
            setInvalidField(validUntilEditText);
            isValid = false;
        } else {
            setValidField(validUntilEditText);
        }

        if (!isValidPhoneNumber(phoneNumberEditText.getText().toString().trim())) {
            setInvalidField(phoneNumberEditText);
            isValid = false;
        } else {
            setValidField(phoneNumberEditText);
        }

        if (!isValidCity(cityEditText.getText().toString().trim())) {
            setInvalidField(cityEditText);
            isValid = false;
        } else {
            setValidField(cityEditText);
        }

        if (!isValidProvince(provinceAutoCompleteTextView.getText().toString().trim())) {
            setInvalidField(provinceAutoCompleteTextView);
            isValid = false;
        } else {
            setValidField(provinceAutoCompleteTextView);
        }

        if (!isValidPostalCode(postalCodeEditText.getText().toString().trim())) {
            setInvalidField(postalCodeEditText);
            isValid = false;
        } else {
            setValidField(postalCodeEditText);
        }

        return isValid;
    }

    private boolean isValidFullName(String fullName) {
        return fullName.matches("^[a-zA-Z\\s]+$");
    }

    private boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{10}$");
    }

    private boolean isValidCity(String city) {
        return city.matches("^[a-zA-Z]+$");
    }

    private boolean isValidProvince(String province) {
        String[] canadianProvinces = {
                "Alberta", "British Columbia", "Manitoba", "New Brunswick", "Newfoundland and Labrador",
                "Nova Scotia", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Northwest Territories",
                "Nunavut", "Yukon"
        };

        for (String provinceName : canadianProvinces) {
            if (provinceName.equalsIgnoreCase(province)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidPostalCode(String postalCode) {
        return postalCode.matches("^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$");
    }

    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches("^\\d{13,16}$");
    }

    private boolean isValidSecurityCode(String securityCode) {
        return securityCode.matches("^\\d{3}$");
    }

    private boolean isValidValidUntil(String validUntil) {
        return validUntil.matches("^\\d{4}$");
    }

    private void setInvalidField(EditText editText) {
        if (editText.getParent().getParent() instanceof TextInputLayout) {
            ((TextInputLayout) editText.getParent().getParent()).setError("Invalid input");
        }
    }

    private void setValidField(EditText editText) {
        if (editText.getParent().getParent() instanceof TextInputLayout) {
            ((TextInputLayout) editText.getParent().getParent()).setError(null);
        }
    }

    private void saveOrderDetailsToFirestore(String totalPayableAmount) {
        String fullName = fullNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String cardNumber = cardNumberEditText.getText().toString().trim();
        String securityCode = securityCodeEditText.getText().toString().trim();
        String validUntil = validUntilEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String province = provinceAutoCompleteTextView.getText().toString().trim();
        String postalCode = postalCodeEditText.getText().toString().trim();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            getCartItemsFromFirestore(cartItems -> {
                Order order = new Order(fullName, address, cardNumber, securityCode, validUntil, totalPayableAmount, phoneNumber, city, province, postalCode, cartItems, userID);

                order.setUserID(userID);

                db.collection("orders")
                        .add(order)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                            deleteCartsCollection();
                            redirectToFragment();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CheckoutActivity.this, "Error placing order. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e("CheckoutActivity", "Error adding order details: " + e.getMessage());
                            e.printStackTrace();
                        });
            });
        } else {
            Log.d("CheckoutActivity", "No user is currently logged in.");
        }
    }

    private void deleteCartsCollection() {
        db.collection("carts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Log.d("CheckoutActivity", "Carts collection deleted.");
                    } else {

                    }
                });
    }

    private void getCartItemsFromFirestore(final OnCartItemsLoadedListener listener) {
        List<CartItem> cartItems = new ArrayList<>();

        db.collection("carts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            CartItem cartItem = document.toObject(CartItem.class);
                            cartItems.add(cartItem);
                            Log.d("CartItems", "CartItem: " + cartItem.toString());
                        }
                        listener.onCartItemsLoaded(cartItems);
                    } else {

                    }
                });
    }

    interface OnCartItemsLoadedListener {
        void onCartItemsLoaded(List<CartItem> cartItems);
    }
    private void redirectToFragment() {
        Intent intent = new Intent(CheckoutActivity.this, ProductActivity.class);
        intent.putExtra("REDIRECT_TO_ORDERS", true);
        startActivity(intent);
        finish();
    }
}
