package com.jjmacagnan.moviesrestapi.util;

public class Constants {

    public static final class HTTP {
        public static final String BASE_URL = "https://desafio-mobile.nyc3.digitaloceanspaces.com";
    }

    public static final class DATABASE {

        public static final String DB_NAME = "movies_tmdb";
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME = "movie";

        public static final String DROP_QUERY = "DROP TABLE IF EXIST " + TABLE_NAME;

        public static final String GET_MOVIES_QUERY = "SELECT * FROM " + TABLE_NAME;

        public static final String GET_MOVIE = "SELECT backdrop, overview, popularity, release_date, revenue, tagline FROM "  + TABLE_NAME + " WHERE ID = ";

        public static final String ID = "id";
        public static final String VOTE_AVERAGE = "vote_average";
        public static final String TITLE = "title";
        public static final String POSTER_URL = "poster_url";
        public static final String POSTER = "poster";
        public static final String GENRES = "genres";
        public static final String BACKDROP_URL = "backdrop_url";
        public static final String BACKDROP = "backdrop";
        public static final String OVERVIEW = "overview";
        public static final String RELEASE_DATE = "release_date";
        public static final String TAGLINE = "tagline";
        public static final String REVENUE = "revenue";
        public static final String POPULARITY = "popularity";



        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "" +
                "(" + ID + " INTEGER PRIMARY KEY not null," +
                VOTE_AVERAGE + " TEXT," +
                TITLE + " TEXT," +
                POSTER_URL + " TEXT," +
                POSTER + " blob," +
                GENRES + " TEXT," +
                BACKDROP_URL + " TEXT," +
                BACKDROP + " blob," +
                OVERVIEW + " TEXT," +
                POPULARITY + " TEXT," +
                RELEASE_DATE + " TEXT," +
                REVENUE + " TEXT," +
                TAGLINE + " TEXT)";
    }

    public static final class REFERENCE {
        public static final String MOVIE = Config.PACKAGE_NAME + "movie";
    }

    public static final class Config {
        public static final String PACKAGE_NAME = "com.jjmacagnan.moviesrestapi.model.";
    }


}
