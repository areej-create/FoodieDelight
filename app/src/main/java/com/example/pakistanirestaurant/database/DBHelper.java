package com.example.pakistanirestaurant.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pakistanirestaurant.models.CartItem;
import com.example.pakistanirestaurant.models.FoodItem;
import com.example.pakistanirestaurant.models.User;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pak_restaurant.db";
    private static final int DB_VERSION = 2; // Incremented for cart table

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUser = "CREATE TABLE Users(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE,email TEXT,password TEXT)";
        db.execSQL(createUser);

        String createFood = "CREATE TABLE FoodItems(id INTEGER PRIMARY KEY," +
                "name TEXT, description TEXT, price REAL, imageUrl TEXT)";
        db.execSQL(createFood);

        String createCart = "CREATE TABLE Cart(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "foodId INTEGER, name TEXT, description TEXT, price REAL, " +
                "imageUrl TEXT, quantity INTEGER, " +
                "FOREIGN KEY(foodId) REFERENCES FoodItems(id))";
        db.execSQL(createCart);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS FoodItems");
        db.execSQL("DROP TABLE IF EXISTS Cart");
        onCreate(db);
    }

    // ==================== USER METHODS ====================
    public boolean addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", user.getUsername());
        cv.put("email", user.getEmail());
        cv.put("password", user.getPassword());
        return db.insert("Users", null, cv) != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username=? AND password=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // ==================== FOOD METHODS ====================
    public void insertFood(FoodItem food) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", food.getId());
        cv.put("name", food.getName());
        cv.put("description", food.getDescription());
        cv.put("price", food.getPrice());
        cv.put("imageUrl", food.getImageUrl());
        db.insertWithOnConflict("FoodItems", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor getAllFoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM FoodItems", null);
    }

    public void deleteFood(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("FoodItems", "id=?", new String[]{String.valueOf(id)});
    }

    public FoodItem getFoodById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FoodItems WHERE id=?",
                new String[]{String.valueOf(id)});

        FoodItem food = null;
        if (cursor.moveToFirst()) {
            food = new FoodItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4)
            );
        }
        cursor.close();
        return food;
    }

    // ==================== CART METHODS ====================

    /**
     * Add item to cart or update quantity if already exists
     */
    public boolean addToCart(FoodItem food) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if item already exists in cart
        Cursor cursor = db.rawQuery("SELECT * FROM Cart WHERE foodId=?",
                new String[]{String.valueOf(food.getId())});

        if (cursor.getCount() > 0) {
            // Item exists, update quantity
            cursor.moveToFirst();
            int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            cursor.close();

            ContentValues cv = new ContentValues();
            cv.put("quantity", currentQuantity + 1);
            return db.update("Cart", cv, "foodId=?",
                    new String[]{String.valueOf(food.getId())}) > 0;
        } else {
            // New item, insert
            cursor.close();
            ContentValues cv = new ContentValues();
            cv.put("foodId", food.getId());
            cv.put("name", food.getName());
            cv.put("description", food.getDescription());
            cv.put("price", food.getPrice());
            cv.put("imageUrl", food.getImageUrl());
            cv.put("quantity", 1);
            return db.insert("Cart", null, cv) != -1;
        }
    }

    /**
     * Get all cart items
     */
    public Cursor getAllCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Cart", null);
    }

    /**
     * Update cart item quantity
     */
    public boolean updateCartQuantity(int cartId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (quantity <= 0) {
            // Remove item if quantity is 0 or less
            return db.delete("Cart", "id=?", new String[]{String.valueOf(cartId)}) > 0;
        } else {
            ContentValues cv = new ContentValues();
            cv.put("quantity", quantity);
            return db.update("Cart", cv, "id=?", new String[]{String.valueOf(cartId)}) > 0;
        }
    }

    /**
     * Remove item from cart
     */
    public boolean removeFromCart(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("Cart", "id=?", new String[]{String.valueOf(cartId)}) > 0;
    }

    /**
     * Clear entire cart
     */
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Cart", null, null);
    }

    /**
     * Get total cart items count
     */
    public int getCartItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(quantity) FROM Cart", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Get total cart price
     */
    public double getCartTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(price * quantity) FROM Cart", null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    /**
     * Check if item is in cart
     */
    public boolean isInCart(int foodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Cart WHERE foodId=?",
                new String[]{String.valueOf(foodId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}