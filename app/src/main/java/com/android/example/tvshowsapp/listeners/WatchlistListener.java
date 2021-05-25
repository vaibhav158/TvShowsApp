package com.android.example.tvshowsapp.listeners;

import com.android.example.tvshowsapp.models.TVShow;

public interface WatchlistListener {

    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchlist(TVShow tvShow, int position);
}
