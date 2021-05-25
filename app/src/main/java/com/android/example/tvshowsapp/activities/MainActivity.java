package com.android.example.tvshowsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.android.example.tvshowsapp.R;
import com.android.example.tvshowsapp.adapters.TVShowsAdapter;
import com.android.example.tvshowsapp.databinding.ActivityMainBinding;
import com.android.example.tvshowsapp.listeners.TVShowsListener;
import com.android.example.tvshowsapp.models.TVShow;
import com.android.example.tvshowsapp.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowsListener {

    private MostPopularTVShowsViewModel viewModel;
    private ActivityMainBinding activityMainBinding;
    private final List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Boolean isLoadingMore =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization() {
        isLoadingMore = false;
        activityMainBinding.tvShowRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        activityMainBinding.tvShowRecyclerView.setAdapter(tvShowsAdapter);
        activityMainBinding.tvShowRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!activityMainBinding.tvShowRecyclerView.canScrollVertically(1)) {
                    if (currentPage <= totalAvailablePages) {
                        currentPage += 1;
                        activityMainBinding.setIsLoadingMore(true);
                        isLoadingMore =true;
                        getMostPopularTVShows();
                    }
                }
            }
        });
        activityMainBinding.imageSearch.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SearchActivity.class)));
        activityMainBinding.imageWatchlist.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), WatchlistActivity.class)));
        getMostPopularTVShows();
    }

    private void getMostPopularTVShows() {

        activityMainBinding.setIsLoading(!isLoadingMore);

        viewModel.getMostPopularTVShows(currentPage).observe(this, mostPopularTVShowsResponse -> {

            activityMainBinding.setIsLoading(false);
            activityMainBinding.setIsLoadingMore(false);

            totalAvailablePages = mostPopularTVShowsResponse.getTotalPages();

            if (mostPopularTVShowsResponse.getTvShows() != null) {
                int oldCount = tvShows.size();
                tvShows.addAll(mostPopularTVShowsResponse.getTvShows());
                tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
            }
        });
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowsDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}