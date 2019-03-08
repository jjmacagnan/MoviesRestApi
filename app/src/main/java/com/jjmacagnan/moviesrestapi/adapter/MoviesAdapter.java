package com.jjmacagnan.moviesrestapi.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjmacagnan.moviesrestapi.R;
import com.jjmacagnan.moviesrestapi.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.Holder> {

        private final MovieClickListener mListener;
        private List<Movie> mMovies;

        public MoviesAdapter(MovieClickListener listener) {
            mMovies = new ArrayList<>();
            mListener = listener;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
            return new Holder(row);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {



            Movie currMovie = mMovies.get(position);

            StringBuilder stringBuilder = new StringBuilder();

            holder.mName.setText(currMovie.getTitle());
            holder.mVoteAverage.setText(String.valueOf(currMovie.getVote_average()));

            for (String s: currMovie.getGenres()) {
               stringBuilder.append(s).append("-");
            }

            holder.mGenres.setText(stringBuilder);

            if (currMovie.isFromDatabase()) {
                holder.mPhoto.setImageBitmap(currMovie.getPoster());
            } else {
                Picasso.with(holder.itemView.getContext()).load(currMovie.getPoster_url()).into(holder.mPhoto);
            }

        }

        @Override
        public int getItemCount() {
            return mMovies.size();
        }

        public void addMovie(Movie movie) {
            mMovies.add(movie);

            /*Adiconado ordenção da lista pela média de votos,
             * conforme enviado pela api*/

            Collections.sort(mMovies, new Comparator<Movie>() {
                @Override
                public int compare(Movie o1, Movie o2) {
                    return o1.getVote_average().compareTo(o2.getVote_average());
                }
            });

            Collections.reverse(mMovies);

            notifyDataSetChanged();
        }


        public Movie getSelectedMovie(int position) {
            return mMovies.get(position);
        }

        public void reset() {
            mMovies.clear();
            notifyDataSetChanged();
        }

        public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView mPhoto;
            private TextView mName, mVoteAverage, mGenres;

            Holder(View itemView) {
                super(itemView);
                mPhoto = itemView.findViewById(R.id.moviePhoto);
                mName = itemView.findViewById(R.id.movieName);
                mVoteAverage = itemView.findViewById(R.id.voteAverage);
                mGenres = itemView.findViewById(R.id.genres);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mListener.onClick(getLayoutPosition());
            }
        }

        public interface MovieClickListener {

            void onClick(int position);
        }
}

