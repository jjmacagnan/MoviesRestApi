package com.jjmacagnan.moviesrestapi.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjmacagnan.moviesrestapi.R;
import com.jjmacagnan.moviesrestapi.model.Movie;
import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {

    private ImageView mPhoto;
    private TextView mOverview, mReleaseDate, mTagline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        Movie movie = (Movie) intent.getSerializableExtra("Movie");


        configViews();

        mReleaseDate.setText(movie.getRelease_date());
        mOverview.setText(movie.getOverview());
        mTagline.setText(movie.getTagline());
        mTagline.setText(movie.getTagline());

        if (movie.isFromDatabase()) {
            mPhoto.setImageBitmap(movie.getBackdrop_photo());
        } else {
            Picasso.with(getApplicationContext()).load(movie.getBackdrop_url()).into(mPhoto);
        }
    }

    private void configViews() {
        mPhoto = findViewById(R.id.backDropUrl);
        mOverview = findViewById(R.id.overview);
        mReleaseDate = findViewById(R.id.release_date);
        mTagline = findViewById(R.id.tagline);

    }
}
