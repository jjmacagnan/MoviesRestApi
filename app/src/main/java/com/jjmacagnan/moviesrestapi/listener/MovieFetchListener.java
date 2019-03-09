package com.jjmacagnan.moviesrestapi.listener;


import com.jjmacagnan.moviesrestapi.model.Movie;

import java.util.List;

public interface MovieFetchListener {

    void onDeliverAllMovies(List<Movie> movies);

    void onDeliverMovie(Movie movie);

    void onHideDialog();

    void onDetailsMovie(Movie mMovie);
}
