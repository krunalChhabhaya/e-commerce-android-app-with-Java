package com.example.trendz;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import com.example.trendz.R;
import com.example.trendz.ProductsFragment;
import com.example.trendz.CartFragment;
import com.example.trendz.OrdersFragment;
import android.content.Intent;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        loadFragment(new ProductsFragment());

        Toolbar topBar = findViewById(R.id.topBar);
        setSupportActionBar(topBar);

        setTitle("Products");

        ImageButton logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(view -> logoutUser());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_products) {
                    loadFragment(new ProductsFragment());
                    setTitle("Products");
                } else if (itemId == R.id.navigation_cart) {
                    loadFragment(new CartFragment());
                    setTitle("Cart");
                } else if (itemId == R.id.navigation_orders) {
                    loadFragment(new OrdersFragment());
                    setTitle("Orders");
                }
    
                return true;
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("REDIRECT_TO_ORDERS", false)) {
            loadFragment(new OrdersFragment());
            setTitle("Orders");
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(ProductActivity.this, Login.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (currentFragment instanceof ProductsFragment) {
            Intent intent = new Intent(ProductActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (currentFragment instanceof CartFragment) {
            loadFragment(new ProductsFragment());
        } else if (currentFragment instanceof OrdersFragment) {
            loadFragment(new CartFragment());
        } else {
            super.onBackPressed();
        }
    }
}