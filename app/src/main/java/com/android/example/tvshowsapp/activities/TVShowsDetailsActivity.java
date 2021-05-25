package com.android.example.tvshowsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.example.tvshowsapp.R;
import com.android.example.tvshowsapp.adapters.EpisodesAdapter;
import com.android.example.tvshowsapp.adapters.ImageSliderAdapter;
import com.android.example.tvshowsapp.databinding.ActivityTvshowsDetailsBinding;
import com.android.example.tvshowsapp.databinding.LayoutEpisodesBottomSheetBinding;
import com.android.example.tvshowsapp.models.TVShow;
import com.android.example.tvshowsapp.utilities.TempDataHolder;
import com.android.example.tvshowsapp.viewmodels.TVShowsDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowsDetailsActivity extends AppCompatActivity {

    private ActivityTvshowsDetailsBinding activityTvshowsDetailsBinding;
    private TVShowsDetailsViewModel tvShowsDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowAvailableInWatchlist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowsDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshows_details);
        doInitialization();
    }

    private void checkTVShowInWatchlist() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowsDetailsViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow -> {
                    isTVShowAvailableInWatchlist = true;
                    activityTvshowsDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                    compositeDisposable.dispose();
                }));
    }

    private void doInitialization() {
        activityTvshowsDetailsBinding.imageBack.setOnClickListener(v -> onBackPressed());
        tvShowsDetailsViewModel = new ViewModelProvider(this).get(TVShowsDetailsViewModel.class);
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        getTVShowDetails();
        checkTVShowInWatchlist();
    }

    private void getTVShowDetails() {
        activityTvshowsDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowsDetailsViewModel.getTVShowsDetails(tvShowId).observe(

                this,
                tvShowsDetailResponse -> {
                    if (tvShowsDetailResponse.getTvShowDetails().getPictures() != null) {
                        loadSliderImage(tvShowsDetailResponse.getTvShowDetails().getPictures());
                    }
                    activityTvshowsDetailsBinding.setIsLoading(false);
                    if (tvShowsDetailResponse.getTvShowDetails() != null) {
                        activityTvshowsDetailsBinding.setTvShowImageURL(
                                tvShowsDetailResponse.getTvShowDetails().getImagePath()
                        );
                        activityTvshowsDetailsBinding.imageTvShow.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.setDescription(
                                String.valueOf(
                                        HtmlCompat.fromHtml(
                                                tvShowsDetailResponse.getTvShowDetails().getDescription(),
                                                HtmlCompat.FROM_HTML_MODE_LEGACY
                                        )
                                )
                        );
                        activityTvshowsDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.textReadMore.setOnClickListener(v -> {
                            if (activityTvshowsDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                                activityTvshowsDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                activityTvshowsDetailsBinding.textDescription.setEllipsize(null);
                                activityTvshowsDetailsBinding.textReadMore.setText(R.string.read_less);
                            } else {
                                activityTvshowsDetailsBinding.textDescription.setMaxLines(4);
                                activityTvshowsDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                activityTvshowsDetailsBinding.textReadMore.setText(R.string.read_more);
                            }
                        });
                        activityTvshowsDetailsBinding.setRating(
                                String.format(
                                        Locale.getDefault(),
                                        "%.2f",
                                        Double.parseDouble(tvShowsDetailResponse.getTvShowDetails().getRating())
                                )
                        );
                        if (tvShowsDetailResponse.getTvShowDetails().getGenres() != null) {
                            activityTvshowsDetailsBinding.setGenre(tvShowsDetailResponse.getTvShowDetails().getGenres()[0]);
                        } else {
                            activityTvshowsDetailsBinding.setGenre("N/A");
                        }

                        activityTvshowsDetailsBinding.setRuntime(tvShowsDetailResponse.getTvShowDetails().getRuntime() + " min");
                        activityTvshowsDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.buttonWebsite.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(tvShowsDetailResponse.getTvShowDetails().getUrl()));
                            startActivity(intent);
                        });
                        activityTvshowsDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                        activityTvshowsDetailsBinding.buttonEpisodes.setOnClickListener(v -> {
                            if (episodesBottomSheetDialog == null) {
                                episodesBottomSheetDialog = new BottomSheetDialog(TVShowsDetailsActivity.this);
                                layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                        LayoutInflater.from(TVShowsDetailsActivity.this),
                                        R.layout.layout_episodes_bottom_sheet,
                                        findViewById(R.id.episodesContainer),
                                        false
                                );
                                episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                        new EpisodesAdapter(tvShowsDetailResponse.getTvShowDetails().getEpisodes())
                                );
                                layoutEpisodesBottomSheetBinding.textTitle.setText(
                                        String.format(
                                                "Episodes | %s", tvShow.getName()
                                        )
                                );
                                layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v1 -> episodesBottomSheetDialog.dismiss());
                            }

                            //--------Optional Section start--------//
                            FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                            if (frameLayout != null) {
                                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                            //------Optional Section end-----//
                            episodesBottomSheetDialog.show();
                        });

                        activityTvshowsDetailsBinding.imageWatchlist.setOnClickListener(v -> {


                            CompositeDisposable compositeDisposable = new CompositeDisposable();
                            if (isTVShowAvailableInWatchlist) {

                                compositeDisposable.add(tvShowsDetailsViewModel.removeTVShowFromWatchlist(tvShow)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            isTVShowAvailableInWatchlist = false;
                                            TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                            activityTvshowsDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_watchlist);
                                            Toast.makeText(getApplicationContext(), "Removed from watchlist", Toast.LENGTH_SHORT).show();
                                            compositeDisposable.dispose();
                                        }));

                            } else {
                                compositeDisposable.add(tvShowsDetailsViewModel.addToWatchlist(tvShow)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                                    activityTvshowsDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                                                    Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                                                    compositeDisposable.dispose();
                                                }
                                        ));
                            }

                        });
                        activityTvshowsDetailsBinding.imageWatchlist.setVisibility(View.VISIBLE);
                    }
                    loadBasicTVShowDetails();
                }
        );
    }

    private void loadSliderImage(String[] sliderImages) {
        activityTvshowsDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvshowsDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTvshowsDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvshowsDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicator(sliderImages.length);
        activityTvshowsDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSlideIndicator(position);
            }
        });
    }

    private void setupSliderIndicator(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {

            indicators[i] = new ImageView(getApplicationContext());

            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive)
            );
            indicators[i].setLayoutParams(layoutParams);
            activityTvshowsDetailsBinding.layoutSliderIndicator.addView(indicators[i]);
        }
        activityTvshowsDetailsBinding.layoutSliderIndicator.setVisibility(View.VISIBLE);
        setCurrentSlideIndicator(0);
    }

    private void setCurrentSlideIndicator(int position) {
        int childCount = activityTvshowsDetailsBinding.layoutSliderIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTvshowsDetailsBinding.layoutSliderIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.background_slider_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.background_slider_indicator_inactive
                ));
            }
        }
    }

    private void loadBasicTVShowDetails() {
        activityTvshowsDetailsBinding.setTvShowName(tvShow.getName());
        activityTvshowsDetailsBinding.setNetworkCountry(
                tvShow.getNetwork() + "(" + tvShow.getCountry() + ")"
        );
        activityTvshowsDetailsBinding.setStatus(tvShow.getStatus());
        activityTvshowsDetailsBinding.setStartDate(tvShow.getStartDate());
        activityTvshowsDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTvshowsDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowsDetailsBinding.textStarted.setVisibility(View.VISIBLE);
        activityTvshowsDetailsBinding.textStatus.setVisibility(View.VISIBLE);
    }
}