package com.example.pakistanirestaurant.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.database.DBHelper;
import com.example.pakistanirestaurant.models.FoodItem;
import com.example.pakistanirestaurant.utils.ThemeHelper;
import com.google.android.material.button.MaterialButton;

public class FoodDetailActivity extends AppCompatActivity {

    // UI Components
    private TextView tvName, tvDesc, tvPrice;
    private ImageView imgFood;
    private MaterialButton btnAddToCart;
    private ImageButton btnFavorite;
    private Toolbar toolbar;

    // Data
    private DBHelper db;
    private int foodId;
    private FoodItem currentFood;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setContentView
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        // Initialize database
        db = new DBHelper(this);

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Get food ID from intent
        foodId = getIntent().getIntExtra("foodId", 0);

        // Load food details
        loadFoodDetails();

        // Setup button listeners
        setupButtonListeners();

        // Update button state
        updateAddToCartButton();
    }

    /**
     * Initialize all views
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvName = findViewById(R.id.tvName);
        tvDesc = findViewById(R.id.tvDesc);
        tvPrice = findViewById(R.id.tvPrice);
        imgFood = findViewById(R.id.imgFood);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnFavorite = findViewById(R.id.btn_favorite);
    }

    /**
     * Setup toolbar with back navigation
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    /**
     * Load food details from database
     */
    private void loadFoodDetails() {
        Cursor cursor = db.getAllFoods();

        while (cursor.moveToNext()) {
            if (cursor.getInt(0) == foodId) {
                // Create FoodItem object
                currentFood = new FoodItem(
                        cursor.getInt(0),           // id
                        cursor.getString(1),        // name
                        cursor.getString(2),        // description
                        cursor.getDouble(3),        // price
                        cursor.getString(4)         // imageUrl
                );

                // Display food details
                tvName.setText(currentFood.getName());
                tvDesc.setText(currentFood.getDescription());
                tvPrice.setText(String.valueOf((int) currentFood.getPrice()));

                // Load image
                loadFoodImage(currentFood.getImageUrl());

                break;
            }
        }
        cursor.close();

        // Check if food not found
        if (currentFood == null) {
            Toast.makeText(this, "Food item not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Load food image using Glide
     */
    private void loadFoodImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imgFood.setImageResource(R.drawable.placeholder);
            return;
        }

        // Try to load as drawable resource first
        int resId = getResources().getIdentifier(
                imageUrl, "drawable", getPackageName());

        if (resId != 0) {
            // Load from drawable
            Glide.with(this)
                    .load(resId)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(imgFood);
        } else {
            // Load from URL
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(imgFood);
        }
    }

    /**
     * Setup button click listeners
     */
    private void setupButtonListeners() {
        // Add to Cart button
        btnAddToCart.setOnClickListener(v -> addToCart());

        // Favorite button
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> toggleFavorite());
        }
    }

    /**
     * Add food item to cart using db.addToCart(FoodItem)
     */
    private void addToCart() {
        if (currentFood == null) {
            Toast.makeText(this, "Unable to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use the existing DBHelper.addToCart(FoodItem) method
        boolean success = db.addToCart(currentFood);

        if (success) {
            // Check if it was an update or new addition
            if (db.isInCart(currentFood.getId())) {
                Toast.makeText(this,
                        currentFood.getName() + " added to cart",
                        Toast.LENGTH_SHORT).show();

                // Update button appearance
                updateAddToCartButton();

                // Animate the button
                animateAddToCart();
            }
        } else {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update Add to Cart button based on cart status
     */
    private void updateAddToCartButton() {
        if (currentFood != null && db.isInCart(currentFood.getId())) {
            btnAddToCart.setIcon(getDrawable(R.drawable.ic_check));
            btnAddToCart.setText("Added to Cart");
        } else {
            btnAddToCart.setIcon(getDrawable(R.drawable.ic_cart));
            btnAddToCart.setText("Add to Cart");
        }
    }

    /**
     * Animate Add to Cart button
     */
    private void animateAddToCart() {
        btnAddToCart.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() ->
                        btnAddToCart.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                )
                .start();
    }

    /**
     * Toggle favorite status
     */
    private void toggleFavorite() {
        isFavorite = !isFavorite;

        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        }

        // TODO: Implement actual favorite database logic here
        // db.addToFavorites(foodId) or db.removeFromFavorites(foodId)
    }

    /**
     * Handle back button press
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Refresh button state when returning to activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateAddToCartButton();
    }

    /**
     * Clean up resources
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}