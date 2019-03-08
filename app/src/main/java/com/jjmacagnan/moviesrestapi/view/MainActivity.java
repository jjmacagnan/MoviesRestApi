package com.jjmacagnan.moviesrestapi.view;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjmacagnan.moviesrestapi.R;
import com.jjmacagnan.moviesrestapi.adapter.MoviesAdapter;
import com.jjmacagnan.moviesrestapi.api.ApiService;
import com.jjmacagnan.moviesrestapi.api.ServiceFactory;
import com.jjmacagnan.moviesrestapi.database.MovieDatabase;
import com.jjmacagnan.moviesrestapi.listener.MovieFetchListener;
import com.jjmacagnan.moviesrestapi.model.Movie;
import com.jjmacagnan.moviesrestapi.util.NetworkStateReceiver;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements MoviesAdapter.MovieClickListener, MovieFetchListener {

    //private static final String TAG = MainActivity.class.getSimpleName();
    private MoviesAdapter mMoviesAdapter;
    private MovieDatabase mDatabase;
    private Button mReload;
    private ProgressDialog mDialog;
    private NetworkStateReceiver mStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configViews();

        mStateReceiver = new NetworkStateReceiver();
        mDatabase = new MovieDatabase(this);

        loadMovieFeed();

        mReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMovieFeed();
            }
        });
    }

    private void loadMovieFeed() {

        mMoviesAdapter.reset();
        showMessage(getString(R.string.loading_movie_data));

        if (NetworkStateReceiver.isNetworkAvailable()) {
            getMovieList();
        } else {
            getFeedFromDatabase();
        }
    }

    private void showMessage(String message) {
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage(message);
        mDialog.setCancelable(true);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);

        mDialog.show();
    }

    private void getFeedFromDatabase() {
        mDatabase.fetchMovies(this);
    }

    private void configViews() {
        mReload = findViewById(R.id.reload);
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mMoviesAdapter = new MoviesAdapter(this);

        mRecyclerView.setAdapter(mMoviesAdapter);
    }

    @Override
    public void onClick(int position) {
        final Movie selectedMovie = mMoviesAdapter.getSelectedMovie(position);
        final Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        showMessage(getString(R.string.loading_movie_details));

        ApiService service = ServiceFactory.getInstance();

        Call<Movie> movieCall = service.getMovie(selectedMovie.getId());

        movieCall.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {

                if (response.isSuccessful()) {
                    Movie movie = response.body();

                    UpdateDatabase task = new UpdateDatabase();
                    task.execute(movie);

                    intent.putExtra("Movie", movie);
                    startActivity(intent);
                } else requestError(response.code());

                mDialog.dismiss();

            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void requestError(int code) {
        switch (code) {
            case 400:
                Log.e("Error 400", "Bad Request");
                break;
            case 404:
                Log.e("Error 404", "Not Found");
                break;
            default:
                Log.e("Error", "Generic Error");
        }
    }

    public void getMovieList() {

        ApiService service = ServiceFactory.getInstance();
        Call<List<Movie>> listCall = service.getAllMovies();
        listCall.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(@NonNull Call<List<Movie>> call, @NonNull Response<List<Movie>> response) {
                if (response.isSuccessful()) {
                    List<Movie> movieList = response.body();

                    assert movieList != null;
                    for (int i = 0; i < movieList.size(); i++) {
                        Movie movie = movieList.get(i);

                        SaveIntoDatabase task = new SaveIntoDatabase();
                        task.execute(movie);

                        mMoviesAdapter.addMovie(movie);
                    }
                } else {
                    requestError(response.code());
                }
                mDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<Movie>> call, @NonNull Throwable t) {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });
    }


    @Override
    public void onDeliverAllMovies(List<Movie> movies) {

    }

    @Override
    public void onDeliverMovie(Movie movie) {
        mMoviesAdapter.addMovie(movie);
    }

    @Override
    public void onHideDialog() {
        mDialog.dismiss();
    }


    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mStateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDialog.dismiss();
        unregisterReceiver(mStateReceiver);
    }

    @SuppressLint("StaticFieldLeak")
    public class SaveIntoDatabase extends AsyncTask<Movie, Void, Void> {


        private final String TAG = SaveIntoDatabase.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Movie... params) {

            Movie movie = params[0];

            try {
                InputStream stream = new URL(movie.getPoster_url()).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                movie.setPoster(bitmap);
                mDatabase.addMovie(movie);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class UpdateDatabase extends AsyncTask<Movie, Void, Void> {


        private final String TAG = SaveIntoDatabase.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Movie... params) {

            Movie movie = params[0];

            try {
                InputStream stream = new URL(movie.getBackdrop_url()).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                movie.setBackdrop_photo(bitmap);
                mDatabase.updateMovie(movie);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }
    }
}
