package com.android.example.tvshowsapp.responses;

import com.android.example.tvshowsapp.models.TVShowDetails;
import com.google.gson.annotations.SerializedName;

public class TVShowsDetailResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}
