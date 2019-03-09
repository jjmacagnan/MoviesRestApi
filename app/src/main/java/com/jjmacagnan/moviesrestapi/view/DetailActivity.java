package com.jjmacagnan.moviesrestapi.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjmacagnan.moviesrestapi.R;
import com.jjmacagnan.moviesrestapi.model.Movie;
import com.jjmacagnan.moviesrestapi.util.Constants;
import com.squareup.picasso.Picasso;


public class DetailActivity extends AppCompatActivity {

    private ImageView mPhoto;
    private TextView mOverview, mReleaseDate, mTagline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        boolean isFromDatase = intent.getBooleanExtra("isFromDatabase", false);
        String overview = intent.getStringExtra("overview");
        String release_date = intent.getStringExtra("release_date");
        String tagline = intent.getStringExtra("tagline");
        String backdrop_url = intent.getStringExtra("backdrop_url");

        configViews();

        mReleaseDate.setText(release_date);
        mOverview.setText(overview);
        mTagline.setText(tagline);

        if (isFromDatase) {
            Bitmap bitmap = intent.getParcelableExtra("backdrop_photo");

            mPhoto.setImageBitmap(bitmap);
        } else {
            Picasso.with(getApplicationContext()).load(backdrop_url).into(mPhoto);
        }
    }

    private void configViews() {
        mPhoto = findViewById(R.id.backDropUrl);
        mOverview = findViewById(R.id.overview);
        mReleaseDate = findViewById(R.id.release_date);
        mTagline = findViewById(R.id.tagline);

    }
}
