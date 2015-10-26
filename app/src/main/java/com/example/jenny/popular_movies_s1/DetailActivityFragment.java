package com.example.jenny.popular_movies_s1;



import android.content.Context;

import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.example.jenny.popular_movies_s1.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    ImageView imageView;
    TextView titleView;
    TextView overViewView;
    TextView voteView;
    TextView dateView;
    List<Movie> movies;

    Cursor reviewCursor;
    Cursor trailerCursor;

    String movieID;
    private static final int DETAIL_LOADER = 0;
    private static final String LOG_TAG = "DetailActivityFragment";

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.Movie.TABLE_NAME+ "."+ MovieContract.Movie._ID,
            MovieContract.Movie.TITLE,
            MovieContract.Movie.OVERVIEW
    };

    public DetailActivityFragment() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.v(LOG_TAG, "CREATE LOADER");
        Intent intent = getActivity().getIntent();

        if(intent == null){
            Log.v(LOG_TAG, "EMPTY INTENT");
            return null;
        }
        Log.v(LOG_TAG, intent.getData().toString());

        int idParameter =  Integer.parseInt(intent.getData().getPathSegments().get(1));
        String idStr = String.valueOf(idParameter);

        Log.v(LOG_TAG, "QUERY REVIEW");
        this.reviewCursor = getContext().getContentResolver().query(MovieContract.Review.buildReviewID(idParameter), null, null, new String[]{idStr}, null);
        Log.v(LOG_TAG, "QUERY TRAILER");
        this.trailerCursor = getContext().getContentResolver().query(MovieContract.Trailer.buildTrailerID(idParameter), new String[]{MovieContract.Trailer.KEY}, null,new String[]{idStr}, null);

        Log.v(LOG_TAG, idStr);
        return new CursorLoader(getActivity(), intent.getData(), MOVIE_COLUMNS, null, new String[]{idStr},null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "FINISHED LOADER");
        if (!data.moveToFirst()){
            Log.v(LOG_TAG, "NO DATA LOADER");
            return;
        }



        int overview =  data.getColumnIndex(MovieContract.Movie.OVERVIEW);
        int title = data.getColumnIndex(MovieContract.Movie.TITLE);
        int releaseDate = data.getColumnIndex(MovieContract.Movie.RELEASE_DATE);
        int path = data.getColumnIndex(MovieContract.Movie.POSTER_PATH);
        int voteAverage = data.getColumnIndex(MovieContract.Movie.VOTE_AVERAGE);
        int voteCount = data.getColumnIndex(MovieContract.Movie.VOTE_COUNT);
        int favorite = data.getColumnIndex(MovieContract.Movie.FAVORITE);

        String overviewData = data.getString(overview);
        String titleData = data.getString(title);
        String releaseDataDate = data.getString(releaseDate);
        String pathData = data.getString(path);
        String voteAverageData = data.getString(voteAverage);
        String voteCountData = data.getString(voteCount);
        String favoriteData = data.getString(favorite);

        //String result = titleData + " | " + overviewData + " | " + releaseDataDate + " | " + pathData + " | " + voteAverageData + " | " + voteCountData;

        //Log.v(LOG_TAG, overviewData);
        TextView titleTextView = (TextView) getView().findViewById(R.id.detail_title);
        titleTextView.setText(titleData);

        TextView overviewTextView = (TextView) getView().findViewById(R.id.detail_overview);
        overviewTextView.setText(overviewData);

        TextView dateTextView = (TextView) getView().findViewById(R.id.detail_date);
        dateTextView.setText(releaseDataDate);

        TextView pathTextView = (TextView) getView().findViewById(R.id.detail_path);
        pathTextView.setText(pathData);

        TextView voteTextView = (TextView) getView().findViewById(R.id.detail_vote);
        voteTextView.setText(voteAverageData);

        TextView voteNumTextView = (TextView) getView().findViewById(R.id.detail_voteNum);
        voteNumTextView.setText(voteCountData);

        TextView favoriteTextView = (TextView) getView().findViewById(R.id.detail_favorite);
        favoriteTextView.setText(favoriteData);

        if (!reviewCursor.moveToFirst()){
            Log.v(LOG_TAG, "NO DATA REVIEW");
            return;
        }

        int author = reviewCursor.getColumnIndex(MovieContract.Review.AUTHOR);
        int content = reviewCursor.getColumnIndex(MovieContract.Review.CONTENT);

        String authorData = reviewCursor.getString(author);
        String contentData = reviewCursor.getString(content);

        TextView authorTextView = (TextView) getView().findViewById(R.id.detail_author1);
        TextView contentTextView = (TextView) getView().findViewById(R.id.detail_review1);

        authorTextView.setText(authorData);
        contentTextView.setText(contentData);


        if (!trailerCursor.moveToFirst()){
            Log.v(LOG_TAG, "NO DATA TRAILER");
            return;
        }

        int trailerName = trailerCursor.getColumnIndex(MovieContract.Trailer.KEY);
        Log.v(LOG_TAG, String.valueOf(trailerName));
        String nameTrailerData = trailerCursor.getString(trailerName);

        TextView nameTextView = (TextView) getView().findViewById(R.id.detail_trailerName1);
        nameTextView.setText(nameTrailerData);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



       return inflater.inflate(R.layout.fragment_detail, container, false);

/*
        String path = intent.getStringExtra("PATH");

        String title = intent.getStringExtra("TITLE");
        String overview = intent.getStringExtra("OVERVIEW");
        String date = intent.getStringExtra("DATE");
        Double vote = intent.getDoubleExtra("VOTE", -1);*/




        /*String link = path;
        DownloadSingleImage asyncDownload = new DownloadSingleImage((ImageView) rootView.findViewById(R.id.detail_image));
        //Log.v("LINK", link.toString());
        asyncDownload.execute(link);
        titleView = (TextView) rootView.findViewById(R.id.detail_title);
        overViewView = (TextView) rootView.findViewById(R.id.detail_overview);
        dateView = (TextView) rootView.findViewById(R.id.detail_date);
        voteView = (TextView) rootView.findViewById(R.id.detail_vote);*/
   // }


       /* titleView.setText(title);
        overViewView.setText(overview);
        dateView.setText(date);
        voteView.setText(String.valueOf(vote));*/
        //}

        //return rootView;
    }
/*
    public class DownloadSingleImage extends AsyncTask<String,Void,Bitmap> {
        private ImageView btmImage;
private String url;
        public DownloadSingleImage(ImageView btmImage){
            this.btmImage = btmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            url = urls[0];
            Bitmap image = null;
            //Log.v("URL for iamge ", url);
            try{
                InputStream in = new URL(url).openStream();
                image = BitmapFactory.decodeStream(in);

            }catch(Exception e){
                e.printStackTrace();

            }

            return image;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap != null) {
                btmImage.setImageBitmap(bitmap);
            }else{
                btmImage.setLayoutParams(new LinearLayout.LayoutParams(342, 460));
                btmImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                btmImage.setPadding(15, 15, 15, 15);
                Picasso.with(getContext()).load(url.toString()).into(btmImage);
            }
        }
    }

*/
}
