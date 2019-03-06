package com.jjmacagnan.moviesrestapi.api;

import com.jjmacagnan.moviesrestapi.model.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {

    @GET("/movies")
    Call<List<Movie>> getAllMovies();

    @GET("/movies/{id}")
    Call<Movie> getMovie(@Path("id") int id);
}
