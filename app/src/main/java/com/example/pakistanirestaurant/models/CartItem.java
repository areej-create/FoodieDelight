package com.example.pakistanirestaurant.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Model class representing a cart item
 * Implements Parcelable for easy data passing between activities
 */
public class CartItem implements Parcelable {

    private int id;              // Cart item ID (primary key)
    private int foodId;          // Reference to FoodItem ID
    private String name;         // Food item name
    private String description;  // Food item description
    private double price;        // Unit price
    private String imageUrl;     // Image URL or drawable name
    private int quantity;        // Quantity in cart

    /**
     * Default constructor
     */
    public CartItem() {
    }

    /**
     * Full constructor
     */
    public CartItem(int id, int foodId, String name, String description,
                    double price, String imageUrl, int quantity) {
        this.id = id;
        this.foodId = foodId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    /**
     * Constructor without cart ID (for creating new cart items)
     */
    public CartItem(int foodId, String name, String description,
                    double price, String imageUrl, int quantity) {
        this.foodId = foodId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    /**
     * Create CartItem from FoodItem
     */
    public static CartItem fromFoodItem(FoodItem foodItem, int quantity) {
        return new CartItem(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getDescription(),
                foodItem.getPrice(),
                foodItem.getImageUrl(),
                quantity
        );
    }

    // ==================== GETTERS ====================

    public int getId() {
        return id;
    }

    public int getFoodId() {
        return foodId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    // ==================== SETTERS ====================

    public void setId(int id) {
        this.id = id;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // ==================== BUSINESS LOGIC ====================

    /**
     * Calculate subtotal (price * quantity)
     */
    public double getSubtotal() {
        return price * quantity;
    }

    /**
     * Get formatted price string
     */
    public String getFormattedPrice() {
        return String.format(Locale.getDefault(), "Rs. %.2f", price);
    }

    /**
     * Get formatted subtotal string
     */
    public String getFormattedSubtotal() {
        return String.format(Locale.getDefault(), "Rs. %.2f", getSubtotal());
    }

    /**
     * Increase quantity by 1
     */
    public void increaseQuantity() {
        this.quantity++;
    }

    /**
     * Decrease quantity by 1 (minimum 0)
     */
    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }

    /**
     * Check if item has valid quantity
     */
    public boolean hasValidQuantity() {
        return quantity > 0;
    }

    /**
     * Check if description is available
     */
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    /**
     * Check if image URL is available
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    // ==================== OBJECT METHODS ====================

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", foodId=" + foodId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartItem cartItem = (CartItem) o;

        if (id != cartItem.id) return false;
        return foodId == cartItem.foodId;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + foodId;
        return result;
    }

    // ==================== PARCELABLE IMPLEMENTATION ====================

    protected CartItem(Parcel in) {
        id = in.readInt();
        foodId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageUrl = in.readString();
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(foodId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeInt(quantity);
    }

    // ==================== BUILDER PATTERN (Optional) ====================

    /**
     * Builder class for creating CartItem instances
     */
    public static class Builder {
        private int id;
        private int foodId;
        private String name;
        private String description;
        private double price;
        private String imageUrl;
        private int quantity = 1;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setFoodId(int foodId) {
            this.foodId = foodId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public CartItem build() {
            return new CartItem(id, foodId, name, description, price, imageUrl, quantity);
        }
    }
}