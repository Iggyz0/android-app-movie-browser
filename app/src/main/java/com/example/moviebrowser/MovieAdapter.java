package com.example.moviebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context mContext;
    private List<MovieModel> mData;

    public MovieAdapter(Context mContext, List<MovieModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public List<MovieModel> getmData() {
        return mData;
    }

    public void setmData(List<MovieModel> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.movie_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Using Picasso (https://square.github.io/picasso/) to load image from a link into ImageView
        Picasso.get()
                .load("https://image.tmdb.org/t/p/original" + mData.get(position).getPoster_path())
                .resize(250,375)
                .into(holder.movieImage);
        holder.movieImage.setTag(mData.get(position).getTmdb_id());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView movieImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImage = itemView.findViewById(R.id.movieImage);
        }
    }

}
