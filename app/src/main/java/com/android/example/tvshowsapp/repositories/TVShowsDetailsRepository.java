package com.android.example.tvshowsapp.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.example.tvshowsapp.network.ApiClient;
import com.android.example.tvshowsapp.network.ApiService;
import com.android.example.tvshowsapp.responses.TVShowsDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowsDetailsRepository {

    private ApiService apiService;

    public TVShowsDetailsRepository() {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowsDetailResponse> getTVShowsDetails(String tvShowId) {
        MutableLiveData<TVShowsDetailResponse> data = new MutableLiveData<>();
        apiService.getTVShowsDetails(tvShowId).enqueue(new Callback<TVShowsDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsDetailResponse> call, @NonNull Response<TVShowsDetailResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsDetailResponse> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
