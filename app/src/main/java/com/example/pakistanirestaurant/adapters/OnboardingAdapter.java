package com.example.pakistanirestaurant.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardViewHolder> {
    private List<Integer> images;
    public OnboardingAdapter(List<Integer> imgs){images=imgs;}

    @NonNull
    @Override
    public OnboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new OnboardViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardViewHolder holder, int position){
        holder.imageView.setImageResource(images.get(position));
    }

    @Override
    public int getItemCount(){ return images.size(); }

    static class OnboardViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public OnboardViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = (ImageView)itemView;
        }
    }
}
