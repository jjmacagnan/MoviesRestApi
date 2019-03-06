package com.jjmacagnan.moviesrestapi.api;

import com.jjmacagnan.moviesrestapi.util.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceFactory {

    public static ApiService getInstance() {

        String baseUrl = Constants.HTTP.BASE_URL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}
