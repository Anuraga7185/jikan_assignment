package com.jikan.anime.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jikan.anime.R;
import com.jikan.anime.databinding.AnimeRowLayoutBinding;
import com.jikan.anime.databinding.DashboardActivityBinding;
import com.jikan.anime.entity.AnimeDetail;
import com.jikan.anime.entity.JikanResponse;
import com.jikan.anime.ui.adapter.GenericRecyclerAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {
    private DashboardActivityBinding binding;
    protected final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    protected final Executor executor = Executors.newSingleThreadExecutor();
    private JikanResponse httpResponse;

    public void setBinding() {
        binding = DashboardActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    protected void getArgs() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArgs();
        setBinding();
        setClickListeners();
        setAccessibility();
        dataToView();
        networkCalls();

    }

    protected void networkCalls() {
        binding.progressBar.setVisibility(View.VISIBLE);
        makeServerCommunication(this.getCallback());
    }

    private IHttpCallback<JikanResponse> getCallback() {
        return new IHttpCallback<>() {
            @Override
            public void onHttpSuccess(JikanResponse response) {
                DashboardActivity.this.httpResponse = response;
                binding.progressBar.setVisibility(View.GONE);
                dataToView();
            }

            @Override
            public void onHttpFailure() {
                binding.progressBar.setVisibility(View.GONE);
            }
        };
    }

    public void setAccessibility() {
    }

    public void setClickListeners() {

    }

    public void dataToView() {
        if (httpResponse == null) {
            return;
        }
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        binding.recyclerView.setAdapter(new GenericRecyclerAdapter<>(httpResponse.data, R.layout.anime_row_layout, this::createRecyclerRow));

    }

    public String getAjax() {
        return "https://api.jikan.moe/v4/top/anime";
    }

    private void createRecyclerRow(View view, AnimeDetail item, int position) {
        AnimeRowLayoutBinding layoutBinding = AnimeRowLayoutBinding.bind(view);
        layoutBinding.ratingBar.setRating((float) item.score);
        Glide.with(this).load(item.images.jpg.image_url).error(item.images.jpg.small_image_url).into(layoutBinding.animeImage);
        layoutBinding.animeName.setText(item.title);
        layoutBinding.episodes.setText(getString(R.string.num_episodes, String.valueOf(item.episodes)));
        layoutBinding.animeReviews.setText(getString(R.string.ratings, String.valueOf(item.score)));
        layoutBinding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AnimeDetailActivity.class);
            intent.putExtra("anime_id", item.mal_id);
            startActivity(intent);
        });
    }

    public Type getType() {
        return JikanResponse.class;
    }

    protected void makeServerCommunication(IHttpCallback callback) {

        executor.execute(() -> {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(getAjax());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                String jsonData = result.toString();
                // To Jikan Response
                Gson gson = new Gson();
                mainThreadHandler.post(() -> callback.onHttpSuccess(gson.fromJson(jsonData, getType())));

            } catch (Exception e) {
                callback.onHttpFailure();
                Log.e("Dashboard Activity", "Error fetching data", e);
            }
        });
    }


    public interface IHttpCallback<T> {
        void onHttpSuccess(T response);

        void onHttpFailure();
    }
}
