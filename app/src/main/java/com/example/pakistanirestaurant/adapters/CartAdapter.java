package com.example.pakistanirestaurant.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.models.CartItem;

import java.util.List;
import java.util.Locale;

/**
 * RecyclerView Adapter for displaying cart items
 * Handles quantity changes and item removal
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private OnCartItemListener listener;

    /**
     * Interface for cart item callbacks
     */
    public interface OnCartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }

    /**
     * Constructor
     */
    public CartAdapter(Context context, List<CartItem> cartItems, OnCartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Set item details
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Rs. %.0f", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvSubtotal.setText(String.format(Locale.getDefault(), "Rs. %.0f", item.getSubtotal()));

        // Set description if available
        if (holder.tvDescription != null) {
            if (item.hasDescription()) {
                holder.tvDescription.setText(item.getDescription());
                holder.tvDescription.setVisibility(View.VISIBLE);
            } else {
                holder.tvDescription.setVisibility(View.GONE);
            }
        }

        // Load image
        loadImage(holder.imgFood, item.getImageUrl());

        // Set up quantity controls
        setupQuantityControls(holder, item, position);

        // Set up remove button
        setupRemoveButton(holder, item, position);
    }

    /**
     * Load image using Glide
     */
    private void loadImage(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.placeholder);
            return;
        }

        // Try to load as drawable resource first
        int resId = context.getResources().getIdentifier(
                imageUrl, "drawable", context.getPackageName());

        if (resId != 0) {
            // Load from drawable
            Glide.with(context)
                    .load(resId)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(imageView);
        } else {
            // Load from URL
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(imageView);
        }
    }

    /**
     * Setup quantity increase/decrease buttons
     */
    private void setupQuantityControls(CartViewHolder holder, CartItem item, int position) {
        // Increase quantity
        holder.btnIncrease.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            int newQuantity = currentQuantity + 1;

            // Optional: Set maximum quantity limit
            if (newQuantity <= 99) { // Max 99 items
                if (listener != null) {
                    listener.onQuantityChanged(item, newQuantity);
                }
            } else {
                // Show toast or message for max quantity
                android.widget.Toast.makeText(context,
                        "Maximum quantity reached",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Decrease quantity
        holder.btnDecrease.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            int newQuantity = currentQuantity - 1;

            if (listener != null) {
                listener.onQuantityChanged(item, newQuantity);
            }
        });

        // Disable decrease button if quantity is 1
        holder.btnDecrease.setEnabled(item.getQuantity() > 1);
        holder.btnDecrease.setAlpha(item.getQuantity() > 1 ? 1.0f : 0.5f);
    }

    /**
     * Setup remove button
     */
    private void setupRemoveButton(CartViewHolder holder, CartItem item, int position) {
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    /**
     * Update the cart items list
     */
    public void updateCartItems(List<CartItem> newItems) {
        this.cartItems = newItems;
        notifyDataSetChanged();
    }

    /**
     * Get total items count
     */
    public int getTotalItemsCount() {
        int total = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    /**
     * Get total cart value
     */
    public double getTotalValue() {
        double total = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                total += item.getSubtotal();
            }
        }
        return total;
    }

    /**
     * Remove item at position
     */
    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        }
    }

    /**
     * Clear all items
     */
    public void clearCart() {
        int size = cartItems.size();
        cartItems.clear();
        notifyItemRangeRemoved(0, size);
    }

    /**
     * ViewHolder class
     */
    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvName, tvPrice, tvQuantity, tvSubtotal, tvDescription;
        ImageButton btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            imgFood = itemView.findViewById(R.id.imgCartFood);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnRemove = itemView.findViewById(R.id.btnRemove);


        }
    }

    // ==================== ANIMATION HELPERS (Optional) ====================

    /**
     * Animate item addition (optional enhancement)
     */
    public void animateItemAddition(int position) {
        notifyItemInserted(position);
    }

    /**
     * Animate item removal (optional enhancement)
     */
    public void animateItemRemoval(int position) {
        notifyItemRemoved(position);
    }

    // ==================== FILTER/SEARCH METHODS (Optional) ====================

    /**
     * Filter items by name (optional feature)
     */
    public void filterByName(String query) {
        // Implementation for search functionality
        notifyDataSetChanged();
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }

    /**
     * Get item at position
     */
    public CartItem getItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            return cartItems.get(position);
        }
        return null;
    }

    /**
     * Find item by food ID
     */
    public int findItemPosition(int foodId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getFoodId() == foodId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Update single item
     */
    public void updateItem(int position, CartItem item) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.set(position, item);
            notifyItemChanged(position);
        }
    }
}