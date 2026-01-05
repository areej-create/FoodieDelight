package com.example.pakistanirestaurant.network;

import com.example.pakistanirestaurant.models.FoodItem;
import java.util.ArrayList;
import java.util.List;

public class MockDataSource {

    public static List<FoodItem> getMockFoods() {
        List<FoodItem> foods = new ArrayList<>();

        foods.add(new FoodItem(1, "Chicken Biryani",
                "Aromatic basmati rice with tender chicken, saffron and traditional spices",
                250.00, "https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=400"));

        foods.add(new FoodItem(2, "Beef Nihari",
                "Slow-cooked beef stew in rich, spiced gravy served with naan",
                330.00, "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400"));

        foods.add(new FoodItem(3, "Chicken Haleem",
                "Traditional wheat and meat porridge with aromatic spices and lentils",
                150.00, "https://images.unsplash.com/photo-1574484284002-952d92456975?w=400"));

        foods.add(new FoodItem(4, "Chicken Karahi",
                "Spicy chicken cooked in traditional wok with fresh tomatoes and peppers",
                1300.00, "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=400"));

        foods.add(new FoodItem(5, "Seekh Kabab",
                "Grilled minced meat skewers with fresh herbs and aromatic spices",
                120.00, "https://images.unsplash.com/photo-1599487488170-d11ec9c172f0?w=400"));

        foods.add(new FoodItem(6, "Chapli Kabab",
                "Spiced ground meat patties from Peshawar with tomatoes and onions",
                200.00, "https://images.unsplash.com/photo-1529042410759-befb1204b468?w=400"));

        foods.add(new FoodItem(7, "Lahori Chargha",
                "Whole marinated fried chicken with special Lahori spices",
                1600.00, "https://images.unsplash.com/photo-1598103442097-8b74394b95c6?w=400"));

        foods.add(new FoodItem(8, "Mutton Paya",
                "Traditional trotters curry slow-cooked overnight with aromatic spices",
                450.00, "https://images.unsplash.com/photo-1567337710282-00832b415979?w=400"));

        foods.add(new FoodItem(9, "Chicken Tikka",
                "Marinated grilled chicken pieces with yogurt and spices",
                150.00, "https://images.unsplash.com/photo-1603360946369-dc9bb6258143?w=400"));

        foods.add(new FoodItem(10, "Aloo Keema",
                "Minced meat with potatoes in flavorful curry sauce",
                320.00, "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=400"));

        return foods;
    }
}