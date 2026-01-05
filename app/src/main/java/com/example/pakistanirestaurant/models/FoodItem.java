package com.example.pakistanirestaurant.models;

public class FoodItem {
    private int id;
    private String name, description, imageUrl;
    private double price;

    public FoodItem(int id,String n,String d,double p,String img){
        this.id=id; name=n; description=d; price=p; imageUrl=img;
    }
    public int getId(){return id;}
    public String getName(){return name;}
    public String getDescription(){return description;}
    public double getPrice(){return price;}
    public String getImageUrl(){return imageUrl;}
}
