package com.example.pakistanirestaurant.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.activities.FoodDetailActivity;
import com.example.pakistanirestaurant.database.DBHelper;
import com.example.pakistanirestaurant.models.FoodItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<FoodItem> foods;
    private Context context;
    private DBHelper db;

    public FoodAdapter(Context ctx, List<FoodItem> list) {
        context = ctx;
        foods = list;
        db = new DBHelper(ctx);
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem f = foods.get(position);

        holder.name.setText(f.getName());
        holder.price.setText(String.format(Locale.getDefault(), "%.0f", f.getPrice()));

        // Set description if available
        if (holder.description != null && f.getDescription() != null) {
            holder.description.setText(f.getDescription());
        }

        // Load image using Glide
        Glide.with(context)
                .load(getImageResourceId(f.getImageUrl()))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.image);

        // Click to view details
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, FoodDetailActivity.class);
            i.putExtra("foodId", f.getId());
            context.startActivity(i);
        });

        // Add to cart button
        if (holder.btnAddCart != null) {
            // Check if item is already in cart
            if (db.isInCart(f.getId())) {
                holder.btnAddCart.setIcon(context.getDrawable(R.drawable.ic_check));
                holder.btnAddCart.setText("Added");
            } else {
                holder.btnAddCart.setIcon(context.getDrawable(R.drawable.ic_add));
                holder.btnAddCart.setText("Add");
            }

            holder.btnAddCart.setOnClickListener(v -> {
                if (db.addToCart(f)) {
                    Toast.makeText(context, f.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                    holder.btnAddCart.setIcon(context.getDrawable(R.drawable.ic_check));
                    holder.btnAddCart.setText("Added");

                    // Reset after 1.5 seconds
                    holder.btnAddCart.postDelayed(() -> {
                        if (db.isInCart(f.getId())) {
                            holder.btnAddCart.setIcon(context.getDrawable(R.drawable.ic_check));
                            holder.btnAddCart.setText("Added");
                        } else {
                            holder.btnAddCart.setIcon(context.getDrawable(R.drawable.ic_add));
                            holder.btnAddCart.setText("Add");
                        }
                    }, 1500);
                } else {
                    Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Favorite button (optional)
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setOnClickListener(v -> {
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                // TODO: Implement favorites functionality
            });
        }
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    /**
     * Helper method to get image resource ID from drawable name or URL
     */
    private Object getImageResourceId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return R.drawable.placeholder;
        }

        // Try to get drawable resource by name
        int resId = context.getResources().getIdentifier(
                imageUrl, "drawable", context.getPackageName());

        if (resId != 0) {
            return resId;
        } else {
            // Return URL for Glide to load from network
            return imageUrl;
        }
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description, currencySymbol;
        ImageView image;
        MaterialButton btnAddCart;
        ImageButton btnFavorite;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.food_name);
            price = itemView.findViewById(R.id.food_price);
            image = itemView.findViewById(R.id.food_image);

            // Optional fields (may not exist in all layouts)
            description = itemView.findViewById(R.id.food_description);
            currencySymbol = itemView.findViewById(R.id.currency_symbol);
            btnAddCart = itemView.findViewById(R.id.btn_add_cart);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}