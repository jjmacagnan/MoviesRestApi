package com.jjmacagnan.moviesrestapi.database;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jjmacagnan.moviesrestapi.util.ImageUtils;
import com.jjmacagnan.moviesrestapi.listener.MovieFetchListener;
import com.jjmacagnan.moviesrestapi.model.Movie;
import com.jjmacagnan.moviesrestapi.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MovieDatabase extends SQLiteOpenHelper {

    private static final String TAG = MovieDatabase.class.getSimpleName();

    public MovieDatabase(Context context) {
        super(context, Constants.DATABASE.DB_NAME, null, Constants.DATABASE.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Constants.DATABASE.CREATE_TABLE_QUERY);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constants.DATABASE.DROP_QUERY);
        this.onCreate(db);
    }


    public void addMovie(Movie movie) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DATABASE.ID, movie.getId());
        values.put(Constants.DATABASE.VOTE_AVERAGE, Double.toString(movie.getVote_average()));
        values.put(Constants.DATABASE.TITLE, movie.getTitle());
        values.put(Constants.DATABASE.POSTER_URL, movie.getPoster_url());
        values.put(Constants.DATABASE.POSTER, ImageUtils.getPictureByteOfArray(movie.getPoster()));
        values.put(Constants.DATABASE.GENRES, Arrays.toString(movie.getGenres()));


        try {
            db.insert(Constants.DATABASE.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public void updateMovie(Movie movie) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DATABASE.BACKDROP_URL, movie.getBackdrop_url());
        values.put(Constants.DATABASE.BACKDROP, ImageUtils.getPictureByteOfArray(movie.getBackdrop_photo()));
        values.put(Constants.DATABASE.OVERVIEW, movie.getOverview());
        values.put(Constants.DATABASE.RELEASE_DATE, movie.getRelease_date());
        values.put(Constants.DATABASE.TAGLINE, movie.getTagline());


        try {
            db.update(Constants.DATABASE.TABLE_NAME, values, "ID = " + movie.getId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public void fetchMovies(MovieFetchListener listener) {
        MoviesFetcher fetcher = new MoviesFetcher(listener, this.getWritableDatabase());
        fetcher.start();
    }

    public void fetchMovie(MovieFetchListener listener, Movie movie) {
        MovieFetcher fetcher = new MovieFetcher(listener, this.getWritableDatabase(), movie);
        fetcher.start();
    }


    public class MoviesFetcher extends Thread {

        private final MovieFetchListener mListener;
        private final SQLiteDatabase mDb;

        MoviesFetcher(MovieFetchListener listener, SQLiteDatabase db) {
            mListener = listener;
            mDb = db;
        }

        @Override
        public void run() {
            Cursor cursor = mDb.rawQuery(Constants.DATABASE.GET_MOVIES_QUERY, null);

            final List<Movie> moviesList = new ArrayList<>();

            if (cursor.getCount() > 0) {

                if (cursor.moveToFirst()) {
                    do {
                        Movie movie = new Movie();
                        movie.setFromDatabase(true);
                        movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.ID))));
                        movie.setVote_average(Double.parseDouble(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.VOTE_AVERAGE))));
                        movie.setTitle(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.TITLE)));
                        movie.setPoster(ImageUtils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constants.DATABASE.POSTER))));
                        movie.setGenres(new String[]{cursor.getString(cursor.getColumnIndex(Constants.DATABASE.GENRES))});
                        movie.setBackdrop_photo(ImageUtils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constants.DATABASE.BACKDROP))));
                        movie.setOverview(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.OVERVIEW)));
                        movie.setRelease_date(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.RELEASE_DATE)));
                        movie.setTagline(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.TAGLINE)));

                        moviesList.add(movie);
                        publishMovie(movie);

                    } while (cursor.moveToNext());
                }
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Collections.sort(moviesList, new Comparator<Movie>() {
                        @Override
                        public int compare(Movie o1, Movie o2) {
                            return o1.getVote_average().compareTo(o2.getVote_average());
                        }
                    });

                    Collections.reverse(moviesList);

                    mListener.onDeliverAllMovies(moviesList);
                    mListener.onHideDialog();
                }
            });
        }

        void publishMovie(final Movie movies) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverMovie(movies);
                }
            });
        }
    }

    public class MovieFetcher extends Thread {

        private final MovieFetchListener mListener;
        private final SQLiteDatabase mDb;
        private final Movie mMovie;

        MovieFetcher(MovieFetchListener listener, SQLiteDatabase db, Movie movie) {
            mListener = listener;
            mDb = db;
            mMovie = movie;
        }

        @Override
        public void run() {
            Cursor cursor = mDb.rawQuery(Constants.DATABASE.GET_MOVIE + mMovie.getId(), null);

            if (cursor.getCount() > 0) {

                if (cursor.moveToFirst()) {
                    do {
                        mMovie.setFromDatabase(true);
                        mMovie.setBackdrop_photo(ImageUtils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constants.DATABASE.BACKDROP))));
                        mMovie.setOverview(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.OVERVIEW)));
                        mMovie.setRelease_date(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.RELEASE_DATE)));
                        mMovie.setTagline(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.TAGLINE)));


                    } while (cursor.moveToNext());

                }
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDetailsMovie(mMovie);
                    mListener.onHideDialog();
                }
            });


        }
    }
}
