package com.example.moviebrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageViewBackdrop;
    ImageView imageViewPoster;
    TextView textViewTitle;
    TextView labelTmdbId;
    TextView labelImdbId;
    TextView labelGenres;
    TextView releaseDate;
    TextView score;
    TextView synopsis;
    ImageView star;
    ImageView webview;
    DataBase db = new DataBase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        initComp();
    }

    @SuppressLint("HandlerLeak")
    private void initComp() {
        Intent i = getIntent();
        String tmdb_id = i.getStringExtra("tmdb_id");

        imageViewBackdrop = findViewById(R.id.imageViewBackdrop);
        imageViewPoster = findViewById(R.id.imageViewPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        labelTmdbId = findViewById(R.id.valueTmdbId);
        labelImdbId = findViewById(R.id.valueImdbId);
        labelGenres = findViewById(R.id.valueGenres);
        releaseDate = findViewById(R.id.valueReleaseDate);
        score = findViewById(R.id.valueScore);
        synopsis = findViewById(R.id.valueSynopsis);

        star = findViewById(R.id.imageViewStar);
        star.setOnClickListener(this);

        webview = findViewById(R.id.imageViewWebview);
        webview.setOnClickListener(this);

        TmdbApi.getJSON("https://api.themoviedb.org/3/movie/" + tmdb_id + "?api_key=" + TmdbApi.getTmdbApiKey(), new ReadDataHandler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                String response = getJson();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    MovieModel movieModel = MovieModel.parseJSONObject(jsonObject);
                    Picasso.get()
                            .load("https://image.tmdb.org/t/p/original" + movieModel.getBackdrop_path())
                            .fit()
                            .centerCrop()
                            .into(imageViewBackdrop);

                    String poster_path = "https://image.tmdb.org/t/p/original" + movieModel.getPoster_path();
                    Picasso.get()
                            .load(poster_path)
                            .resize(250,375)
                            .into(imageViewPoster);
                    imageViewPoster.setTag(movieModel.getPoster_path());

                    textViewTitle.setText(movieModel.getTitle());
                    labelTmdbId.setText(movieModel.getTmdb_id());
                    labelImdbId.setText(movieModel.getImdb_id());
                    labelGenres.setText(movieModel.getGenres());
                    releaseDate.setText(movieModel.getRelease_date());
                    score.setText(Double.toString(movieModel.getScore()));
                    synopsis.setText(movieModel.getSynopsis());

                    if (db.getMovieById(movieModel.getTmdb_id())) {
                        star.setImageDrawable(getResources().getDrawable(R.drawable.star_yellow));
                    }
                    else {
                        star.setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        String tmdb_id = (String)labelTmdbId.getText();
        String poster_path = (String)imageViewPoster.getTag();
        switch (v.getId()) {
            case R.id.imageViewStar:
                if (db.getMovieById(tmdb_id)) {
                    db.deleteMovieFromFavourites(tmdb_id);
                    star.setImageDrawable(getResources().getDrawable(R.drawable.star_empty));
                    Toast.makeText(getApplicationContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                }
                else {
                    db.addMovieToFavourites(tmdb_id, poster_path);
                    star.setImageDrawable(getResources().getDrawable(R.drawable.star_yellow));
                    Toast.makeText(getApplicationContext(),"Added to favourites",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.imageViewWebview:
                Intent intent = new Intent(MovieDetailsActivity.this, WebViewActivity.class).putExtra("tmdb_id", tmdb_id);
                startActivity(intent);
                break;
        }
    }
}