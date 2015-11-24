package com.example.jenny.popular_movies_s1.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import java.util.List;

/**
 * Created by Jenny on 11/12/2015.
 */
public class MovieService extends IntentService {

    public static final String LOCATION_QUERY_EXTRA = "lqe";
   // private String sortType;


    private Context mContext;
    private String sortType;

    public MovieService(){super("Movie");
           }


    public MovieService(String name) {
        super(name);
    }


    static public class AlarmReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, MovieService.class);
           // sendIntent.putExtra(LOCATION_QUERY_EXTRA, intent.getStringExtra(LOCATION_QUERY_EXTRA));
            context.startService(sendIntent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mContext = getApplicationContext();
        String locationQuery = intent.getStringExtra(LOCATION_QUERY_EXTRA);

       /* HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        this.sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default));


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://api.themoviedb.org/3/discover/movie?sort_by=");
        stringBuilder.append(sortType);
        stringBuilder.append(".desc&api_key=25fce2cd7e460dfabda689d0ebfcf69f");

        Log.v(LOG_TAG, "onHandleIntent " + stringBuilder.toString());

        try{
            URL url = new URL(stringBuilder.toString());

            urlConnection  =  (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect(); // cannot be done on the main thread

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if(inputStream == null){
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }
            if(buffer.length() == 0){
                return;
            }
            movieJsonStr = buffer.toString();

            Log.v(LOG_TAG, "BEGIN INSERT");
            Log.v(LOG_TAG, movieJsonStr);
            getMovieDataFromJson(movieJsonStr);

        }catch(IOException e){
            Log.e(LOG_TAG, "Error", e);
            return;
        }catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(),e);
            e.printStackTrace();
        }
        finally {
            if(urlConnection  != null){
                urlConnection.disconnect();
            }
            if(reader  != null){
                try{
                    reader.close();
                }catch(final IOException e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;*/
    }


   /* private void getMovieDataFromJson (String movieJsonString) throws JSONException{

        final String RESULTS = "results";
        final String ID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OBJECT = "object";
        final String PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String VOTE_COUNT = "vote_count";

        boolean exists = false;
        Integer id = null;

        *//*
        get all the movie ids from database
         *//*
        setArrayListWithExistingMovieIDs();


        try {
            JSONObject moviesObj = new JSONObject(movieJsonString);
            JSONArray jsonArray = moviesObj.getJSONArray(RESULTS);

            Vector<ContentValues> movieVector = new Vector<>(jsonArray.length());



            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);
                id = object.getInt(ID);

                if (!existingMovieIDList.contains(id)) {
                    String title = object.getString(ORIGINAL_TITLE);
                    String path = object.getString(PATH);
                    String overview = object.getString(OVERVIEW);
                    double vote = object.getDouble(VOTE_AVERAGE);
                    String releaseDate = object.getString(RELEASE_DATE);
                    int voteCount = object.getInt(VOTE_COUNT);
                    int sort;

                    sort = getMovieSortType(sortType);

                    ContentValues movieValues = createContentValues_Movie_ForInsert(id, title, path, overview, vote, releaseDate, voteCount, sort);
                    movieVector.add(movieValues);

                    DownloadJsonReviewTask downloadReview = new DownloadJsonReviewTask(mContext);
                    downloadReview.execute(String.valueOf(id));

                    DownloadJsonTrailerTask trailerReview = new DownloadJsonTrailerTask(mContext);
                    trailerReview.execute(String.valueOf(id));

                    Log.v(LOG_TAG, movieValues.toString());
                }

            }


            Log.v(LOG_TAG, "BEFORE INSERT");
            Log.v(LOG_TAG, "VECTOR SIZE " + String.valueOf(movieVector.size()));
            int inserted = 0;
            if (movieVector.size() > 0) {
                ContentValues[] movieArray = new ContentValues[movieVector.size()];
                movieVector.toArray(movieArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.Movie.CONTENT_URI, movieArray);
            }
            Log.v(LOG_TAG, "Complete " + inserted + " inserted");



        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private int getMovieSortType(String sortType){
        if ("popularity".equals(sortType)) {
            return 1;
        } else if ("vote_average".equals(sortType)) {
            return 2;
        }

        return -1;
    }

    private void setArrayListWithExistingMovieIDs(){
        try {
            getMovieIDsPresentInDBcursor = mContext.getContentResolver().query(
                    MovieContract.Movie.CONTENT_URI,
                    new String[]{MovieContract.Movie._ID},
                    null,
                    null,
                    MovieContract.Movie._ID
            );

            Log.v(LOG_TAG, "Number of IDs returned " + String.valueOf(getMovieIDsPresentInDBcursor.getCount()));
            int idColumn = getMovieIDsPresentInDBcursor.getColumnIndex(MovieContract.Movie._ID);
            existingMovieIDList = new ArrayList<>();


            while (getMovieIDsPresentInDBcursor.moveToNext()) {
                existingMovieIDList.add(getMovieIDsPresentInDBcursor.getInt(idColumn));
                Log.v(LOG_TAG,"movies in DB " + existingMovieIDList.size() + "  " + String.valueOf(getMovieIDsPresentInDBcursor.getInt(idColumn)));
            }
        }finally {
           // getMovieIDsPresentInDBcursor.close();
        }
    }

    private ContentValues createContentValues_Movie_ForInsert(int id,String title,String path,String overview,double vote,String releaseDate, int voteCount, int sort){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.Movie._ID, id);
        movieValues.put(MovieContract.Movie.TITLE, title);
        movieValues.put(MovieContract.Movie.POSTER_PATH, path);
        movieValues.put(MovieContract.Movie.OVERVIEW, overview);
        movieValues.put(MovieContract.Movie.VOTE_AVERAGE, vote);
        movieValues.put(MovieContract.Movie.RELEASE_DATE, releaseDate);
        movieValues.put(MovieContract.Movie.VOTE_COUNT, voteCount);
        movieValues.put(MovieContract.Movie.SORT_TYPE, sort);

        return movieValues;
    }*/
}
