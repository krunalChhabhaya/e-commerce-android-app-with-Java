package com.example.trendz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context mContext;
    private List<Order> mOrderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        mContext = context;
        mOrderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_card_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order currentOrder = mOrderList.get(position);

        holder.fullNameTextView.setText("Full Name: " + currentOrder.getFullName());
        holder.addressTextView.setText("Address: " + currentOrder.getAddress());

        holder.cartItemsContainer.removeAllViews();

        for (CartItem cartItem : currentOrder.getCartItems()) {
            View orderItemView = LayoutInflater.from(mContext).inflate(R.layout.order_item, holder.cartItemsContainer, false);

            TextView productNameTextView = orderItemView.findViewById(R.id.textViewProductName);
            TextView productPriceTextView = orderItemView.findViewById(R.id.textViewProductPrice);
            TextView productQuantityTextView = orderItemView.findViewById(R.id.textViewProductQuantity);
            TextView productSizeTextView = orderItemView.findViewById(R.id.textViewProductSize);
            ImageView productImageView = orderItemView.findViewById(R.id.imageViewProduct);

            productNameTextView.setText("Product Name: " + cartItem.getProductName());
            productPriceTextView.setText("Price: " + cartItem.getPrice());
            productQuantityTextView.setText("Quantity: " + cartItem.getQuantity());
            productSizeTextView.setText("Size: " + cartItem.getSize());

            int resourceId = mContext.getResources().getIdentifier(cartItem.getImageFileName(), "drawable", mContext.getPackageName());
            if (resourceId != 0) {
                Glide.with(mContext)
                        .load(resourceId)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .into(productImageView);
            } else {
                productImageView.setImageResource(R.drawable.placeholder_image);
            }

            holder.cartItemsContainer.addView(orderItemView);
        }

        holder.totalPaidAmountTextView.setText(currentOrder.getTotalPayableAmount());
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView fullNameTextView, addressTextView;
        TextView productNameTextView, productPriceTextView, productQuantityTextView, productSizeTextView;
        TextView totalPaidAmountTextView;
        ImageView productImageView;
        LinearLayout cartItemsContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameTextView = itemView.findViewById(R.id.textViewFullName);
            addressTextView = itemView.findViewById(R.id.textViewAddress);
            productNameTextView = itemView.findViewById(R.id.textViewProductName);
            productPriceTextView = itemView.findViewById(R.id.textViewProductPrice);
            productQuantityTextView = itemView.findViewById(R.id.textViewProductQuantity);
            productSizeTextView = itemView.findViewById(R.id.textViewProductSize);
            totalPaidAmountTextView = itemView.findViewById(R.id.textViewTotalPaidAmount);
            productImageView = itemView.findViewById(R.id.imageViewProduct);
            cartItemsContainer = itemView.findViewById(R.id.cartItemsContainer);
        }
    }
}
