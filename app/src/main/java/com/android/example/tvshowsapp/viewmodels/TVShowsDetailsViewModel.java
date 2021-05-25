package com.android.example.tvshowsapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.android.example.tvshowsapp.database.TVShowsDatabase;
import com.android.example.tvshowsapp.models.TVShow;
import com.android.example.tvshowsapp.repositories.TVShowsDetailsRepository;
import com.android.example.tvshowsapp.responses.TVShowsDetailResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowsDetailsViewModel extends AndroidViewModel {

    private final TVShowsDetailsRepository tvShowsDetailsRepository;
    private final TVShowsDatabase tvShowsDatabase;

    public TVShowsDetailsViewModel(@NonNull Application application){
        super(application);
        tvShowsDetailsRepository= new TVShowsDetailsRepository();
        tvShowsDatabase= TVShowsDatabase.getTvShowsDatabase(application);
    }

    public LiveData<TVShowsDetailResponse> getTVShowsDetails(String tvShowId){
        return tvShowsDetailsRepository.getTVShowsDetails(tvShowId);
    }
    public Completable addToWatchlist(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().addToWatchlist(tvShow);
    }

    public Flowable<TVShow> getTVShowFromWatchlist(String tvShowId){
        return tvShowsDatabase.tvShowDao().getTVShowFromWatchlist(tvShowId);
    }
    public Completable removeTVShowFromWatchlist(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }
}
