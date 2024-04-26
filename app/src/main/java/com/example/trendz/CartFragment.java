package com.example.trendz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import android.widget.Button;
import android.graphics.Paint;
import android.widget.TextView;
import android.content.Intent;
import android.widget.ImageView;
import java.io.Serializable;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Map;
import com.google.gson.JsonSyntaxException;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private int quantity = 1;
    private RecyclerView cartRecyclerView;
    private List<CartItem> cartItemList;
    private CartAdapter cartAdapter;
    private TextView totalPayableTextView;
    private Button removeAllButton;
    private Button makePaymentButton;
    private TextView cartEmptyTextView;
    private ImageView emptyCartImage;

    public CartFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartEmptyTextView = view.findViewById(R.id.cartEmptyTextView);
        emptyCartImage = view.findViewById(R.id.emptyCartImage);

        TextView cartEmptyTextView = view.findViewById(R.id.cartEmptyTextView);
        ImageView emptyCartImage = view.findViewById(R.id.emptyCartImage);

        removeAllButton = view.findViewById(R.id.removeAllButton);
        makePaymentButton = view.findViewById(R.id.makePaymentButton);

        cartItemList = new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = null;

        if (currentUser != null) {
            userID = currentUser.getUid();
            Log.d("UserID", "Logged-in User ID: " + userID);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("carts")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            cartItemList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CartItem cartItem = document.toObject(CartItem.class);
                                cartItemList.add(cartItem);
                            }

                            updateUI();
                            calculateAndDisplayTotalPayable();
                        } else {

                        }
                    });
        } else {
            Log.d("UserID", "No user is currently logged in.");
        }

        cartAdapter = new CartAdapter(cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);

        totalPayableTextView = view.findViewById(R.id.totalPayableTextView);

        Button removeAllButton = view.findViewById(R.id.removeAllButton);
        removeAllButton.setPaintFlags(removeAllButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        removeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllItems();
            }
        });

        Button makePaymentButton = view.findViewById(R.id.makePaymentButton);
        makePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalPayableAmount = totalPayableTextView.getText().toString();
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                intent.putExtra("TOTAL_PAYABLE_AMOUNT", totalPayableAmount);
                intent.putExtra("CART_ITEMS", (Serializable) cartItemList);
                startActivity(intent);
            }
        });

        return view;
    }

    private void removeAllItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            db.collection("carts")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            List<Task<Void>> deleteTasks = new ArrayList<>();

                            for (DocumentSnapshot document : documents) {
                                // Only delete items that belong to the current user
                                if (document.getString("userID").equals(userID)) {
                                    deleteTasks.add(document.getReference().delete());
                                }
                            }

                            Tasks.whenAllComplete(deleteTasks)
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            cartItemList.clear();
                                            cartAdapter.notifyDataSetChanged();

                                            updateUI();
                                            calculateAndDisplayTotalPayable();
                                        } else {

                                        }
                                    });
                        } else {

                        }
                    });
        }
    }


    private void updateUI() {
    if (cartItemList.isEmpty()) {
        cartRecyclerView.setVisibility(View.GONE);
        totalPayableTextView.setVisibility(View.GONE);
        removeAllButton.setVisibility(View.GONE);
        makePaymentButton.setVisibility(View.GONE);

        cartEmptyTextView.setVisibility(View.VISIBLE);
        emptyCartImage.setVisibility(View.VISIBLE);
    } else {
        cartEmptyTextView.setVisibility(View.GONE);
        emptyCartImage.setVisibility(View.GONE);
        cartAdapter = new CartAdapter(cartItemList);
        cartRecyclerView.setAdapter(cartAdapter);
        //calculateAndDisplayTotalPayable(cartItemList);
    }
}

    private void calculateAndDisplayTotalPayable() {
        double totalPayable = 0;
        for (CartItem cartItem : cartItemList) {
            totalPayable += cartItem.calculateTotalPrice();
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedTotal = decimalFormat.format(totalPayable);

        totalPayableTextView.setText(getString(R.string.total_payable_amount, formattedTotal));
    }

}