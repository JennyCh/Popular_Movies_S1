package com.example.jenny.popular_movies_s1;



import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.example.jenny.popular_movies_s1.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
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

    MovieAdapter mMovieAdapter;

    Cursor reviewCursor;
    Cursor trailerCursor;

    ListView reviewListView;
    ListView trailerListView;

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


        int movieId = data.getColumnIndex(MovieContract.Movie._ID);
        int overview =  data.getColumnIndex(MovieContract.Movie.OVERVIEW);
        int title = data.getColumnIndex(MovieContract.Movie.TITLE);
        int releaseDate = data.getColumnIndex(MovieContract.Movie.RELEASE_DATE);
        int path = data.getColumnIndex(MovieContract.Movie.POSTER_PATH);
        int voteAverage = data.getColumnIndex(MovieContract.Movie.VOTE_AVERAGE);
        int voteCount = data.getColumnIndex(MovieContract.Movie.VOTE_COUNT);
        final int favorite = data.getColumnIndex(MovieContract.Movie.FAVORITE);

        String overviewData = data.getString(overview);
        String titleData = data.getString(title);
        String releaseDataDate = data.getString(releaseDate);
        String pathData = data.getString(path);
        int voteAverageData = data.getInt(voteAverage);
        String voteCountData = data.getString(voteCount);
        //String favoriteData = data.getString(favorite);
        final String idData = data.getString(movieId);
         final int favoriteData = data.getInt(favorite);

        //String result = titleData + " | " + overviewData + " | " + releaseDataDate + " | " + pathData + " | " + voteAverageData + " | " + voteCountData;

        Log.v(LOG_TAG, "FAVORITE " + String.valueOf(favoriteData));
        TextView titleTextView = (TextView) getView().findViewById(R.id.detail_title);
        titleTextView.setText(titleData);

        TextView overviewTextView = (TextView) getView().findViewById(R.id.detail_overview);
        overviewTextView.setText(overviewData);

        TextView dateTextView = (TextView) getView().findViewById(R.id.detail_date);
        dateTextView.setText(releaseDataDate);

        ImageView pathImageView = (ImageView) getView().findViewById(R.id.detail_movie_poster);
        String baseUrl = "http://image.tmdb.org/t/p/w342";
        String url = baseUrl + pathData;
        Picasso.with(getContext()).load(url.toString()).into(pathImageView);

        final ImageView likeImage = (ImageView) getView().findViewById(R.id.like);
        if(favoriteData == 1){
            Picasso.with(getContext()).load(R.drawable.heart_blue).into(likeImage);
        } else{
            Picasso.with(getContext()).load(R.drawable.heart_love).into(likeImage);
        }
        likeImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri uri = MovieContract.Movie.buildMovieID(Integer.valueOf(idData));
                ContentValues updateValues = new ContentValues();
                //if favoriteData == 1 ? favoriteData = 0 : favoriteData = 1;
                int favoriteValue;
                if(favoriteData == 1){
                    favoriteValue = 0;
                    Picasso.with(getContext()).load(R.drawable.heart_love).into(likeImage);
                } else{
                    favoriteValue = 1;
                    Picasso.with(getContext()).load(R.drawable.heart_blue).into(likeImage);
                }


                updateValues.put(MovieContract.Movie.FAVORITE, favoriteValue);
                getContext().getContentResolver().update(uri, updateValues, null, new String[]{idData});
                Log.v(LOG_TAG, "UPDATED FAVORITE");
            }
        });


        int val =  voteAverageData;
        if (10 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star10));
        }
        if(9 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star9));
        } if(8 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star8));
        } if(7 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star7));
        } if(6 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star6));
        } if(5 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star5));
        } if(4 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star4));
        } if(3 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star3));
        } if(2 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star2));
        } if(1 <= val){
            Picasso.with(getContext()).load(R.drawable.yellow_star).into((ImageView) getView().findViewById(R.id.star1));
        }

        TextView voteNumTextView = (TextView) getView().findViewById(R.id.detail_voteNum);
        voteNumTextView.setText(voteCountData);

   /*     TextView favoriteTextView = (TextView) getView().findViewById(R.id.detail_favorite);
        favoriteTextView.setText(favoriteData);*/

        if (!reviewCursor.moveToFirst()){
            Log.v(LOG_TAG, "NO DATA REVIEW");
            return;
        }else{
            List<Review> reviews = new ArrayList<>();
            int reviewAuthorColumn = reviewCursor.getColumnIndex(MovieContract.Review.AUTHOR);
            int reviewContentColumn = reviewCursor.getColumnIndex(MovieContract.Review.CONTENT);
            int count = reviewCursor.getCount();

            for(int i = 0; i < count; i ++){
                reviews.add(new Review(reviewCursor.getString(reviewAuthorColumn),reviewCursor.getString(reviewContentColumn)));
                reviewCursor.moveToNext();
            }

            //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.review_item, reviews);
            ReviewAdapter mReviewAdapter = new ReviewAdapter(getActivity(),reviews);
            this.reviewListView.setAdapter(mReviewAdapter);

        }

        if (!trailerCursor.moveToFirst()){
            Log.v(LOG_TAG, "NO DATA TRAILER");
            return;
        }else{
            final List<Trailer> trailers = new ArrayList<>();
            int trailerNameColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.NAME);
            int trailerTypeColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.TYPE);
            int trailerSizeColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.SIZE);
            int trailerKeyColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.KEY);





            int count = trailerCursor.getCount();

            for(int i = 0; i < count; i ++){
                trailers.add(new Trailer(trailerCursor.getString(trailerNameColumn), 
                                            trailerCursor.getString(trailerSizeColumn), 
                                            trailerCursor.getString(trailerTypeColumn), 
                                            trailerCursor.getString(trailerKeyColumn)));

                //final String key = trailerCursor.getString(trailerKeyColumn);

                final StringBuilder link = new StringBuilder();
                //

                this.trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                         String key = trailers.get(position).getKey();

                        link.append("https://www.youtube.com/watch?v=");
                        link.append(key);
                        Log.v("ON CLICK LINK YOUTUBE ", link.toString());
                        try {
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.toString())));
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.toString()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.toString()));
                            startActivity(intent);
                        }
                        link.delete(0, link.length());
                    }
                });

                trailerCursor.moveToNext();
            }
            TrailerAdapter mTrailerAdapter = new TrailerAdapter(getActivity(), trailers);
            this.trailerListView.setAdapter(mTrailerAdapter);





        }



/*
        int author = reviewCursor.getColumnIndex(MovieContract.Review.AUTHOR);
        int content = reviewCursor.getColumnIndex(MovieContract.Review.CONTENT);

        String authorData = reviewCursor.getString(author);
        String contentData = reviewCursor.getString(content);

        TextView authorTextView = (TextView) getView().findViewById(R.id.detail_author1);
        TextView contentTextView = (TextView) getView().findViewById(R.id.detail_review1);

        authorTextView.setText(authorData);
        contentTextView.setText(contentData);*/


       /* if (!trailerCursor.moveToFirst()){
            Log.v(LOG_TAG, "NO DATA TRAILER");
            return;
        }

        int trailerName = trailerCursor.getColumnIndex(MovieContract.Trailer.KEY);
        Log.v(LOG_TAG, String.valueOf(trailerName));
        String nameTrailerData = trailerCursor.getString(trailerName);

        TextView nameTextView = (TextView) getView().findViewById(R.id.detail_trailerName1);
        nameTextView.setText(nameTrailerData);*/


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

        //mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewListView = (ListView) rootView.findViewById(R.id.reviewListView);
        trailerListView = (ListView) rootView.findViewById(R.id.trailerListView);



        //ListView trailerListView = (ListView) rootView.findViewById(R.id.trailerListView);
        //reviewListView.setAdapter(mMovieAdapter);



       return rootView;

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
