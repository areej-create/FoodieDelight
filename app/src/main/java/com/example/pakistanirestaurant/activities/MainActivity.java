package com.example.pakistanirestaurant.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pakistanirestaurant.R;
import com.example.pakistanirestaurant.adapters.FoodAdapter;
import com.example.pakistanirestaurant.database.DBHelper;
import com.example.pakistanirestaurant.models.FoodItem;
import com.example.pakistanirestaurant.network.MockDataSource;
import com.example.pakistanirestaurant.utils.MySharedPrefManager;
import com.example.pakistanirestaurant.utils.ThemeHelper;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FoodAdapter adapter;
    List<FoodItem> foodList;
    DBHelper db;
    CollapsingToolbarLayout collapsingToolbar;
    private MenuItem cartMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // IMPORTANT: Apply theme BEFORE setContentView
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("FoodieDelight");
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.white));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

        recyclerView = findViewById(R.id.recyclerFood);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        db = new DBHelper(this);

        foodList = new ArrayList<>();
        loadFoodsFromDB();
        loadMockDataIfNeeded();
    }

    private void loadFoodsFromDB() {
        Cursor cursor = db.getAllFoods();
        while (cursor.moveToNext()) {
            foodList.add(new FoodItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4)
            ));
        }
        cursor.close();

        adapter = new FoodAdapter(this, foodList);
        recyclerView.setAdapter(adapter);
    }

    private void loadMockDataIfNeeded() {
        if (foodList.isEmpty()) {
            List<FoodItem> mockFoods = MockDataSource.getMockFoods();

            for (FoodItem food : mockFoods) {
                db.insertFood(food);
                foodList.add(food);
            }

            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Loaded " + mockFoods.size() + " items", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get cart menu item and update badge
        cartMenuItem = menu.findItem(R.id.action_cart);
        updateCartBadge();

        // Update theme toggle text based on current theme
        MenuItem themeToggle = menu.findItem(R.id.theme_toggle);
        if (ThemeHelper.isDarkTheme(this)) {
            themeToggle.setTitle("Switch to Light Theme");
        } else {
            themeToggle.setTitle("Switch to Dark Theme");
        }

        return true;
    }

    private void updateCartBadge() {
        if (cartMenuItem != null) {
            int cartCount = db.getCartItemCount();
            if (cartCount > 0) {
                cartMenuItem.setTitle("Cart (" + cartCount + ")");
            } else {
                cartMenuItem.setTitle("Cart");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
            return true;

        } else if (id == R.id.theme_toggle) {
            // Toggle between Light and Dark theme
            if (ThemeHelper.isDarkTheme(this)) {
                ThemeHelper.setTheme(this, ThemeHelper.LIGHT);
                Toast.makeText(this, "Switched to Light Theme", Toast.LENGTH_SHORT).show();
            } else {
                ThemeHelper.setTheme(this, ThemeHelper.DARK);
                Toast.makeText(this, "Switched to Dark Theme", Toast.LENGTH_SHORT).show();
            }
            recreate(); // Restart activity to apply theme
            return true;

        } else if (id == R.id.logout) {
            new MySharedPrefManager(this).setLoggedIn(false);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge when returning to main activity
        updateCartBadge();
    }
}