package com.example.pakistanirestaurant.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.adapters.CartAdapter;
import com.example.pakistanirestaurant.database.DBHelper;
import com.example.pakistanirestaurant.models.CartItem;
import com.example.pakistanirestaurant.utils.ThemeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemListener {

    private RecyclerView recyclerCart;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private DBHelper db;

    // UI Components
    private TextView tvSubtotal, tvTax, tvDelivery, tvTotal;
    private LinearLayout tvEmptyCart, layoutSummary;
    private Button btnCheckout;

    // Pricing Constants
    private static final double TAX_RATE = 0.16; // 16% GST
    private static final double DELIVERY_FEE = 50.0; // Rs. 50 flat delivery fee
    private static final double FREE_DELIVERY_THRESHOLD = 1000.0; // Free delivery above Rs. 1000

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setContentView
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Setup ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        // Initialize views
        initializeViews();

        // Initialize database
        db = new DBHelper(this);
        cartItems = new ArrayList<>();

        // Setup RecyclerView
        setupRecyclerView();

        // Load cart items
        loadCartItems();

        // Setup button listeners
        setupButtonListeners();
    }

    /**
     * Initialize all views
     */
    private void initializeViews() {
        recyclerCart = findViewById(R.id.recyclerCart);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvTax = findViewById(R.id.tvTax);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvTotal = findViewById(R.id.tvTotal);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        layoutSummary = findViewById(R.id.layoutSummary);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    /**
     * Setup RecyclerView with LayoutManager
     */
    private void setupRecyclerView() {
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setHasFixedSize(true);
    }

    /**
     * Setup button click listeners
     */
    private void setupButtonListeners() {
        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    /**
     * Load all cart items from database
     */
    private void loadCartItems() {
        cartItems.clear();
        Cursor cursor = db.getAllCartItems();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    CartItem item = new CartItem(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("foodId")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("description")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                    );
                    cartItems.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        // Update UI based on cart status
        if (cartItems.isEmpty()) {
            showEmptyCartView();
        } else {
            showCartWithItems();
        }
    }

    /**
     * Show empty cart message
     */
    private void showEmptyCartView() {
        recyclerCart.setVisibility(View.GONE);
        layoutSummary.setVisibility(View.GONE);
        tvEmptyCart.setVisibility(View.VISIBLE);
    }

    /**
     * Show cart items and summary
     */
    private void showCartWithItems() {
        recyclerCart.setVisibility(View.VISIBLE);
        layoutSummary.setVisibility(View.VISIBLE);
        tvEmptyCart.setVisibility(View.GONE);

        // Setup adapter
        cartAdapter = new CartAdapter(this, cartItems, this);
        recyclerCart.setAdapter(cartAdapter);

        // Update price summary
        updatePriceSummary();
    }

    /**
     * Calculate and update price summary
     */
    private void updatePriceSummary() {
        double subtotal = 0;

        // Calculate subtotal
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }

        // Calculate tax
        double tax = subtotal * TAX_RATE;

        // Calculate delivery fee
        double deliveryFee = subtotal >= FREE_DELIVERY_THRESHOLD ? 0 : DELIVERY_FEE;

        // Calculate total
        double total = subtotal + tax + deliveryFee;

        // Update UI
        tvSubtotal.setText(String.format(Locale.getDefault(), "Rs. %.2f", subtotal));
        tvTax.setText(String.format(Locale.getDefault(), "Rs. %.2f", tax));

        if (deliveryFee == 0) {
            tvDelivery.setText("FREE");
            tvDelivery.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvDelivery.setText(String.format(Locale.getDefault(), "Rs. %.2f", deliveryFee));
            tvDelivery.setTextColor(getResources().getColor(android.R.color.black));
        }

        tvTotal.setText(String.format(Locale.getDefault(), "Rs. %.2f", total));
    }

    /**
     * Handle quantity change from adapter
     */
    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            // Show confirmation dialog before removing
            showRemoveConfirmation(item);
        } else {
            // Update quantity in database
            if (db.updateCartQuantity(item.getId(), newQuantity)) {
                item.setQuantity(newQuantity);
                cartAdapter.notifyDataSetChanged();
                updatePriceSummary();
                Toast.makeText(this, "Quantity updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                cartAdapter.notifyDataSetChanged(); // Revert UI changes
            }
        }
    }

    /**
     * Handle item removal from adapter
     */
    @Override
    public void onRemoveItem(CartItem item) {
        showRemoveConfirmation(item);
    }

    /**
     * Show confirmation dialog before removing item
     */
    private void showRemoveConfirmation(CartItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Item")
                .setMessage("Remove " + item.getName() + " from cart?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeItemFromCart(item);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    cartAdapter.notifyDataSetChanged(); // Refresh to show correct quantity
                })
                .setCancelable(true)
                .show();
    }

    /**
     * Remove item from cart
     */
    private void removeItemFromCart(CartItem item) {
        if (db.removeFromCart(item.getId())) {
            cartItems.remove(item);
            cartAdapter.notifyDataSetChanged();

            if (cartItems.isEmpty()) {
                showEmptyCartView();
            } else {
                updatePriceSummary();
            }

            Toast.makeText(this, item.getName() + " removed from cart", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle checkout process
     */
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get total amount
        double total = Double.parseDouble(
                tvTotal.getText().toString()
                        .replace("Rs. ", "")
                        .replace(",", "")
        );

        // Show checkout confirmation
        new AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setMessage(String.format(Locale.getDefault(),
                        "Total Amount: Rs. %.2f\n\nItems: %d\n\nProceed to checkout?",
                        total, cartItems.size()))
                .setPositiveButton("Confirm", (dialog, which) -> {
                    processCheckout(total);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Process the checkout
     */
    private void processCheckout(double total) {
        // TODO: Implement actual checkout logic here
        // - Payment gateway integration
        // - Order creation in database
        // - Send order to restaurant
        // - Generate receipt

        Toast.makeText(this,
                String.format(Locale.getDefault(),
                        "Order Placed Successfully!\nTotal: Rs. %.2f\n\n(Payment & Order features coming soon)",
                        total),
                Toast.LENGTH_LONG).show();

        // For now, just clear the cart after "successful" checkout
        // Uncomment these lines when implementing real checkout:
        // db.clearCart();
        // finish();
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
     * Refresh cart when activity resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
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