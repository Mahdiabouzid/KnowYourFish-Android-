package com.example.homescreen;
import android.view.LayoutInflater;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
import java.util.Vector;


public class SliderAdapter extends  RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{

    private List<SliderItem> SliderItems;
    private ViewPager2 viewPager2;


    SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        SliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(SliderItems.get(position));
        if(position==SliderItems.size()-2){
            viewPager2.post(sliderRunnable);

        }
    }

    @Override
    public int getItemCount() {
        return SliderItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{
        private RoundedImageView imageView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.ViewPagerImageSlider);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Open current favourite ground

                }
            });

        }
        void setImage(SliderItem sliderItem){
            imageView.setImageResource(sliderItem.getImage());
        }

    }
    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            SliderItems.addAll(SliderItems);
            notifyDataSetChanged();

        }
    };
}
