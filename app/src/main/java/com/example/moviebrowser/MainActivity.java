package com.example.moviebrowser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<MovieModel> movieList;
    RecyclerView recyclerView;
    BottomNavigationView bottomNavView;
    Button loadMoreButton;
    String latestUrl = "";
    int pageNumber = 2;

    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        myToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        return false;
                    default:
                        loadMoreButton.setVisibility(View.INVISIBLE);
                        String sufix = getResources().getResourceName(item.getItemId()).replaceFirst("^.+\\/g", "");
                        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + TmdbApi.getTmdbApiKey() + "&with_genres=" + sufix;

                        // on click in the genre menu - addToRecycler
                        setMoviesByGenreIntoRecycler(url);

                        return false;
                }
            }
        });

        recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                    totalItemCount = recyclerView.getLayoutManager().getItemCount();

                    GridLayoutManager layoutManager = ((GridLayoutManager)recyclerView.getLayoutManager());
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    loading = true;
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            loadMoreButton = findViewById(R.id.buttonLoadMore);
                            loadMoreButton.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });


        bottomNavView = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                View i1 = findViewById(R.id.page_popular);
                View i2 = findViewById(R.id.page_my_list);
                switch (item.getItemId()) {
                    case R.id.page_popular:
                        pageNumber = 2;
                        i1.setBackgroundColor(getResources().getColor(R.color.light_gray));
                        i2.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                        setPopularIntoRecycler();
                        break;
                    case R.id.page_my_list:
                        loadMoreButton.setVisibility(View.INVISIBLE);
                        i2.setBackgroundColor(getResources().getColor(R.color.light_gray));
                        i1.setBackgroundColor(getResources().getColor(R.color.dark_gray));
                        setMyFavouritesIntoRecycler();
                        break;
                }
                return false;
            }
        });
        initComponents();
    }

    private void initComponents() {
        View i1 = findViewById(R.id.page_popular);
        i1.setBackgroundColor(getResources().getColor(R.color.light_gray));
        setPopularIntoRecycler();
        loadMoreButton = findViewById(R.id.buttonLoadMore);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore();
                pageNumber++;
                loadMoreButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search movies...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                setSearchResultsIntoRecycler(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @SuppressLint("HandlerLeak")
    private void setPopularIntoRecycler() {
        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewMain);
        TmdbApi.getJSON("https://api.themoviedb.org/3/discover/movie?api_key=" + TmdbApi.getTmdbApiKey() + "&sort_by=popularity.desc", new ReadDataHandler(){
            @Override
            public void handleMessage(Message msg) {
                String response = getJson();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject currentMovieObj = jsonArray.getJSONObject(i);
                        MovieModel movieModel = MovieModel.parseJSONObject(currentMovieObj);

                        movieList.add(movieModel);
                    }

                    fillRecyclerView(movieList);

                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            // do it
                            String tmdb_id = movieList.get(position).getTmdb_id();
                            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class).putExtra("tmdb_id", tmdb_id);
                            startActivity(intent);
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //
    }

    private void setMyFavouritesIntoRecycler() {
        pageNumber = 2;
        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewMain);
        DataBase db = new DataBase(this);
        movieList = db.getAllFavouriteMovies();

        if (movieList.size() > 0) {
            fillRecyclerView(movieList);

            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    // do it
                    String tmdb_id = movieList.get(position).getTmdb_id();
                    Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class).putExtra("tmdb_id", tmdb_id);
                    startActivity(intent);
                }
            });
        } else {
            //
        }

    }

    @SuppressLint("HandlerLeak")
    private void setSearchResultsIntoRecycler(String query) {
        pageNumber = 2;
        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewMain);
        TmdbApi.getJSON("https://api.themoviedb.org/3/search/movie?api_key=" + TmdbApi.getTmdbApiKey() + "&language=en-US&query=" + query + "&page=1&include_adult=false", new ReadDataHandler(){
            @Override
            public void handleMessage(Message msg) {
                String response = getJson();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject currentMovieObj = jsonArray.getJSONObject(i);
                        MovieModel movieModel = MovieModel.parseJSONObject(currentMovieObj);

                        movieList.add(movieModel);
                    }

                    fillRecyclerView(movieList);

                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            // do it
                            String tmdb_id = movieList.get(position).getTmdb_id();
                            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class).putExtra("tmdb_id", tmdb_id);
                            startActivity(intent);
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //
    }

    @SuppressLint("HandlerLeak")
    private void setMoviesByGenreIntoRecycler(String url) {
        pageNumber = 2;
        movieList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewMain);
        TmdbApi.getJSON(url, new ReadDataHandler(){
            @Override
            public void handleMessage(Message msg) {
                String response = getJson();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject currentMovieObj = jsonArray.getJSONObject(i);
                        MovieModel movieModel = MovieModel.parseJSONObject(currentMovieObj);

                        movieList.add(movieModel);
                    }

                    fillRecyclerView(movieList);

                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            // do it
                            String tmdb_id = movieList.get(position).getTmdb_id();
                            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class).putExtra("tmdb_id", tmdb_id);
                            startActivity(intent);
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //
    }

    private void fillRecyclerView(List<MovieModel> movieList) {
        MovieAdapter movieAdapter = new MovieAdapter(this, movieList);
        movieAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4)); // Using GridLayoutManager to fill the RecyclerView
        recyclerView.setAdapter(movieAdapter);
    }

    @SuppressLint("HandlerLeak")
    private void loadMore() {
        recyclerView = findViewById(R.id.recyclerViewMain);
        TmdbApi.getJSON("https://api.themoviedb.org/3/discover/movie?api_key=" + TmdbApi.getTmdbApiKey() + "&adult=false&sort_by=popularity.desc&page=" + pageNumber, new ReadDataHandler(){
            @Override
            public void handleMessage(Message msg) {
                String response = getJson();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject currentMovieObj = jsonArray.getJSONObject(i);
                        MovieModel movieModel = MovieModel.parseJSONObject(currentMovieObj);

                        movieList.add(movieModel);
                    }

                    fillRecyclerView(movieList);

                    ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            // do it
                            String tmdb_id = movieList.get(position).getTmdb_id();
                            Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class).putExtra("tmdb_id", tmdb_id);
                            startActivity(intent);
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}