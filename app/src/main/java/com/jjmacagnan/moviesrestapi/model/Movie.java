package com.jjmacagnan.moviesrestapi.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Movie implements Serializable {


    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("vote_average")
    @Expose
    private Double vote_average;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_url")
    @Expose
    private String poster_url;

    @SerializedName("genres")
    @Expose
    private String[] genres;

    @SerializedName("backdrop_url")
    @Expose
    private String backdrop_url;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("release_date")
    @Expose
    private String release_date;

    @SerializedName("tagline")
    @Expose
    private String tagline;

    private Bitmap poster;

    private Bitmap backdrop_photo;

    private boolean isFromDatabase;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getVote_average() {
        return vote_average;
    }

    public void setVote_average(Double vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public void setPoster_url(String poster_url) {
        this.poster_url = poster_url;
    }

    public String[] getGenres() {
        return genres;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public String getBackdrop_url() {
        return backdrop_url;
    }

    public void setBackdrop_url(String backdrop_url) {
        this.backdrop_url = backdrop_url;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }

    public Bitmap getBackdrop_photo() {
        return backdrop_photo;
    }

    public void setBackdrop_photo(Bitmap backdrop_photo) {
        this.backdrop_photo = backdrop_photo;
    }

    public boolean isFromDatabase() {
        return isFromDatabase;
    }

    public void setFromDatabase(boolean fromDatabase) {
        isFromDatabase = fromDatabase;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {

        out.writeObject(id);
        out.writeObject(vote_average);
        out.writeObject(title);
        out.writeObject(poster_url);
        out.writeObject(genres);
        out.writeObject(backdrop_url);
        out.writeObject(overview);
        out.writeObject(release_date);
        out.writeObject(tagline);


        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte bitmapPosterBytes[] = byteStream.toByteArray();
        byte bitmapBackdropBytes[] = byteStream.toByteArray();

        if (poster != null) {
            poster.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        }

        if (backdrop_photo != null) {
            backdrop_photo.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        }

        out.write(bitmapBackdropBytes, 0, bitmapBackdropBytes.length);
        out.write(bitmapPosterBytes, 0, bitmapPosterBytes.length);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {

        id = (Integer) in.readObject();
        vote_average = (Double) in.readObject();
        title = (String) in.readObject();
        poster_url = (String) in.readObject();
        genres = (String[]) in.readObject();
        backdrop_url = (String) in.readObject();
        overview = (String) in.readObject();
        release_date = (String) in.readObject();
        tagline = (String) in.readObject();


        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1)
            byteStream.write(b);
        byte bitmapPosterBytes[] = byteStream.toByteArray();
        byte bitmapBackdropBytes[] = byteStream.toByteArray();

        poster = BitmapFactory.decodeByteArray(bitmapPosterBytes, 0,
                bitmapPosterBytes.length);

        backdrop_photo = BitmapFactory.decodeByteArray(bitmapBackdropBytes, 0,
                bitmapBackdropBytes.length);
    }
}