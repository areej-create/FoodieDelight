package com.example.pakistanirestaurant.network;

import com.example.pakistanirestaurant.models.FoodItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ApiClient {
    // Using a mock API service
    private static final String BASE_URL = "https://run.mocky.io/v3/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public interface ApiService {
        // This endpoint returns a list of Pakistani food items
        @GET("3c5e7f89-8f4a-4d3b-9c2a-1e5f6b7c8d9e")
        Call<List<FoodItem>> getFoods();
    }
}