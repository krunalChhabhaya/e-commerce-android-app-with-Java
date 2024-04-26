package com.example.trendz;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trendz.Product;
import com.example.trendz.ProductAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsFragment extends Fragment implements ProductAdapter.OnItemClickListener {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productsCollection = db.collection("Product");

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productAdapter = new ProductAdapter(productList);
        productAdapter.setOnItemClickListener(this);

        productRecyclerView.setAdapter(productAdapter);

        fetchProductsFromFirestore();

        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        productRecyclerView.setLayoutManager(layoutManager);

        Button sortButton = view.findViewById(R.id.sortButton);

        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortMenu(v);
            }
        });

        return view;
    }

    private void fetchProductsFromFirestore() {
        productsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");
                        String name = document.getString("name");
                        String brand = document.getString("brand");
                        String description = document.getString("description");
                        double price = document.getDouble("price");
                        String category = document.getString("category");
                        String gender = document.getString("gender");
                        String image = document.getString("image");
                        String color = document.getString("color");
                        Object sizesObject = document.get("sizes");

                        if (sizesObject instanceof List) {
                            List<String> sizes = (List<String>) sizesObject;

                            Product product = new Product(id, name, brand, description, price, image, category, gender, sizes, color);
                            productList.add(product);
                        } else {

                        }
                    }
            productAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {

        });
    }

    @Override
    public void onItemClick(Product product) {
        openProductDetailFragment(product);
    }

    private void openProductDetailFragment(Product product) {
        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_product", product);
        productDetailFragment.setArguments(bundle);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, productDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showSortMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());

        Map<Integer, Runnable> sortingActions = new HashMap<>();
        sortingActions.put(R.id.sortLowToHigh, this::sortByPriceLowToHigh);
        sortingActions.put(R.id.sortHighToLow, this::sortByPriceHighToLow);

        popupMenu.setOnMenuItemClickListener(item -> {
            Runnable sortingAction = sortingActions.get(item.getItemId());
            if (sortingAction != null) {
                sortingAction.run();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void sortByPriceLowToHigh() {
        Collections.sort(productList, Comparator.comparingDouble(Product::getPrice));
        refreshAdapter();
    }

    private void sortByPriceHighToLow() {
        Collections.sort(productList, (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        refreshAdapter();
    }

    private void refreshAdapter() {
        productAdapter.notifyDataSetChanged();
    }
}
