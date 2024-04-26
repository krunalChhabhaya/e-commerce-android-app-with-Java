package com.example.trendz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.widget.ImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;

    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.bind(cartItem);

        int imageResourceId = holder.itemView.getContext().getResources().getIdentifier(
                cartItem.getImageFileName(),
                "drawable",
                holder.itemView.getContext().getPackageName());

        if (imageResourceId != 0) {
            holder.productImageView.setImageResource(imageResourceId);
        } else {
            holder.productImageView.setImageResource(R.drawable.default_image);
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        private TextView productNameTextView;
        private TextView sizeTextView;
        private TextView quantityTextView;
        private TextView priceTextView;
        private TextView totalPriceTextView;
        private ImageView productImageView;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            sizeTextView = itemView.findViewById(R.id.sizeTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
        }

        public void bind(CartItem cartItem) {
            productNameTextView.setText(cartItem.getProductName());
            sizeTextView.setText("Size: " + cartItem.getSize());
            quantityTextView.setText("Quantity: " + cartItem.getQuantity());
            priceTextView.setText("Price: $" + cartItem.getPrice());
            totalPriceTextView.setText("Total Price: $" + cartItem.calculateTotalPrice());
        }
    }
}
