package com.jikan.anime.ui.activity;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.jikan.anime.R;
import com.jikan.anime.databinding.AnimeDetailLayoutBinding;
import com.jikan.anime.entity.AnimeDetailResponse;
import com.jikan.anime.entity.JikanGenres;

import java.lang.reflect.Type;

public class AnimeDetailActivity extends DashboardActivity {
    private AnimeDetailLayoutBinding binding;
    private long anime_id;
    AnimeDetailResponse httpResponse;

    @Override
    protected void getArgs() {
        anime_id = getIntent().getLongExtra("anime_id", -1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void dataToView() {
        if (httpResponse == null) {
            return;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(httpResponse.data.title);
        }
        binding.trailer.setVisibility(View.GONE);
        binding.poster.setVisibility(View.GONE);
        if (httpResponse.data.trailer == null || httpResponse.data.trailer.embed_url == null) {
            binding.poster.setVisibility(View.VISIBLE);
            Glide.with(this).load(httpResponse.data.trailer.images.large_image_url).error(httpResponse.data.trailer.images.image_url).into(binding.poster);
            return;
        }
        binding.trailer.setVisibility(View.VISIBLE);
        binding.title.setText(httpResponse.data.title);
        binding.trailer.getSettings().setJavaScriptEnabled(true);
        binding.trailer.loadUrl(httpResponse.data.trailer.embed_url);
        binding.synopsis.setText(httpResponse.data.synopsis);
        binding.mainCast.setText(R.string.no_cast);
        binding.episodes.setText(getApplicationContext().getString(R.string.num_episodes, String.valueOf(httpResponse.data.episodes)));
        binding.rating.setText(getString(R.string.ratings, String.valueOf(httpResponse.data.score)));
        binding.ratingBar.setRating((float) httpResponse.data.score);
        binding.ratingBar.post(() -> {
            binding.ratingBar.setRating((float) httpResponse.data.score / 2);
        });
        //TO concat all genres Together
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(httpResponse.data.genres.stream().findFirst().orElse(new JikanGenres()).name);
        httpResponse.data.genres.stream().skip(1).forEach(jikanGenres -> {
            stringBuilder.append(", ").append(jikanGenres.name);
        });
        binding.genres.setText(getString(R.string.genres, stringBuilder.toString()));
    }

    @Override
    public void setBinding() {
        binding = AnimeDetailLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    public String getAjax() {
        return "https://api.jikan.moe/v4/anime/" + anime_id;
    }

    @Override
    public Type getType() {
        return AnimeDetailResponse.class;
    }

    @Override
    protected void networkCalls() {

        if (anime_id == -1) {
            return;
        }
        if (!isNetworkAvailable()) {
            showErrorDialog(getString(R.string.no_internet_connection));
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        makeServerCommunication(this.getCallBack());
    }

    private IHttpCallback<AnimeDetailResponse> getCallBack() {
        return new IHttpCallback<>() {
            @Override
            public void onHttpSuccess(AnimeDetailResponse response) {
                binding.progressBar.setVisibility(View.GONE);
                AnimeDetailActivity.this.httpResponse = response;
                dataToView();
                setAccessibility();
            }

            @Override
            public void onHttpFailure() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showErrorDialog(getString(R.string.network_error));
                });
            }
        };
    }
}
