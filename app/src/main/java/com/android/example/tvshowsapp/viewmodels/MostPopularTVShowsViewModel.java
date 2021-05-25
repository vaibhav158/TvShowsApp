package com.android.example.tvshowsapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.android.example.tvshowsapp.repositories.MostPopularTVShowsRepositories;
import com.android.example.tvshowsapp.responses.TVShowsResponse;

public class MostPopularTVShowsViewModel extends ViewModel {

    private final MostPopularTVShowsRepositories mostPopularTVShowsRepositories;

    public MostPopularTVShowsViewModel(){
        mostPopularTVShowsRepositories= new MostPopularTVShowsRepositories();
    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page){
        return mostPopularTVShowsRepositories.getMostPopularTVShows(page);
    }

}
