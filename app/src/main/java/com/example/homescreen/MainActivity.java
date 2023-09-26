package com.example.homescreen;

import android.content.Intent;
import android.os.Bundle;

import com.example.homescreen.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;


import android.os.Handler;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private FragmentTransaction transaction;
    private ViewPager2 viewPager2;
    private Handler sliderHandler=new Handler();
    private ActivityMainBinding binding;
    private Button button_identification;
    private Button button_grounds;
    private Button button_statistics;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        button_identification=(Button)findViewById(R.id.button_identification);
        button_identification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomNav.setSelectedItemId(R.id.navigation_identification);
                startActivity(new Intent(MainActivity.this, IdentificationActivity.class));
            }
        });
        button_grounds=(Button)findViewById(R.id.button_grounds);
        button_grounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Activity Grounds
                bottomNav.setSelectedItemId(R.id.navigation_grounds);
            }
        });
        button_statistics=(Button)findViewById(R.id.button_statistics);
        button_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Fangstatistik.class));
                bottomNav.setSelectedItemId(R.id.navigation_statistics);
            }
        });
        bottomNav=findViewById(R.id.bottomNavigationView);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    break;
                case R.id.navigation_identification:
                    startActivity(new Intent(MainActivity.this, IdentificationActivity.class));
                    break;
                case R.id.navigation_statistics:
                    startActivity(new Intent(MainActivity.this, Fangstatistik.class));
                    break;
                case R.id.navigation_grounds:

                    break;
            }
            return true;
        });
        viewPager2 = findViewById(R.id.ViewPagerImageSlider);
        List<SliderItem> sliderItems= new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.fisch1));
        sliderItems.add(new SliderItem(R.drawable.fisch2));
        sliderItems.add(new SliderItem(R.drawable.fisch3));
        sliderItems.add(new SliderItem(R.drawable.fisch4));
        sliderItems.add(new SliderItem(R.drawable.fisch5));
        sliderItems.add(new SliderItem(R.drawable.fisch6));
        viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer=new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r=1-Math.abs(position);
                page.setScaleY(0.85f + r*0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }
    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() +1 );
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        //Log.e("onPause","onResume");
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.e("onResume","onResume");
        sliderHandler.postDelayed(sliderRunnable,3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Log.e("onDestroy","onResume");
    }

}