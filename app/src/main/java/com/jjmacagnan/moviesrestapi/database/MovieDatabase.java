package com.jjmacagnan.moviesrestapi.database;


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

    public void addMovie(Movie movies) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.DATABASE.ID, movies.getId());
        values.put(Constants.DATABASE.VOTE_AVERAGE,Double.toString(movies.getVote_average()));
        values.put(Constants.DATABASE.TITLE, movies.getTitle());
        values.put(Constants.DATABASE.POSTER_URL, movies.getPoster_url());
        values.put(Constants.DATABASE.POSTER, ImageUtils.getPictureByteOfArray(movies.getPoster()));
        values.put(Constants.DATABASE.GENRES, Arrays.toString(movies.getGenres()));
//        values.put(Constants.DATABASE.BACKDROP_URL, movies.getBackdrop_url());
//        values.put(Constants.DATABASE.BACKDROP, ImageUtils.getPictureByteOfArray(movies.getBackdrop_photo()));
//        values.put(Constants.DATABASE.OVERVIEW, movies.getOverview());
//        values.put(Constants.DATABASE.RELEASE_DATE, movies.getRelease_date());
//        values.put(Constants.DATABASE.TAGLINE, movies.getTagline());




        try {
            db.insert(Constants.DATABASE.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public void fetchMovies(MovieFetchListener listener) {
        MovieFetcher fetcher = new MovieFetcher(listener, this.getWritableDatabase());
        fetcher.start();
    }

    public class MovieFetcher extends Thread {

        private final MovieFetchListener mListener;
        private final SQLiteDatabase mDb;

        public MovieFetcher(MovieFetchListener listener, SQLiteDatabase db) {
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
                        movie.setPoster_url(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.POSTER_URL)));
                        movie.setPoster(ImageUtils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constants.DATABASE.POSTER))));
                        movie.setGenres(new String[]{cursor.getString(cursor.getColumnIndex(Constants.DATABASE.GENRES))});
//                        movie.setBackdrop_url(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.BACKDROP_URL)));
//                        movie.setBackdrop_photo(ImageUtils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constants.DATABASE.BACKDROP))));
//                        movie.setOverview(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.OVERVIEW)));
//                        movie.setRelease_date(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.RELEASE_DATE)));
//                        movie.setTagline(cursor.getString(cursor.getColumnIndex(Constants.DATABASE.TAGLINE)));

                        moviesList.add(movie);
                        publishMovie(movie);

                    } while (cursor.moveToNext());
                }
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverAllMovie(moviesList);
                    mListener.onHideDialog();
                }
            });
        }

        public void publishMovie(final Movie movies) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverMovie(movies);
                }
            });
        }
    }
}
