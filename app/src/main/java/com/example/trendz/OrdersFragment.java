package com.example.trendz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class OrdersFragment extends Fragment {

    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private ArrayList<Order> orderList;
    private FirebaseFirestore db;
    private TextView ordersEmptyTextView;
    private ImageView ordersEmptyImage;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getActivity(), orderList);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ordersRecyclerView.setAdapter(orderAdapter);

        Button deleteAllOrdersButton = view.findViewById(R.id.deleteAllOrdersButton);
        deleteAllOrdersButton.setOnClickListener(v -> deleteAllOrders());

        ordersEmptyTextView = view.findViewById(R.id.ordersEmptyTextView);
        ordersEmptyImage = view.findViewById(R.id.ordersEmptyImage);

        db = FirebaseFirestore.getInstance();

        fetchOrderDetails();

        return view;
    }

    private void fetchOrderDetails() {
        orderList.clear();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            db.collection("orders")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Order order = document.toObject(Order.class);
                                orderList.add(order);
                            }
                            orderAdapter.notifyDataSetChanged();

                            if (orderList.isEmpty()) {
                                ordersEmptyTextView.setVisibility(View.VISIBLE);
                                ordersEmptyImage.setVisibility(View.VISIBLE);
                                hideClearHistoryButton();
                            } else {
                                ordersEmptyTextView.setVisibility(View.GONE);
                                ordersEmptyImage.setVisibility(View.GONE);
                                showClearHistoryButton();
                            }
                        } else {

                        }
                    });
        }
    }

    private void deleteAllOrders() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userID = currentUser.getUid();

            db.collection("orders")
                    .whereEqualTo("userID", userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                            }

                            orderList.clear();
                            orderAdapter.notifyDataSetChanged();

                            if (orderList.isEmpty()) {
                                ordersEmptyTextView.setVisibility(View.VISIBLE);
                                ordersEmptyImage.setVisibility(View.VISIBLE);
                                hideClearHistoryButton();
                            } else {
                                ordersEmptyTextView.setVisibility(View.GONE);
                                ordersEmptyImage.setVisibility(View.GONE);
                                showClearHistoryButton();
                            }

                            Button deleteAllOrdersButton = getView().findViewById(R.id.deleteAllOrdersButton);
                            deleteAllOrdersButton.setVisibility(View.GONE);

                            Toast.makeText(getActivity(), "All orders deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Error deleting orders", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void hideClearHistoryButton() {
        Button deleteAllOrdersButton = getView().findViewById(R.id.deleteAllOrdersButton);
        deleteAllOrdersButton.setVisibility(View.GONE);
    }

    private void showClearHistoryButton() {
        Button deleteAllOrdersButton = getView().findViewById(R.id.deleteAllOrdersButton);
        deleteAllOrdersButton.setVisibility(View.VISIBLE);
    }
}