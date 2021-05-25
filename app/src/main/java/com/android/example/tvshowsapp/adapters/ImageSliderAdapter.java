package com.android.example.tvshowsapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.tvshowsapp.R;
import com.android.example.tvshowsapp.databinding.ItemContainerSlideImageBinding;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>{

    private final String[] sliderImage;
    private LayoutInflater layoutInflater;

    public ImageSliderAdapter(String[] sliderImage) {
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null){
            layoutInflater= LayoutInflater.from(parent.getContext());
        }
        ItemContainerSlideImageBinding slideImageBinding= DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_slide_image, parent, false
        );
        return new ImageSliderViewHolder(slideImageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderAdapter.ImageSliderViewHolder holder, int position) {
        holder.bindSliderImage(sliderImage[position]);
    }

    @Override
    public int getItemCount() {
        return sliderImage.length;
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSlideImageBinding itemContainerSlideImageBinding;

        public ImageSliderViewHolder(ItemContainerSlideImageBinding itemContainerSlideImageBinding) {
            super(itemContainerSlideImageBinding.getRoot());
            this.itemContainerSlideImageBinding= itemContainerSlideImageBinding;
        }

        public void bindSliderImage(String imageURL){
            itemContainerSlideImageBinding.setImageURL(imageURL);
        }
    }
}
