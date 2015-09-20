package com.example.jenny.popular_movies_s1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jenny on 8/16/2015.
 */
public class Movie implements Parcelable {

    private String title;
    private String path;
    private String overview;
    private String releaseDate;
    private double vote;

    public Movie(String title, String overview, double vote, String releaseDate, String path) {
        this.title = title;
        this.overview = overview;
        this.vote = vote;
        this.releaseDate = releaseDate;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVote() {
        return vote;
    }

    protected Movie(Parcel source){
        this.title = source.readString();
        this.path = source.readString();
        this.overview = source.readString();
        this.releaseDate = source.readString();
        this.vote = source.readDouble();

    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeSerializable(path);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeDouble(vote);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", vote=" + vote +
                '}';
    }
}
