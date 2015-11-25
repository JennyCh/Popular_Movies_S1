package com.example.jenny.popular_movies_s1;



import android.content.ActivityNotFoundException;
import android.content.ContentValues;

import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


import com.example.jenny.popular_movies_s1.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // ImageView imageView;
    //TextView titleView;
    //TextView overViewView;
    //TextView voteView;
    //TextView dateView;
    //List<Movie> movies;

    //MovieAdapter mMovieAdapter;

    String idArgument;

    Cursor reviewCursor;
    Cursor trailerCursor;

    ListView reviewListView;
    ListView trailerListView;

    private Uri mUri = null;
    //private int id;
    static final String DETAIL_URI = "URI";

    // String movieID;
    private static final int DETAIL_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    private static final String LOG_TAG = "DetailActivityFragment";

    public interface Callback{
        /*
        Callback for when an item has been selected
         */
        public void shareData(String str);
        public void saveUri(Uri uri);
        // public void onFirstLoad(Uri movieUri);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        //Log.v(LOG_TAG, "onCreateLoader " + mUri + "|");
        if(mUri!= null) {
        //    Log.v(LOG_TAG, "mUri ++" + mUri.toString());//clicked id mUri ++content://com.example.jenny.popular_movies_s1/movie/150540
            this.idArgument = mUri.getPathSegments().get(1); //if take off, then upper detail won't load
          //  Log.v(LOG_TAG, "RESET ARGUMENT else" + idArgument);
        }else{
         //   Log.v(LOG_TAG, "mUri ++IS NULL");
        }
        //if (null != mUri){
      //  Log.v(LOG_TAG, "GETTING CURSOR " + idArgument);
        if(idArgument != null ) {
            if (Integer.valueOf(idArgument) == 0) {
                this.idArgument = mUri.getPathSegments().get(1);
           //     Log.v(LOG_TAG, "RESET ARGUMENT if" + idArgument);
            }
        }else{
                    /*this.idArgument = mUri.getPathSegments().get(1);
                    Log.v(LOG_TAG, "RESET ARGUMENT else" + idArgument);*/
        }
            if (id == DETAIL_LOADER){



                    //  Log.v(LOG_TAG, "ID VALUE " + idArgument);
                    //  Log.v(LOG_TAG, "QUERY REVIEW");
       /*     this.reviewCursor = getContext().getContentResolver().query(MovieContract.Review.buildReviewID(Integer.valueOf(idArgument)), null, null, new String[]{idArgument}, null);
            // Log.v(LOG_TAG, "QUERY TRAILER");
            this.trailerCursor = getContext().getContentResolver().query(MovieContract.Trailer.buildTrailerID(Integer.valueOf(idArgument)), new String[]{MovieContract.Trailer.KEY}, null,new String[]{idArgument}, null);
*///Log.v(LOG_TAG, "QUERY " + idArgument);
                    return new CursorLoader(getActivity(), mUri, null, null, new String[]{String.valueOf(idArgument)},null);
               // }/*else {
                    //return null;
               // }*/
            }

        if(id == TRAILER_LOADER){
          //  Log.v(LOG_TAG, "GETTING TRAILER " + idArgument);
           // this.idArgument = mUri.getPathSegments().get(1);
            return new CursorLoader(getActivity(),MovieContract.Trailer.buildTrailerID(Integer.valueOf(idArgument)), new String[]{MovieContract.Trailer.KEY}, null, new String[]{idArgument}, null);

        }

        if(id == REVIEW_LOADER){
           // this.idArgument = mUri.getPathSegments().get(1);
           // Log.v(LOG_TAG, "GETTING REVIEW " + idArgument);
            return new CursorLoader(getActivity(),MovieContract.Review.buildReviewID(Integer.valueOf(idArgument)), null, null, new String[]{idArgument}, null);
        }

       return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        StringBuilder shareMessage = new StringBuilder();
       // Log.v(LOG_TAG, "Loader Finished");
            if(loader.getId() == DETAIL_LOADER){

             //   Log.v(LOG_TAG, "FINISHED LOADER");
                if (!data.moveToFirst()){
                    //    Log.v(LOG_TAG, "NO DATA LOADER");
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
              //  Log.v(LOG_TAG, "BEFORE " + idArgument + "|");

                    if(data.getPosition() == 0) {
                 //       Log.v(LOG_TAG, "POSITION " + data.getPosition());
                        getLoaderManager().restartLoader(TRAILER_LOADER,null,this );

                        getLoaderManager().restartLoader(REVIEW_LOADER,null,this );
                }

                shareMessage.append("Check out this movie: ");
                shareMessage.append(titleData);
                shareMessage.append("\n");

                //String result = titleData + " | " + overviewData + " | " + releaseDataDate + " | " + pathData + " | " + voteAverageData + " | " + voteCountData;

                // Log.v(LOG_TAG, "FAVORITE " + String.valueOf(favoriteData));
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
                    Picasso.with(getContext()).load(R.drawable.pink_heart).into(likeImage);
                } else{
                    Picasso.with(getContext()).load(R.drawable.what_imoj).into(likeImage);
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
                            Picasso.with(getContext()).load(R.drawable.what_imoj).into(likeImage);
                        } else{
                            favoriteValue = 1;
                            Picasso.with(getContext()).load(R.drawable.pink_heart).into(likeImage);
                        }

                        onIDChange(Integer.valueOf(idData)); //to spop refreshing to new element once clicked
                        updateValues.put(MovieContract.Movie.FAVORITE, favoriteValue);
                        getContext().getContentResolver().update(uri, updateValues, null, new String[]{idData});

                        // Log.v(LOG_TAG, "UPDATED FAVORITE");
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

            }

        if(loader.getId() == TRAILER_LOADER) {
            trailerCursor = data;
            if (trailerCursor == null) {
               // Log.v(LOG_TAG, "NO DATA TRAILER");
                shareMessage.append("\t");
                shareMessage.append("#PopularMovies_SP2");
                //Log.v(LOG_TAG , shareMessage.toString());
                ((Callback) getActivity()).shareData(shareMessage.toString());
                //return;
            } else {
               // Log.v(LOG_TAG, " DATA TRAILER");
                final List<Trailer> trailers = new ArrayList<>();
                int trailerNameColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.NAME);
                int trailerTypeColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.TYPE);
                int trailerSizeColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.SIZE);
                int trailerKeyColumn = trailerCursor.getColumnIndex(MovieContract.Trailer.KEY);


                int count = trailerCursor.getCount();
                    trailerCursor.moveToFirst();
                if(trailerCursor.getCount() > 0) {
                    for (int i = 0; i < count; i++) {
                        if (i == 0) {
                            shareMessage.append("https://www.youtube.com/watch?v=");
                            shareMessage.append(trailerCursor.getString(trailerKeyColumn));
                            shareMessage.append("\n");
                            shareMessage.append("#PopularMovies_SP2");
                            //  Log.v(LOG_TAG, shareMessage.toString());
                            ((Callback) getActivity()).shareData(shareMessage.toString());
                        }
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

                                //  Log.v("ON CLICK LINK YOUTUBE ", link.toString());
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
                }
                TrailerAdapter mTrailerAdapter = new TrailerAdapter(getActivity(), trailers);
                this.trailerListView.setAdapter(mTrailerAdapter);
                ListUtils.setDynamicHeight(trailerListView);
            }
        }
            if(loader.getId() == REVIEW_LOADER){
                reviewCursor = data;
                if (reviewCursor == null){
              //      Log.v(LOG_TAG, "NO DATA REVIEW");
                    //return;
                }else{
                //    Log.v(LOG_TAG, " DATA REVIEW");
                    List<Review> reviews = new ArrayList<>();
                    int reviewAuthorColumn = reviewCursor.getColumnIndex(MovieContract.Review.AUTHOR);
                    int reviewContentColumn = reviewCursor.getColumnIndex(MovieContract.Review.CONTENT);
                    int count = reviewCursor.getCount();
                    reviewCursor.moveToFirst();
                    if(reviewCursor.getCount() > 0) {
                        for (int i = 0; i < count; i++) {
                            reviews.add(new Review(reviewCursor.getString(reviewAuthorColumn), reviewCursor.getString(reviewContentColumn)));
                            reviewCursor.moveToNext();
                        }
                    }

                    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.review_item, reviews);
                    ReviewAdapter mReviewAdapter = new ReviewAdapter(getActivity(),reviews);
                    this.reviewListView.setAdapter(mReviewAdapter);
                    ListUtils.setDynamicHeight(reviewListView);

                }






        }

    }

    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putString("MURI", mUri.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if(savedInstanceState !=null) {
            this.mUri = Uri.parse(savedInstanceState.getString("MURI"));
            //((Callback)getActivity()).saveUri(mUri);
        }

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // loader.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();

       // Log.v(LOG_TAG, "CURSOR onResume");

        if(idArgument != null){
            //mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);

          //  Log.v(LOG_TAG, "CURSOR " + idArgument + "|");
            mUri = MovieContract.Movie.buildMovieID(Integer.valueOf(idArgument));

        }/*else {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this); //adding to restart the top part, since picks up only first element
        }*/
       // Log.v(LOG_TAG, "CURSOR POST Resume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(savedInstanceState != null){

            this.mUri = Uri.parse(savedInstanceState.getString("MURI"));
           // Log.v(LOG_TAG, "CHECKING INSTANCE STATE " + mUri);
        }else{
           // Log.v(LOG_TAG, "no INSTANCE STATE");
        }

       // Log.v(LOG_TAG, "CURSOR onCreateView");

        Bundle arguments = getArguments();
        if(arguments != null){
            //mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);

          //  Log.v(LOG_TAG, "ARGUMENTS EXIST");

            String idVal = arguments.getParcelable("MOVIEID");
            Uri uriPassed = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            if(idVal != null) {
             //   Log.v(LOG_TAG, "ARGUMENTS EXIST idVal" +  idVal);
                mUri = MovieContract.Movie.buildMovieID(Integer.valueOf(idVal));
             //   Log.v(LOG_TAG, "SET NEW mUri " + mUri.toString());
                if(Integer.valueOf(idVal) != 0) {

                    this.idArgument = idVal;
                  //  Log.v(LOG_TAG, "SET NEW idArgument " + idArgument);
                }
            }
            else if(uriPassed != null){
               // Log.v(LOG_TAG, "ARGUMENTS EXIST uriPasses" +  uriPassed.toString());
                mUri = uriPassed;
                idArgument = uriPassed.getPathSegments().get(1);
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this); //force reload for the top fragment
            }
            else if(mUri != null) {//TODO: mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
              ///  Log.v(LOG_TAG, "ARGUMENTS EXIST mUri" +  mUri.toString());
              /*
                //mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
                mUri = MovieContract.Movie.buildMovieID(0);*/
              //  Log.v(LOG_TAG, "DOING NOTHING RIGHT HERE -  BEWARE " + mUri.toString());
            }else{
               // Log.v(LOG_TAG, "NONE MATCHED");
            }

        }else{
          //  Log.v(LOG_TAG, "ASSIGNING 0 argument BEWARE");
            mUri = MovieContract.Movie.buildMovieID(0);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        reviewListView = (ListView) rootView.findViewById(R.id.reviewListView);
        trailerListView = (ListView) rootView.findViewById(R.id.trailerListView);
/*if(Integer.valueOf(mUri.getPathSegments().get(1)) != 0) {
    this.idArgument = mUri.getPathSegments().get(1);
}*/
         // Log.v(LOG_TAG, "ID VALUE --" + idArgument);

        return rootView;

    }

    void onIDChange(int id){

      //  Log.v(LOG_TAG, "ON ID CHANGE " + String.valueOf(id) + "|");

        Uri uri = mUri;
        Uri updateUri;
        if(null !=  uri){
            updateUri = MovieContract.Movie.buildMovieID(id);
            mUri = updateUri;
        //   Log.v(LOG_TAG, mUri.toString());
         //   Log.v(LOG_TAG, "RESTARTING LOADER IF " + mUri.toString());
            if(id != 0) {
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            }
        }else{
            updateUri = MovieContract.Movie.buildMovieID(id);
            mUri = updateUri;
          //  Log.v(LOG_TAG, "RESTARTING LOADER ELSE " + mUri.toString());
            if(id != 0) {
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            }
        }




    }


}