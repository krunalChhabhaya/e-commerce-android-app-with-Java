package com.example.trendz;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import com.google.gson.Gson;

public class ProductDetailFragment extends Fragment {

    private int quantity = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        Product product = (Product) getArguments().getSerializable("selected_product");

        ImageView productDetailImageView = view.findViewById(R.id.productDetailImageView);
        TextView productDetailNameTextView = view.findViewById(R.id.productDetailNameTextView);
        TextView productDetailPriceTextView = view.findViewById(R.id.productDetailPriceTextView);
        TextView productDetailCategoryTextView = view.findViewById(R.id.productDetailCategoryTextView);
        TextView productDetailColorTextView = view.findViewById(R.id.productDetailColorTextView);
        TextView productDetailGenderTextView = view.findViewById(R.id.productDetailGenderTextView);
        TextView productDetailDescriptionTextView = view.findViewById(R.id.productDetailDescriptionTextView);
        RadioGroup sizeRadioGroup = view.findViewById(R.id.sizeRadioGroup);

        if (product != null) {
            String imageName = product.getImage();
            int imageResourceId = getResources().getIdentifier(imageName, "drawable", requireActivity().getPackageName());
            productDetailImageView.setImageResource(imageResourceId);

            productDetailNameTextView.setText(product.getName());
            productDetailPriceTextView.setText("Price: $" + String.valueOf(product.getPrice()));
            productDetailCategoryTextView.setText("Category: " + product.getCategory());
            productDetailColorTextView.setText("Color: " + product.getColor());
            productDetailGenderTextView.setText("Gender: " + product.getGender());
            productDetailDescriptionTextView.setText("Description: " + product.getDescription());

            List<String> sizes = product.getSizes();

            for (String size : sizes) {
                RadioButton radioButton = new RadioButton(requireContext());
                radioButton.setText(size);

                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

                sizeRadioGroup.addView(radioButton);
            }
        }

        TextView quantityTextView = view.findViewById(R.id.quantityTextView);
        Button minusButton = view.findViewById(R.id.minusButton);
        Button plusButton = view.findViewById(R.id.plusButton);

        quantityTextView.setText(String.valueOf(quantity));

        minusButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Minimum quantity of 1 required", Toast.LENGTH_SHORT).show();
            }
        });

        plusButton.setOnClickListener(v -> {
            if (quantity < 10) {
                quantity++;
                quantityTextView.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Maximum quantity of 10 reached", Toast.LENGTH_SHORT).show();
            }
        });

        Button addToCartButton = view.findViewById(R.id.addToCartButton);
        addToCartButton.setOnClickListener(v -> {
            int selectedSizeId = sizeRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedSizeRadioButton = sizeRadioGroup.findViewById(selectedSizeId);
            String selectedSize = selectedSizeRadioButton != null ? selectedSizeRadioButton.getText().toString() : "";

            if (!selectedSize.isEmpty()) {
                updateCart(product, selectedSize, quantity);
            } else {
                Toast.makeText(getContext(), "Please select a size.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateCart(Product product, String selectedSize, int quantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            db.collection("carts")
                    .whereEqualTo("userID", userID)
                    .whereEqualTo("productName", product.getName())
                    .whereEqualTo("size", selectedSize)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Product already exists in the cart, update the quantity
                                int existingQuantity = document.getLong("quantity").intValue();
                                int newQuantity = existingQuantity + quantity;

                                document.getReference()
                                        .update("quantity", newQuantity)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Cart updated", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Failed to update cart", Toast.LENGTH_SHORT).show();
                                        });

                                return;
                            }

                            addNewCartItem(db, product, selectedSize, quantity, userID);
                        } else {
                            Toast.makeText(getContext(), "Error checking cart", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addNewCartItem(FirebaseFirestore db, Product product, String selectedSize, int quantity, String userID) {
        DocumentReference cartRef = db.collection("carts").document();

        String imageFileName = product.getImage();
        CartItem cartItem = new CartItem(product.getName(), selectedSize, quantity, product.getPrice(), imageFileName, userID);

        cartRef.set(cartItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                });
    }

}
